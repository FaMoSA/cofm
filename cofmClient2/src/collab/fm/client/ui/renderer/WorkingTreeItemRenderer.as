package collab.fm.client.ui.renderer {
	import mx.controls.treeClasses.*;
	import mx.utils.StringUtil;
	import collab.fm.client.data.TreeData;
	
	public class WorkingTreeItemRenderer extends GlobalTreeItemRenderer {
		public function WorkingTreeItemRenderer() {
			super();
		}

		override public function set data(value:Object): void {
			if (value != null) {
				super.data = value;
				var cur: XMLList = new XMLList(TreeListData(super.listData).item);
				// Set errors to red color
				var e1: int = int(cur[0].@parents);
				var unnamed: Boolean = String(cur[0].@name) == TreeData.UNNAMED;
				if (e1 > 1 || unnamed) {
					setStyle("color", 0xff0000);
				} else {
					setStyle("color", 0x000000);
				}
				// Bold the controversial features
				var rate: Number = Number(cur[0].@support);
				if (rate < 1) {
					setStyle("fontWeight", 'bold');
				} else {
					setStyle("fontWeight", 'normal');
				}
			}
		}

		
		// Show controversy rate for controversial features.
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number): void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			if (super.data) {
				var curItems: XMLList = new XMLList(TreeListData(super.listData).item);
				var extraInfo: String = "";
				var rate: Number = Number(curItems[0].@support);
				if (rate < 1) {
					extraInfo += " (" + (rate * 100).toPrecision(3) + "%)";
				}
				var people: String = String(curItems[0].@people);
				if (people != null && mx.utils.StringUtil.trim(people) != "") {
					extraInfo += "    <----- (" + people + ")";
				}
				super.label.text = TreeListData(super.listData).label + extraInfo;
			}
		}
		
	}
}