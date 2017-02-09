package bn.blaszczyk.roseapp.controller;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.*;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.impl.SessionImpl;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.RoseException;
import bn.blaszczyk.roseapp.tools.EntityUtils;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.Messenger;
import bn.blaszczyk.roseapp.view.tools.ProgressDialog;

import static bn.blaszczyk.roseapp.tools.Preferences.*;

public class HibernateController implements ModelController {
	
	private static final Logger LOGGER = Logger.getLogger(HibernateController.class);

	private static final String KEY_URL = "hibernate.connection.url";
	private static final String KEY_USER = "hibernate.connection.username";
	private static final String KEY_PW = "hibernate.connection.password";
	
	private final SessionFactory sessionFactory;
	private Session session;

	private final Map<Class<?>,List<Readable>> entityLists = new HashMap<>();
	private final Set<Writable> changedEntitys = new LinkedHashSet<>();
	private final String dbFullUrl;
	private boolean connected = false;
	private Messenger messenger;
	private Timer timer = new Timer(5000, e -> checkConnection(e));
	
	public HibernateController()
	{
		String dburl = getStringValue(DB_HOST,null);
		String dbport = getStringValue(DB_PORT,null);
		String dbname = getStringValue(DB_NAME,null);
		String dbuser = getStringValue(DB_USER,null);
		String dbpassword = getStringValue(DB_PASSWORD,null);
		
		boolean fetchOnStart = getBooleanValue(FETCH_ON_START, false);

		Configuration configuration = new AnnotationConfiguration().configure();
		if(dburl != null && dbport != null && dbname != null)
			configuration.setProperty(KEY_URL, String.format("jdbc:mysql://%s:%s/%s",dburl,dbport,dbname));
		if(dbuser != null)
			configuration.setProperty(KEY_USER, dbuser);
		if(dbpassword != null)
			configuration.setProperty(KEY_PW, dbpassword);
		sessionFactory = configuration.buildSessionFactory();
		
		dbFullUrl = configuration.getProperty(KEY_URL);
		
		for(Class<? extends Readable> type : TypeManager.getEntityClasses())
			entityLists.put(type, new ArrayList<>());
		if(fetchOnStart)
			loadEntities();
		timer.setInitialDelay(1000);
		timer.start();
	}

	private Session getSession()
	{
		if(session == null || !session.isOpen())
			session = sessionFactory.openSession();
		return session;
	}

	@Override
	public void delete(Writable entity) throws RoseException
	{
		if(entity == null)
			return;
		LOGGER.warn("delete Entity:\r\n" + EntityUtils.toStringFull(entity));
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
		try
		{
			Session sesson = getSession();
			sesson.beginTransaction();
			sesson.delete(entity);
			sesson.getTransaction().commit();
			getEntites(TypeManager.convertType(entity.getClass())).remove(entity);
		}
		catch(HibernateException e)
		{
			throw new RoseException("Error deleting " + entity, e);
		}
	}

	@Override
	public <T extends Readable> T createNew(Class<T> type) throws RoseException
	{
		try
		{
			T entity = type.newInstance();
			Session session = getSession();
			session.beginTransaction();
				entity.setId((Integer) session.save(entity));
			session.getTransaction().commit();
			getEntites(type).add(entity);
			LOGGER.info("new entity: " + EntityUtils.toStringPrimitives(entity));
			return entity;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new RoseException("Unable to create new " + type.getName(), e);
		}
	}
	
	@Override
	public Writable createCopy(Writable entity) throws RoseException
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
	public void commit() throws RoseException
	{
		Session session = getSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			boolean hasNew = false;
			for(Writable entity : changedEntitys)
			{
				if(entity == null)
					continue;
				if(entity.getId() < 0)
				{
					LOGGER.debug("Saving new entity:\r\n" + EntityUtils.toStringFull(entity));
					Integer id = (Integer) session.save(entity);
					entity.setId(id);
					hasNew = true;
				}
			}
			if(hasNew)
			{
				transaction.commit();
				transaction = session.beginTransaction();
			}
			for(Writable entity : changedEntitys)
			{
				LOGGER.debug("Updating entity:\r\n" + EntityUtils.toStringFull(entity));
				session.update(entity);
			}
			transaction.commit();
			changedEntitys.clear();
		}
		catch(HibernateException e)
		{
			throw new RoseException("Error saving or updating entities to database",e);
		}
	}
	
	@Override
	public void closeSession()
	{
		timer.stop();
		if(session != null)
			session.close();
		session = null;
	}
	
	private void loadEntities(Class<?> type) throws RoseException
	{
		LOGGER.debug("start loading entities: " + type.getName());
		try
		{
			Session session = getSession();
			Criteria criteria = session.createCriteria(type);
			List<?> list = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			List<Readable> entities = entityLists.get(type);
			entities.clear();
			for(Object o : list)
			{
				entities.add((Readable) o);
			}
		}
		catch(HibernateException e)
		{
			throw new RoseException("Error loading Entities: " + type.getName(), e);
		}
		LOGGER.debug("successfully finished loading entities: " + type.getName());
	}

	private void checkConnection(ActionEvent e)
	{
		String title = Messages.get("Connection to Database: ") + dbFullUrl;
		String message;
		boolean wasConnected = connected;
		if(session instanceof SessionImpl)
		{
			try
			{
				connected = ((SessionImpl)session).connection().isValid(10);
			}
			catch (HibernateException | SQLException e1)
			{
				if(wasConnected)
				{
					messenger.error(e1, "connection lost");
					LOGGER.error("No connection to " + dbFullUrl, e1);
				}
				connected = false;
			}
			message = Messages.get( connected ? "connected" : "disconnected" );
			if(session.isDirty())
				message = message + ", " + Messages.get("dirty");
		}
		else
			message = Messages.get("unknown");
		if(messenger != null)
			messenger.info(message, title);
		LOGGER.debug(title + " - " + message);
	}

	private void loadEntities()
	{
		ProgressDialog dialog = new ProgressDialog(null,TypeManager.getEntityClasses().size(),Messages.get("initialize"),null, true);
		dialog.showDialog();
		dialog.appendInfo(Messages.get("initialize database connection"));
		for(Class<?> type : TypeManager.getEntityClasses())
		{
			entityLists.put(type, new ArrayList<>());
		}
		try{
			for(Class<?> type : TypeManager.getEntityClasses())
			{
				dialog.incrementValue();
				dialog.appendInfo( String.format("\n%s %s", Messages.get("loading"), Messages.get(type.getSimpleName() + "s") ) );
				loadEntities(type);
			}
			dialog.disposeDialog();
		}
		catch(RoseException e)
		{
			LOGGER.error("Error loading entities",e);
			dialog.appendException(e);
			dialog.appendInfo("\nconnection error");
			dialog.setFinished();
		}
	}

	@Override
	public List<Readable> getEntites(Class<?> type)
	{
		List<Readable> entities = entityLists.get(type);
		if(entities.isEmpty())
		{
			try
			{
				loadEntities(type);
			}
			catch(RoseException e)
			{
				String message = "Error fetching entities from Database";
				LOGGER.error(message, e);
				if(messenger != null)
					messenger.error(e, message);
			}
		}
		return entities;
	}

	@Override
	public void update(Writable... entities)
	{
		for(Writable entity : entities)
		{
			if(entity == null)
				return;
			if(entity.getId() < 0)
			{
				List<Readable> list = getEntites(entity.getClass());
				if(list != null)
					list.add(entity);
			}
			changedEntitys.add(entity);
		}
	}

	@Override
	public void rollback()
	{
		changedEntitys.clear();
	}

	@Override
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}

}
