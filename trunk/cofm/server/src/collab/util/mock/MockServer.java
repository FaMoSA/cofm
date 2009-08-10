package collab.util.mock;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.server.BasicController;
import collab.server.Controller;
import collab.storage.DataProvider;
import collab.util.Utils;
import collab.action.*;
import collab.data.*;

public class MockServer {
	static Logger logger = Logger.getLogger(MockServer.class);
	
	private Controller controller = new BasicController();
	private DataProvider dp = new MockDataProvider();
	private List<Action> actions = new LinkedList<Action>();
	
	public MockServer() {
		actions.add(new CommitAction(controller, dp));
	}
	
	public void run(int times) {
		for (; times > 0; times--) {
			logger.info("************ Start " + times + " *************");
			Request req = new Request();
			req.setAddress(RequestGenerator.nextIPv4());
			req.setData(RequestGenerator.nextCommit());
			List<Response> rsp = controller.handleRequest(req);
			if (rsp == null) {
				logger.info("Response is null.");
			} else {
				for (Response r: rsp) {
					logger.info("Response is: " + Utils.beanToJson(r));
				}
			}
		}
	}
}
