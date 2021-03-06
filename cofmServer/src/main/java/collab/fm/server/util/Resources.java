package collab.fm.server.util;

import java.lang.reflect.*;
import java.util.ResourceBundle;

public class Resources {
	
	private static ResourceBundle protocolRes = ResourceBundle.getBundle("protocol");
	private static ResourceBundle msgRes = ResourceBundle.getBundle("locale.message");
	
	//Protocol
	public static final String REQ_ERROR_AUTHORITY = get("req.error.authority");
	public static final String REQ_ERROR_FORMAT = get("req.error.format");
	
	public static final String REQ_COMMENT = get("req.comment");
	public static final String REQ_EXIT_MODEL = get("req.exitModel");
	public static final String REQ_EDIT = get("req.edit");
	public static final String REQ_UPDATE = get("req.update");
	public static final String REQ_COMMIT = get("req.commit");
	public static final String REQ_LOGIN = get("req.login");
	public static final String REQ_LOGOUT = get("req.logout");
	public static final String REQ_CONNECT = get("req.connect");
	public static final String REQ_LISTUSER = get("req.listuser");
	public static final String REQ_LIST_MODEL = get("req.listmodel");
	public static final String REQ_REGISTER = get("req.register");
	public static final String REQ_CREATE_MODEL = get("req.createModel");

	public static final String RSP_ERROR_FORMAT = get("rsp.error.format");
	
	public static final String RSP_SUCCESS = get("rsp.success");
	public static final String RSP_FORWARD = get("rsp.forward");
	public static final String RSP_ERROR = get("rsp.error");
	public static final String RSP_STALE = get("rsp.stale");
	public static final String RSP_SERVER_ERROR = get("rsp.serverError");

	public static final String OP_ADD_NAME = get("op.addName");
	public static final String OP_ADD_DES = get("op.addDes");
	public static final String OP_SET_OPT = get("op.setOpt");
	public static final String OP_CREATE_FEATURE = get("op.createFeature");
	public static final String OP_CREATE_RELATIONSHIP = get("op.createRelationship");
	
	public static final String BIN_REL_REFINES = get("binrel.refines");
	public static final String BIN_REL_REQUIRES = get("binrel.requires");
	public static final String BIN_REL_EXCLUDES = get("binrel.excludes");

	//Message
	public static final String MSG_ERROR_EXCEPTION = get("msg.error.exception");
	public static final String MSG_ERROR_CONSTRAINT = get("msg.error.constraint");
	public static final String MSG_ERROR_REQUEST = get("msg.error.request");
	
	public static final String MSG_ERROR_FIELD_MISSING = get("msg.error.field.missing");
	public static final String MSG_ERROR_FIELD_VALUE = get("msg.error.field.value");
		
	public static final String MSG_ERROR_USER_DENIED = get("msg.error.user.denied");
	public static final String MSG_ERROR_USER_LOGIN_FAILED = get("msg.error.user.loginfailed");
	public static final String MSG_ERROR_USER_EXISTED = get("msg.error.user.existed");
	public static final String MSG_ERROR_USER_LOGIN_REPEAT = get("msg.error.user.login.repeat");
	
	public static final String MSG_ERROR_FEATURE_NOTFOUND = get("msg.error.feature.notfound");
	public static final String MSG_ERROR_FEATURE_APPLYOP = get("msg.error.feature.applyop");
	
	public static final String MSG_ERROR_PERSISTENT_WRITE = get("msg.error.persistent.write");
	public static final String MSG_ERROR_PERSISTENT_GET = get("msg.error.persistent.get");
	
	public static final String MSG_ERROR_STALE = get("msg.error.stale");
	
	public static final String MSG_LOGIN = get("msg.login");
	public static final String MSG_REGISTER = get("msg.register");
	
	private static String get(String key) {
		try {
			return protocolRes.getString(key);
		} catch (Exception e) {
			return msgRes.getString(key);
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		Class cRes = Class.forName("collab.fm.server.util.Resources");
		Field[] fields = cRes.getFields();
		int i = 1;
		for (Field f: fields) {
			String val = null;
			try {
				val = (String)f.get(null);
			} catch (Exception e) {
				val = String.valueOf(f.get(null));
			}
			System.out.println((i++) + ": " + val);
		}
	}
}
