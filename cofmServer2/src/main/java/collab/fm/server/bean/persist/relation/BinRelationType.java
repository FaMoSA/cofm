package collab.fm.server.bean.persist.relation;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.transfer.BinRelationType2;
import collab.fm.server.bean.transfer.DataItem2;

// A simple one-to-one binary relation type
public class BinRelationType extends RelationType {
	
	protected ElementType sourceType;
	protected ElementType targetType;
	
	
	
	@Override 
	public void transfer(DataItem2 item) {
		BinRelationType2 that = (BinRelationType2) item;
		super.transfer(that);
		that.setTypeName(this.getTypeName());
		that.setSuperId(null);  // relatioType's super type is always null.
		that.setModel(this.getModel().getId());
		that.setHier(this.isHierarchical());
		that.setDir(this.isDirected());
		that.setSourceId(this.getSourceType().getId());
		that.setTargetId(this.getTargetType().getId());
	}

	public ElementType getSourceType() {
		return sourceType;
	}

	public void setSourceType(ElementType sourceType) {
		this.sourceType = sourceType;
	}

	public ElementType getTargetType() {
		return targetType;
	}

	public void setTargetType(ElementType targetType) {
		this.targetType = targetType;
	}

}
