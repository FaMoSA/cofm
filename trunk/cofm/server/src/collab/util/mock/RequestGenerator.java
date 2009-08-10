package collab.util.mock;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import collab.data.*;
import collab.util.*;

public class RequestGenerator {
	
	static Logger logger = Logger.getLogger(RequestGenerator.class);
	
	private static final String[] op = {
		Resources.OP_ADDCHILD,
		Resources.OP_ADDDES,
		Resources.OP_ADDEXCLUDE,
		Resources.OP_ADDNAME,
		Resources.OP_ADDREQUIRE,
		Resources.OP_SETEXT,
		Resources.OP_SETOPT
	};
	
	private static class ReqField extends Pair<Class<?>, String[]> {

		public ReqField(Class<?> type, String[] possibleValues) {
			super(type, possibleValues);
		}
		
	}
	private static ConcurrentHashMap<String, ReqField> fieldTable = new ConcurrentHashMap<String, ReqField>();
	static {
		//TODO: set possible values for user name and feature name.
		fieldTable.put(Resources.REQ_FIELD_USER, 
				new ReqField(String.class, null));  // user name
		fieldTable.put(Resources.OP_FIELD_OP,
				new ReqField(String.class, null));
		fieldTable.put(Resources.OP_FIELD_LEFT,
				new ReqField(Object.class, null));  // feature name
		fieldTable.put(Resources.OP_FIELD_RIGHT, 
				new ReqField(Object.class, null));  // feature name
		fieldTable.put(Resources.OP_FIELD_VOTE, 
				new ReqField(Boolean.class, null));
	}

	private static int count = 0;
	
	private static final int maxValueLength = 8;

	private static int maxFeatureId = 10;
	private static int maxUserId = 10;
	
	public static synchronized void setMaxFeatureId(int id) {
		maxFeatureId = id;
	}
	
	public static synchronized void setMaxUserId(int id) {
		maxUserId = id;
	}
	
	public static String nextIPv4() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			sb.append(trimHeadZeros(RandomStringUtils.randomNumeric(3)));
			if (i < 3) {
				sb.append('.');
			}
		}
		return sb.toString();
	}
	
	private static String trimHeadZeros(String s) {
		boolean needTrim = true;
		for (int i = 0; i < s.length(); i++) {
			if (!needTrim) {
				return s.substring(i);
			}
			if (s.charAt(i) != '0') {
				needTrim = false;
			}
		}
		return "0"; // trim at most s.length - 1 zeros.
	}
	
	public static String nextCommit() {
		return nextCommit(op[RandomUtils.nextInt(op.length)]);
	}
	
	public static String nextCommit(String op) {
		OpBean o = new OpBean();
		o.setOp(op);
		o.setLeft(generateValue(Resources.OP_FIELD_LEFT));
		o.setRight(generateValue(Resources.OP_FIELD_RIGHT));
		o.setVote((Boolean)generateValue(Resources.OP_FIELD_VOTE));
		Request req = new Request();
		req.setData(o);
		req.setId(nextCount());
		req.setName(Resources.REQ_COMMIT);
		req.setUser((String)generateValue(Resources.REQ_FIELD_USER));
		// In a "real" client, no Address field will be sent, so we need to skip it here.
		return Utils.beanToJson(req, new String[] {"address"}); 
	}
	
	
	
	private static synchronized int nextCount() {
		return ++count;
	}
	
	private static Object generateValue(String fieldName) {
		try {
			ReqField f = fieldTable.get(fieldName);
			if (Boolean.class.equals(f.first)) {
				return RandomUtils.nextBoolean();
			}
			if (Object.class.equals(f.first)) {
				return generateStringOrInteger(maxFeatureId, f.second);
			}
			return generateString(f.second);
		} catch (Exception e) {
			logger.warn("error in generate value for field '" + fieldName + "'");
			return "Error_in_generator";
		}
	}
	
	private static String generateString(String[] possibleValues) {
		if (possibleValues != null && possibleValues.length > 0) {
			return possibleValues[RandomUtils.nextInt(possibleValues.length)];
		}
		return RandomStringUtils.randomAlphabetic(maxValueLength);
	}
	
	private static Object generateStringOrInteger(int maxIntVal, String[] possibleValues) {
		if (possibleValues != null && possibleValues.length > 0) {
			boolean needString = RandomUtils.nextBoolean();
			if (needString) {
				return possibleValues[RandomUtils.nextInt(possibleValues.length)];
			}
		}
		return RandomUtils.nextInt(maxIntVal) + 1;
	}
	
	public static class OpBean {
		private String op;
		private Object left;
		private Object right;
		private Boolean vote;
		public OpBean() {}
		public String getOp() {
			return op;
		}
		public void setOp(String op) {
			this.op = op;
		}
		public Object getLeft() {
			return left;
		}
		public void setLeft(Object left) {
			this.left = left;
		}
		public Object getRight() {
			return right;
		}
		public void setRight(Object right) {
			this.right = right;
		}
		public Boolean getVote() {
			return vote;
		}
		public void setVote(Boolean vote) {
			this.vote = vote;
		}
	}
	
}
