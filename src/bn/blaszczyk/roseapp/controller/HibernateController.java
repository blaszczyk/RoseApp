package bn.blaszczyk.roseapp.controller;

import java.util.*;

import javax.swing.JOptionPane;

import org.hibernate.*;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.JDBCConnectionException;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.TypeManager;

import static bn.blaszczyk.roseapp.tools.Preferences.*;

public class HibernateController implements ModelController {

	private final SessionFactory sessionFactory;

	private final Map<Class<?>,List<Readable>> entityLists = new HashMap<>();
	private final Set<Writable> changedEntitys = new HashSet<>();

	private static final String KEY_URL = "hibernate.connection.url";
	private static final String KEY_USER = "hibernate.connection.username";
	private static final String KEY_PW = "hibernate.connection.password";
	
	public HibernateController()
	{
		String dburl = getStringValue(DB_HOST,null);
		String dbport = getStringValue(DB_PORT,null);
		String dbname = getStringValue(DB_NAME,null);
		String dbuser = getStringValue(DB_USER,null);
		String dbpassword = getStringValue(DB_PASSWORD,null);
		
		Configuration cfg = new AnnotationConfiguration().configure();
		if(dburl != null && dbport != null && dbname != null)
			cfg.setProperty(KEY_URL, String.format("jdbc:mysql://%s:%s/%s",dburl,dbport,dbname));
		if(dbuser != null)
			cfg.setProperty(KEY_USER, dbuser);
		if(dbpassword != null)
			cfg.setProperty(KEY_PW, dbpassword);
		
		sessionFactory = cfg.buildSessionFactory();
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
		if(value !=  null && !(value instanceof Enum))
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
		Session sesson = sessionFactory.openSession();
		sesson.beginTransaction();
		sesson.delete(entity);
		sesson.getTransaction().commit();
		sesson.close();		
		entityLists.get(entity.getClass()).remove(entity);
//		for(int i = 0; i < entity.getEntityCount(); i++)
//			if(entity.getRelationType(i).equals(RelationType.ONETOONE) && entity.getEntityValue(i) instanceof Writable)
//				delete((Writable) entity.getEntityValue(i));
	}
	

	@Override
	public Writable createNew(Class<?> type)
	{
		Writable entity;
		try
		{
			entity = (Writable) type.newInstance();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
				entity.setId((Integer) session.save(entity));
			session.getTransaction().commit();
			session.close();
			entityLists.get(type).add(entity);
			return entity;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Writable createCopy(Writable entity)
	{
		Writable copy = createNew((Class<Writable>) entity.getClass());
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
				for( Object o :  ((Set<?>) copy.getEntityValue(i)).toArray())
					copy.addEntity( i, createCopy((Writable) o));
				break;
			case MANYTOONE:
				copy.setEntity(i, (Writable) copy.getEntityValue(i));
				break;
			case MANYTOMANY:
				break;
			}
		return copy;
	}
	
	@Override
	public void commit()
	{
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		for(Writable entity : changedEntitys)
		{
			if(entity.getId() < 0)
			{
				Integer id = (Integer) session.save(entity);
				entity.setId(id);
			}
			else
				session.update(entity);
		}
		transaction.commit();
		session.close();
		changedEntitys.clear();
	}

	@Override
	public void loadEntities()
	{
		Session session = sessionFactory.openSession();
		try{
			for(Class<?> type : TypeManager.getEntityClasses())
			{
				List<Readable> entities = new ArrayList<>();
				entityLists.put(type, entities);
					List<?> list = session.createCriteria(type).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
					for(Object o : list)
						entities.add((Readable) o);
			}
			connectEntities();
		}
		catch(JDBCConnectionException e)
		{
			JOptionPane.showMessageDialog(null, Messages.get("Unable to connect to database"), Messages.get("Connection Error"), JOptionPane.ERROR_MESSAGE);
		}
		session.close();
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
						Readable oldEntity = (Readable) entity.getEntityValue(i);
						if(oldEntity == null)
							continue;
						int nIndex = entityLists.get(oldEntity.getClass()).indexOf(oldEntity);
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
		List<Readable> list = entityLists.get(entity.getClass());
		if(list != null)
			list.add(entity);
		changedEntitys.add(entity);
	}
		

}
