package cofm.util
{
	import cofm.command.*;
	import cofm.model.*;
	import cofm.event.*;
	
	import flash.utils.Dictionary;

	/**
	 * Dispatch server response to proper commands.
	 */
	public class ServerDataDispatcher {

		private static var rsps: Array = [
			Cst.RSP_ERROR,
			Cst.RSP_STALE,
			Cst.RSP_SUCCESS
			];

		private static function isResponse(name: String): Boolean {
			return rsps.indexOf(name) >= 0;
		}

		public static function dispatchData(data: Object): void {
			// get the resposne name
			var name: String = data[Cst.FIELD_RSP_NAME] as String;
			if (isResponse(name) && data[Cst.FIELD_RSP_SOURCE_ID] != null) {
				Msg.showResponse(name, data[Cst.FIELD_RSP_MESSAGE]);
				CommandBuffer.instance().getCommand(
					int(data[Cst.FIELD_RSP_SOURCE_ID])).handleResponse(data);
			} else if (Cst.RSP_FORWARD == name) {
				// do forwarded command
				name = data[Cst.FIELD_RSP_SOURCE_NAME] as String;
				switch (name) {
					case Cst.REQ_VA_ATTR:
					case Cst.REQ_VA_ATTR_ENUM:
					case Cst.REQ_VA_ATTR_NUMBER:
					case Cst.REQ_VA_BIN_REL:
					case Cst.REQ_VA_FEATURE:
					case Cst.REQ_VA_VALUE:
						ClientEvtDispatcher.instance().dispatchEvent(
							new OperationCommitEvent(
							OperationCommitEvent.FORWARDED, 
							data));
						break;
					case Cst.REQ_FOCUS:
						// Handle current feature model only.
						if (ModelCollection.instance().currentModelId == int(data["modelId"])) {
							ClientEvtDispatcher.instance().dispatchEvent(
								new FeatureSelectEvent(
									FeatureSelectEvent.OTHER_PEOPLE_SELECT_ON_TREE,
									int(data["featureId"]),
									null,  // feature name is omitted
									int(data["modelId"]),
									int(data["requesterId"])));
						}
						break;
					case Cst.REQ_LOGOUT:
						ClientEvtDispatcher.instance().dispatchEvent(
							new LogoutEvent(LogoutEvent.LOGGED_OUT, int(data[Cst.FIELD_RSP_SOURCE_USER_ID])));
						break;
					case Cst.REQ_EXIT_MODEL:
						ClientEvtDispatcher.instance().dispatchEvent(
							new PageSwitchEvent(PageSwitchEvent.OTHERS_EXIT_WORK_PAGE, 
								int(data[Cst.FIELD_RSP_SOURCE_USER_ID]),
								int(data["modelId"])));
							break;
					case Cst.REQ_REGISTER:
						var d: Dictionary = new Dictionary();
						var key: String = String(data[Cst.FIELD_RSP_SOURCE_USER_ID]);
						var val: String = String(data[Cst.FIELD_RSP_MESSAGE]);
						d[key] = val;
						ClientEvtDispatcher.instance().dispatchEvent(
							new ListUserEvent(ListUserEvent.APPEND, 
							d));
						break;
					case Cst.REQ_COMMENT:
						ClientEvtDispatcher.instance().dispatchEvent(
							new AddCommentEvent(AddCommentEvent.SUCCESS,
							int(data[Cst.FIELD_RSP_SOURCE_USER_ID]),
							int(data["featureId"]),
							data["content"],
							data["dateTime"]));
						break;
				}
			} else if (Cst.RSP_SERVER_ERROR == name) {
				//TODO: report internal error
			}
		}
	}
}