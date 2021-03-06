package cofm.command 
{
	import cofm.model.*;
	import cofm.event.*;
	import cofm.util.*;
	
	public class LogoutCommand implements IDurableCommand {
		private var _cmdId: int;

		public function LogoutCommand() {
		}

		public function execute(): void {
			_cmdId = CommandBuffer.instance().addCommand(this);
			// Build a login request object and convert it to json
			var req: Object = {
					"id": _cmdId,
					"name": Cst.REQ_LOGOUT,
					"requesterId": UserList.instance().myId
				};
			Connector.instance().send(JsonUtil.objectToJson(req));
		}

		public function redo(): void {
		}

		public function undo(): void {
		}

		public function setDurable(val:Boolean): void {
		}

		public function handleResponse(data:Object): void {
			if (Cst.RSP_SUCCESS == (data[Cst.FIELD_RSP_NAME] as String)
				&& Cst.REQ_LOGOUT == data[Cst.FIELD_RSP_SOURCE_NAME]) {

				CommandBuffer.instance().removeCommand(_cmdId);
				ClientEvtDispatcher.instance().dispatchEvent(
					new LogoutEvent(LogoutEvent.LOGGED_OUT, UserList.instance().myId));
				}
		}

	}
}