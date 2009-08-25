package collab.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import collab.data.*;
import collab.data.bean.User;
import collab.server.Controller;
import collab.storage.DataProvider;

public class RegisterAction extends Action {

	static Logger logger = Logger.getLogger(RegisterAction.class);
	
	public RegisterAction(Controller controller,
			DataProvider dp) {
		super(new String[]{Resources.REQ_REGISTER}, controller, dp);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public List<Response> process(Object input) {
		// TODO mock
		List<Response> result = new ArrayList<Response>();
		Response r = new Response();
		writeSource(r, (Request)input);
		
		User user = new User();
		user.setName(((Request)input).getUser());
		user = dp.addUser(user);
		
		write(r, Response.TYPE_BACK, Resources.RSP_SUCCESS, user);
		result.add(r);
		return result;
	}

}
