package collab.filter;

import collab.data.*;

public abstract class Filter {
	protected final String name;
	
	public Filter(String filterName) {
		name = filterName;
	}
	
	public Request filterRequest(Request request) {
		request.latestFilter(name);
		return doFilterRequest(request);
	}
	
	public Response filterResponse(Response response) {
		response.latestFilter(name);
		return doFilterResponse(response);
	}
	
	protected void onFilterError(Filterable filtee, String error, String msg) {
		filtee.filterError(error);
		filtee.filterMessage(msg);
	}
	
	protected abstract Request doFilterRequest(Request request);
	protected abstract Response doFilterResponse(Response response);
	
	public String getName() {
		return name;
	}
}
