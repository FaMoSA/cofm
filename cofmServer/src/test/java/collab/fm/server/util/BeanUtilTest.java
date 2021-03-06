package collab.fm.server.util;

import java.util.*;


import org.apache.log4j.Logger;
import org.junit.*;

import static org.junit.Assert.*;

import collab.fm.server.bean.operation.BinaryRelationshipOperation;
import collab.fm.server.bean.operation.Operation;
import collab.fm.server.bean.protocol.*;

@Ignore
public class BeanUtilTest {

	static Logger logger = Logger.getLogger(BeanUtilTest.class);
	
	@Test
	public void testOperationsJsonBidirectionCast() {
			
	}
	
	@Test
	public void testBeanFromMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Mark");
		map.put("vote", true);
		map.put("userid", 100L);
		map.put("leftFeatureId", 1L);
		map.put("rightFeatureId", 2L);
		try {
		Operation op = BeanUtil.mapToBean(BinaryRelationshipOperation.class, map);
		logger.debug(op.toString());
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testNestedBeanJsonBidirectionCast() {
		Bean1 b1 = new Bean1();
		b1.setName("bean one");
		Bean2 b2 = new Bean2();
		b2.setId("two");
		b2.setData(Arrays.asList(b1));
		Bean3 b3 = new Bean3();
		b3.setKey("key 3");
		b3.setValue(b2);
		try {
		String json = BeanUtil.beanToJson(b3);
		//logger.info(json);
		
		// Step 1: json to bean3, typeof value == DynaBean
		Bean3 bean = BeanUtil.jsonToBean(json, Bean3.class, null);
		
		// Step 2: bean3.value(DynaBean) to Bean2
		Map<String, Class> map = new HashMap<String, Class>();
		map.put("data", Bean1.class);
		Bean2 bean2 = BeanUtil.jsonToBean(bean.getValue(), Bean2.class, map);
		
		assertEquals(b3.getKey(), bean.getKey());
		assertEquals(b2.getId(), bean2.getId());
		assertArrayEquals(b2.getData().toArray(), bean2.getData().toArray());
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public static class Bean1 {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object obj) {
			return name.equals(((Bean1)obj).getName());
		}
		
		
	}
	
	public static class Bean2 {
		private String id;
		private List<Bean1> data = new ArrayList<Bean1>();
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<Bean1> getData() {
			return data;
		}
		public void setData(List<Bean1> data) {
			this.data = data;
		}
	}
	
	public static class Bean3 {
		private String key;
		private Object value;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
}
