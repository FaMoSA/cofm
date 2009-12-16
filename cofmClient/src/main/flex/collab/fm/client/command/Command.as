package collab.fm.client.command
{
	public interface Command
	{
		public function execute(): Boolean;
		public function redo(): Boolean;
		public function undo(): Boolean;
	}
}