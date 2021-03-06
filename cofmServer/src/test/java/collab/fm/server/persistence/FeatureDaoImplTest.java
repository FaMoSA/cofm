package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.junit.*;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Model;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;
import static org.junit.Assert.*;
public class FeatureDaoImplTest {

	static Logger logger = Logger.getLogger(FeatureDaoImplTest.class);
	
	private static FeatureDao dao = DaoUtil.getFeatureDao();
	private static Model m;
	@BeforeClass
	public static void beginSession() {
		HibernateUtil.getCurrentSession().beginTransaction();
		m = new Model();
		m.voteName("hahahaha domain", true, 9L);
		try {
			m = DaoUtil.getModelDao().save(m);
		} catch (BeanPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void closeSession() {
		HibernateUtil.getCurrentSession().getTransaction().commit();
	}
	
	@Test
	public void testSaveChineseCharacters() {
		Feature feature = new Feature();
		feature.vote(true, 1L);
		feature.voteOptionality(false, 1L);
		feature.voteName("中文1", true, 3L, m.getId());
		feature.voteDescription("汉字，，，，，，，", true, 4L, m.getId());
		feature.voteName("中文1", false, 4L, m.getId());
		m.addFeature(feature);
		try {
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			logger.debug("Feature = " + feature.toString());
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertEquals("Shouldn't reach here", "");
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSave() {
		Feature feature = new Feature();
		feature.vote(true, 1L);
		feature.voteOptionality(false, 1L);
		feature.voteName("Dragon", true, 3L, m.getId());
		feature.voteDescription("An award from XXX", true, 4L, m.getId());
		feature.voteName("Dragon", false, 4L, m.getId());
		m.addFeature(feature);
		try {
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			logger.debug("Feature = " + feature.toString());
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertEquals("Shouldn't reach here", "");
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test @Ignore
	public void testSaveValueTwice() {
		Feature feature = new Feature();
		feature.vote(false, 2L);
		feature.vote(true, 1L);
		feature.voteOptionality(false, 11L);
		feature.voteName("Firefox", true, 333L, m.getId());
		feature.voteDescription("A software", true, 14L, m.getId());
		feature.voteName("Mozilla", true, 4L, m.getId());
		feature.voteName("Firefox", false, 11L, m.getId());
		m.addFeature(feature);
		try {
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			logger.debug("Feature = " + feature.toString());
			
			Feature feature2 = new Feature();
			feature2.setId(feature.getId());
			feature2 = dao.save(feature2);
			assertTrue(false);
		} catch (BeanPersistenceException e) {
			//logger.info("Couldn't save samething twice.");
			assertTrue(true);
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetByIdAfterSave() {
		try {
			Feature feature = new Feature();
			feature.vote(true, 10L);
			feature.vote(true, 20L);
			feature.vote(false, 11L);
			m.addFeature(feature);
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			logger.debug("Feature = " + feature.toString());
			Feature feature2 = dao.getById(feature.getId(), false);
			logger.debug("Feature fetched");
			assertTrue(feature == feature2);
		} catch (BeanPersistenceException e) {
			logger.error("Get after save failed.", e);
			assertTrue(false);
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetNullById() {
		try {
			assertNull(dao.getById(1000L, false));
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertTrue(false);
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testGetByName() {
		try {
			
			Feature feature = new Feature();
			feature.voteName("QueryMe", true, 1L, m.getId());
			
			
			Feature another = new Feature();
			another.voteName("Another", true, 3L, m.getId());
			
			m.addFeature(feature);
			m.addFeature(another);
			
			dao.save(another);
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			
			Feature me = dao.getByName(m.getId(), "QueryMe");
			assertEquals(feature.getId(), me.getId());
		} catch (Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetBySimilarName() {
		try {
			
			Feature feature = new Feature();
			feature.voteName("MarkWilliams", true, 1L, m.getId());
			
			
			Feature another = new Feature();
			another.voteName("MarkAllen", true, 3L, m.getId());
			
			m.addFeature(feature);
			m.addFeature(another);
			
			dao.save(another);
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			
			List<Feature> me = dao.getBySimilarName(m.getId(), "Mark");
			assertTrue(me.size()==2);
		} catch (Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetNullByName() {
		try {
			assertNull(dao.getByName(1L, "IamNotHere"));
		} catch (Exception e) {
			logger.error(e);
			assertTrue(false);
		}
	}
	
	@Test
	public void testDeleteFeature(){ 
		Feature feature = new Feature();
		feature.vote(true, 10L);
		feature.voteOptionality(false, 10L);
		feature.voteName("No-This-Name", true, 3L, m.getId());
		feature.voteDescription("Very bad thing happens if you see this", true, 4L, m.getId());
		m.addFeature(feature);
		try {
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			
			dao.deleteById(feature.getId());
			
			assertNull(dao.getById(feature.getId(), false));
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertEquals("Shouldn't reach here", "");
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteFeatureNameByVoting(){ 
		Feature feature = new Feature();
		feature.vote(true, 10L);
		feature.voteOptionality(false, 10L);
		feature.voteName("No-This-Name-Please", true, 3L, m.getId());
		feature.voteName("You-should-see-me", true, 3L, m.getId());
		feature.voteDescription("You should see me as well!!", true, 4L, m.getId());
		m.addFeature(feature);
		try {
			feature = dao.save(feature);
			DaoUtil.getModelDao().save(m);
			
			feature.voteName("No-This-Name-Please", false, 3L, m.getId());
			dao.save(feature);
		} catch (BeanPersistenceException e) {
			logger.error(e);
			assertEquals("Shouldn't reach here", "");
			
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
