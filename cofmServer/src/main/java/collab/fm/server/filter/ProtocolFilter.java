package collab.fm.server.filter;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.FilterException;

public class ProtocolFilter extends Filter {

	static Logger logger = Logger.getLogger(ProtocolFilter.class);
	
	private void writeSource(Request req, Response rsp) {
		if (rsp != null) {
			rsp.setRequesterId(req.getRequesterId());
			rsp.setRequestId(req.getId());
			rsp.setRequestName(req.getName());
		}
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// Ensure the source information has been added to responses
		try {
			// No response from Actions/Filters means success.
			if (rg.getBack() == null) {
				rg.setBack(new Response());
				rg.getBack().setName(Resources.RSP_SUCCESS);
			}
			writeSource(req, rg.getBack());
			writeSource(req, rg.getBroadcast());
			writeSource(req, rg.getPeer());
			logger.info("Source information has wrote.");
			return true;
		} catch (Exception e) {
			logger.error("Couldn't write source.", e);
			throw new FilterException("Couldn't write source.", e);
		}
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// Do nothing now
		return true;
	}

	@Override
	protected void doDisconnection(String addr, ResponseGroup rg) {
		// TODO Auto-generated method stub
		
	}

}
