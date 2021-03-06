package collab.fm.client.data {
	import collab.fm.client.command.StartEditFeatureCommand;
	import collab.fm.client.event.*;
	import collab.fm.client.util.*;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import mx.collections.SortField;
	import mx.collections.XMLListCollection;

	public class CurrentFeature implements IOperationListener {
		private static var _instance: CurrentFeature = new CurrentFeature();

		private var _feature: XML;

		public var id: int;

		[Bindable]
		public var names: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var descriptions: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var votes: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var parents: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var children: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var binaryConstraints: ArrayCollection = new ArrayCollection();

		[Bindable]
		public var basicInfo: XMLListCollection = new XMLListCollection();

		public static function get instance(): CurrentFeature {
			return _instance;
		}

		public function CurrentFeature() {
			FeatureModel.instance.registerSubView(this);

			ClientEvtDispatcher.instance().addEventListener(
				ModelUpdateEvent.SUCCESS, onModelUpdate);
			ClientEvtDispatcher.instance().addEventListener(
				FeatureSelectEvent.DB_CLICK_ON_TREE, onCurrentFeatureSelected);
		}

		private function toUserArray(list: XMLList): Array {
			var rslt: Array = new Array();
			for each (var u: Object in list) {
				rslt.push(u.text().toString());
			}
			return rslt;
		}
		
		private function clear(): void {
			id = -1;
			votes.source = [];
			names.source = [];
			parents.source = [];
			children.source = [];
			binaryConstraints.source = [];
			basicInfo.source = null;
			
		}
		
		private function onModelUpdate(evt: ModelUpdateEvent): void {
			// clear current feature
			this.clear();
		}

		private function onCurrentFeatureSelected(evt: FeatureSelectEvent): void {
			// Broadcast my location
			new StartEditFeatureCommand(evt.id).execute();
			
			this.clear();
			
			// Set the feature id
			id = evt.id;
			_feature = XML(FeatureModel.instance.features.source.(@id==String(evt.id))[0]);
			
			// update creator info
			var creator: String = UserList.instance.getNameById(int(_feature.@creator));
			basicInfo.addItem(<attr key="creator" type={Cst.ATTR_TYPE_STRING} label="Creator:" value={creator}/>);
			
			updateVotes(); // votes to this feature
			updateNames();
			updateDescriptions();
			updateOptionality();
			updateRefinements();
			updateBinaryConstraints();

			// update basic info
			ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
		}

		private function updateVotes(): void {
			votes.source = [];
			// no votes
			var noNum: int = XMLList(_feature.no.user).length();
			var yesNum: int = XMLList(_feature.yes.user).length();
			var yesRatio: Number = (noNum <= 0) ? 100 : (100 * yesNum / (yesNum + noNum));
			var noRatio: Number = 100 - yesRatio;

			votes.addItem({
					"label": RS.EDIT_FEATURE_BAR_NO,
					"num": noNum,
					"ratio": ((noRatio > 0) ? noRatio.toPrecision(3) : "0") + "%",
					"n": toUserArray(XMLList(_feature.no.user))
				});
			// yes votes
			votes.addItem({
					"label": RS.EDIT_FEATURE_BAR_YES,
					"num": yesNum,
					"ratio": ((yesRatio > 0) ? yesRatio.toPrecision(3) : "0") + "%",
					"y": toUserArray(XMLList(_feature.yes.user))
				});
		}

		private function updateNames(): void {
			names.source = [];
			// Construct the "names" array for Feature_Name_DataGrid.
			// Columns: name, supporters (with percentage), opponents.
			var primary: String;
			var rate: Number = 0;
			for each (var _name: Object in _feature.names.name) {
				var y: int = XMLList(_name.yes.user).length();
				var n: int = XMLList(_name.no.user).length();
				names.addItem({
						"name": _name.@val,
						"supporters": y,
						"opponents": n,
						"y": toUserArray(XMLList(_name.yes.user)),
						"n": toUserArray(XMLList(_name.no.user))
					});
				// update primary name (max supporting rate)
				var r: Number = y / (y+n);
				if (rate < r) {
					rate = r;
					primary = _name.@val;
				}
			}
			var strRate: String = (rate * 100).toPrecision(3) + "%";
			// remove previous name from basic info
			var key: String = "name";
			var previous: XMLList = this.basicInfo.source.(@key==key);
			var xml: XML = <attr key={key} type={Cst.ATTR_TYPE_STRING} label="Name:" value={primary} rate={strRate}/>;
			if (previous.length() <= 0) {
				this.basicInfo.addItem(xml);
			} else {
				var pos: int = this.basicInfo.getItemIndex(previous[0]);
				this.basicInfo.removeItemAt(pos);
				this.basicInfo.addItemAt(xml, pos);
			}
		}

		private function updateDescriptions(): void {
			descriptions.source = [];
			var des: Array = new Array();
			for each (var d: Object in _feature.descriptions.description) {
				var yesId: Array = toUserArray(XMLList(d.yes.user));
				var noId: Array = toUserArray(XMLList(d.no.user));
				des.push({
						"des": XML(d.value).text().toString(),
						"supporters": yesId.length,
						"opponents": noId.length,
						"y": yesId,
						"n": noId,
						"ratio": ((noId.length == 0) ? "100%" : 
							Number(100 * yesId.length / (yesId.length + noId.length)).toPrecision(3) + "%")
					});
			}
			ModelUtil.sortOnRating(des, "y", "n", UserList.instance.myId);
			descriptions.source = des;

			// update basic info
			var key: String = "des";
			if (des.length <= 0) {
				var pre: XMLList = this.basicInfo.source.(@key==key);
				if (pre.length() > 0) {
					var p: int = this.basicInfo.getItemIndex(pre[0]);
					this.basicInfo.removeItemAt(p);
				}
				return;
			}
			var primary: String = des[0].des;
			var y: int = int(des[0].supporters);
			var n: int = int(des[0].opponents);
			var rate: Number = y / (y + n);
			var strRate: String = (rate * 100).toPrecision(3) + "%";
			// remove previous name from basic info

			var previous: XMLList = this.basicInfo.source.(@key==key);
			var xml: XML = <attr key={key} type={Cst.ATTR_TYPE_TEXT} label="Description:" value={primary} rate={strRate}/>;
			if (previous.length() <= 0) {
				this.basicInfo.addItem(xml);
			} else {
				var pos: int = this.basicInfo.getItemIndex(previous[0]);
				this.basicInfo.removeItemAt(pos);
				this.basicInfo.addItemAt(xml, pos);
			}
		}

		private function updateOptionality(): void {

		}

		private function updateRefinements(): void {
			parents.source = [];
			children.source = [];
			for each (var r: Object in FeatureModel.instance.binaries.source) {
				if (r.@type == Cst.BIN_REL_REFINES) {
					if (r.@left == String(this.id)) {
						children.addItem({
								"id": r.@id,
								"name": ModelUtil.getFeatureNameById(r.@right),
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_REFINES,
								"left": r.@left,
								"right": r.@right
							});
					} else if (r.@right == String(this.id)) {
						parents.addItem({
								"id": r.@id,
								"name": ModelUtil.getFeatureNameById(r.@left),
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_REFINES,
								"left": r.@left,
								"right": r.@right
							});
					}
				}
			}
		}

		private function updateBinaryConstraints(): void {
			binaryConstraints.source = [];
			for each (var r: Object in FeatureModel.instance.binaries.source) {
				if (r.@type == Cst.BIN_REL_REQUIRES) {
					if (r.@left == String(this.id)) {
						binaryConstraints.addItem({
								"id": r.@id,
								"name": "this requires " + ModelUtil.getFeatureNameById(r.@right),
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_REQUIRES,
								"left": r.@left,
								"right": r.@right
							});
					} else if (r.@right == String(this.id)) {
						binaryConstraints.addItem({
								"id": r.@id,
								"name": ModelUtil.getFeatureNameById(r.@left) + " requires this",
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_REQUIRES,
								"left": r.@left,
								"right": r.@right
							});
					}
				} else if (r.@type == Cst.BIN_REL_EXCLUDES) {
					if (r.@left == String(this.id)) {
						binaryConstraints.addItem({
								"id": r.@id,
								"name": "this excludes " + ModelUtil.getFeatureNameById(r.@right),
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_EXCLUDES,
								"left": r.@left,
								"right": r.@right
							});
					} else if (r.@right == String(this.id)) {
						binaryConstraints.addItem({
								"id": r.@id,
								"name": "this excludes " + ModelUtil.getFeatureNameById(r.@left),
								"supporters": XMLList(r.yes.user).length(),
								"opponents": XMLList(r.no.user).length(),
								"y": toUserArray(XMLList(r.yes.user)),
								"n": toUserArray(XMLList(r.no.user)),
								"type": Cst.BIN_REL_EXCLUDES,
								"left": r.@left,
								"right": r.@right
							});
					}
				}
			}

			// Sort binaryConstraints on "type" first, "id" second.
			var sort: Sort = new Sort();
			sort.fields = [new SortField("type", true), new SortField("id", true)];
			binaryConstraints.sort = sort;
			binaryConstraints.refresh();
		}



		public function handleAddDescription(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateDescriptions();
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(
					ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleAddName(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateNames();
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(
					ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleCreateFeature(op:Object): void {
			if (op["featureId"] == String(id)) {
				if (op[FeatureModel.SHOULD_DELETE_ELEMENT] == true) {
					this.clear();
					ClientEvtDispatcher.instance().dispatchEvent(
						new ClientEvent(ClientEvent.CURRENT_FEATURE_DELETED));
				} else {
					this.updateVotes();
					if (op[FeatureModel.VOTE_NO_TO_FEATURE] == true) {
						this.updateNames();
						this.updateDescriptions();
						this.updateOptionality();
					}
				}
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleCreateBinaryRelationship(op:Object): void {
			if (op["leftFeatureId"] == String(id) || op["rightFeatureId"] == String(id)) {
				switch (op["type"]) {
					case Cst.BIN_REL_REFINES:
						this.updateRefinements();
						break;
					case Cst.BIN_REL_REQUIRES:
					case Cst.BIN_REL_EXCLUDES:
						this.updateBinaryConstraints();
						break;
				}
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleSetOpt(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateOptionality();
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleFeatureVotePropagation(op:Object): void {
			if (op["featureId"] == String(id)) {
				this.updateVotes();
				
				// update basic info
				ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
			}
		}

		public function handleRelationshipVotePropagation(op:Object): void {
			updateRefinements();
			updateBinaryConstraints();
			
			// update basic info
			ClientEvtDispatcher.instance().dispatchEvent(new ClientEvent(ClientEvent.BASIC_INFO_UPDATED));
		}
	}
}