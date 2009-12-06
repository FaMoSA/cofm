package collab.fm.server.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.BinaryRelationshipOperation;
import collab.fm.server.bean.protocol.CommitRequest;
import collab.fm.server.bean.protocol.CommitResponse;
import collab.fm.server.bean.protocol.FeatureOperation;
import collab.fm.server.bean.protocol.Operation;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.BeanUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.controller.*;


public class CommitAction extends Action {

	static Logger logger = Logger.getLogger(CommitAction.class);
	
	public CommitAction() {
		super(new String[] { Resources.REQ_COMMIT });
	}

	@Override
	public boolean execute(Request req, ResponseGroup rg) throws ActionException {
		try {
			List<Operation> ops = ((CommitRequest)req).getOperation().apply();
			
			CommitResponse cr = new CommitResponse();
			cr.setName(Resources.RSP_SUCCESS);
			cr.addOperations(ops);
			
			CommitResponse cr2 = new CommitResponse();
			cr.setName(Resources.RSP_FORWARD);
			cr.addOperations(ops);
			
			rg.setBack(cr);
			rg.setBroadcast(cr2);
			rg.setPeer(null);
			
			return true;
		} catch (Exception e) {
			logger.warn("Execution failed.", e);
			throw new ActionException("Action execution failed."); 
		}
	}

}
