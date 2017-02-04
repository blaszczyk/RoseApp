package bn.blaszczyk.roseapp.controller;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.tools.ProgressDialog;

import static bn.blaszczyk.roseapp.tools.Preferences.*;

public class HibernateController implements ModelController {

	private static final String KEY_URL = "hibernate.connection.url";
	private static final String KEY_USER = "hibernate.connection.username";
	private static final String KEY_PW = "hibernate.connection.password";
	
	private final SessionFactory sessionFactory;
	private Session session;

	private final Map<Class<?>,List<Readable>> entityLists = new HashMap<>();
	private final Set<Writable> changedEntitys = new HashSet<>();
	
	public HibernateController()
	{
		String dburl = getStringValue(DB_HOST,null);
		String dbport = getStringValue(DB_PORT,null);
		String dbname = getStringValue(DB_NAME,null);
		String dbuser = getStringValue(DB_USER,null);
		String dbpassword = getStringValue(DB_PASSWORD,null);

		Configuration configuration = new AnnotationConfiguration().configure();
		if(dburl != null && dbport != null && dbname != null)
			configuration.setProperty(KEY_URL, String.format("jdbc:mysql://%s:%s/%s",dburl,dbport,dbname));
		if(dbuser != null)
			configuration.setProperty(KEY_USER, dbuser);
		if(dbpassword != null)
			configuration.setProperty(KEY_PW, dbpassword);
		sessionFactory = configuration.buildSessionFactory();
	}
	
	private Session getSession()
	{
		if(session == null || !session.isOpen())
			session = sessionFactory.openSession();
		return session;
	}

	@Override
	public void setField(Writable entity, int index, Object value)
	{
		changedEntitys.add(entity);
		entity.setField(index, value);
	}

	@Override
	public void setEntityField(Writable entity, int index, Writable value)
	{
		changedEntitys.add(entity);
		changedEntitys.add(value);
		entity.setEntity( index, value);
	}
	
	@Override
	public void addEntityField(Writable entity, int index, Writable value)
	{
		changedEntitys.add(entity);
		changedEntitys.add(value);
		entity.addEntity( index, value);
	}
	
	@Override
	public void delete(Writable entity)
	{
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			if(entity.getRelationType(i).isSecondMany())
			{
				Set<? extends Readable> set = new TreeSet<>( entity.getEntityValueMany(i));
				for(Readable subEntity : set)
				{
					changedEntitys.add((Writable) subEntity);
					entity.removeEntity(i, (Writable) subEntity);
				}
			}
			else
			{
				changedEntitys.add((Writable) entity.getEntityValueOne(i));
				entity.setEntity(i, null);
			}
		}
		commit();
		Session sesson = getSession();
		sesson.beginTransaction();
		sesson.delete(entity);
		sesson.getTransaction().commit();
		entityLists.get(entity.getClass()).remove(entity);
	}

	@Override
	public <T extends Readable> T createNew(Class<T> type)
	{
		try
		{
			T entity = type.newInstance();
			Session session = getSession();
			session.beginTransaction();
				entity.setId((Integer) session.save(entity));
			session.getTransaction().commit();
			entityLists.get(type).add(entity);
			return entity;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Writable createCopy(Writable entity)
	{
		Writable copy = createNew(entity.getClass());
		for(int i = 0; i < copy.getFieldCount(); i++)
			copy.setField( i, copy.getFieldValue(i));
		for(int i = 0; i < copy.getEntityCount(); i++)
			switch(copy.getRelationType(i))
			{
			case ONETOONE:
//				Writable subCopy = createCopy( (Writable) copy.getEntityValue(i) );
//				copy.setEntity( i, subCopy );
				break;
			case ONETOMANY:
				for( Readable o : copy.getEntityValueMany(i) )
					copy.addEntity( i, createCopy((Writable) o));
				break;
			case MANYTOONE:
				copy.setEntity(i, (Writable) copy.getEntityValueOne(i));
				break;
			case MANYTOMANY:
				break;
			}
		return copy;
	}
	
	@Override
	public void commit()
	{
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		for(Writable entity : changedEntitys)
		{
			if(entity == null)
				continue;
			if(entity.getId() < 0)
			{
				Integer id = (Integer) session.save(entity);
				entity.setId(id);
			}
			else
				session.update(entity);
		}
		transaction.commit();
		changedEntitys.clear();
	}
	
	@Override
	public void closeSession()
	{
		if(session != null)
			session.close();
		session = null;
	}

	@Override
	public void loadEntities()
	{
		ProgressDialog dialog = new ProgressDialog(null,TypeManager.getEntityClasses().size(),Messages.get("initialize"),null, true);
		dialog.showDialog();
		dialog.appendInfo(Messages.get("initialize database connection"));
		Session session = getSession();
		for(Class<?> type : TypeManager.getEntityClasses())
		{
			entityLists.put(type, new ArrayList<>());
		}
		try{
			for(Class<?> type : TypeManager.getEntityClasses())
			{
				dialog.incrementValue();
				dialog.appendInfo( String.format("\n%s %s", Messages.get("loading"), Messages.get(type.getSimpleName() + "s") ) );
				Criteria criteria = session.createCriteria(type);
				List<?> list = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
				List<Readable> entities = entityLists.get(type);
				for(Object o : list)
				{
					((Writable)o).resetSets();
					entities.add((Readable) o);
				}
				dialog.incrementValue();
			}
			connectEntities();
			dialog.disposeDialog();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			dialog.appendException(e);
			dialog.appendInfo("\nconnection error");
			dialog.setFinished();
		}
	}
	
	private void connectEntities()
	{
		for(Class<?> type : TypeManager.getEntityClasses())
		{
			List<Readable> entities = entityLists.get(type);			
			if( !entities.isEmpty() && entities.get(0) instanceof Writable)
			for(Readable entity : entities)
			{
				for(int i = 0; i < entity.getEntityCount(); i++)
				{
					if(!entity.getRelationType(i).isSecondMany())
					{
						Readable oldEntity = entity.getEntityValueOne(i);
						if(oldEntity == null)
							continue;
						Class<?> fieldType = TypeManager.convertType(oldEntity.getClass());
						int nIndex = entityLists.get(fieldType).indexOf(oldEntity);
						if(nIndex >= 0)
							((Writable)entity).setEntity(i, (Writable) entityLists.get(oldEntity.getClass()).get(nIndex) );
					}
				}
			}
		}
		changedEntitys.clear();
	}

	@Override
	public void removeEntityField(Writable entity, int index, Writable value)
	{
		changedEntitys.add(entity);
		changedEntitys.add(value);
		entity.removeEntity( index, value);
	}

	@Override
	public List<Readable> getAllEntites(Class<?> type)
	{
		if(!entityLists.containsKey(type))
			entityLists.put(type, new ArrayList<>());
		return entityLists.get(type);
	}

	@Override
	public void register(Writable entity)
	{
		if(entity == null)
			return;
		if(entity.getId() < 0)
		{
			List<Readable> list = entityLists.get(entity.getClass());
			if(list != null)
				list.add(entity);
		}
		changedEntitys.add(entity);
	}

}
