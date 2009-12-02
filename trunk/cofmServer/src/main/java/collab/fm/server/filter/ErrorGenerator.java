package collab.fm.server.filter;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.util.exception.FilterException;

/**
 * Generate error values for request and response deliberately.
 * This filter is often placed between ResponseValidator and RequestValidator,
 * which means Actions will receive requests with correct format but wrong values, 
 * and/or clients will receive responses like that.
 * Useful for testing.
 * 
 * @author Yi Li
 *
 */
public class ErrorGenerator extends Filter {
	
	static Logger logger = Logger.getLogger(ErrorGenerator.class);

	@Override
	protected void doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected FilterException onFilterError(Request req, ResponseGroup rg,
			Throwable t) {
		// TODO Auto-generated method stub
		return null;
	}

}
