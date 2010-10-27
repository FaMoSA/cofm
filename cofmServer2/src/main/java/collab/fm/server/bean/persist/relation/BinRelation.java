package collab.fm.server.bean.persist.relation;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.transfer.DataItem2;

public class BinRelation extends Relation {
	
	private Long sourceId;
	private Long targetId;
	
	public void setEntities(Entity source, Entity target) {
		this.getEntities().clear();
		this.getEntities().add(source);
		this.getEntities().add(target);
		
		this.setSourceId(source.getId());
		this.setTargetId(target.getId());
		
		// Maintain the many-to-many association between Entity and Relation
		source.addRelationship(this);
		target.addRelationship(this);
	}
	
	@Override
	public void transfer(DataItem2 r) {
//		BinaryRelation2 r2 = (BinaryRelation2) r;
//		super.transfer(r2);
//		r2.setLeft(this.getLeftFeatureId());
//		r2.setRight(this.getRightFeatureId());
//		r2.setType(this.getType());
	}

	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return "(" + this.getType().getTypeName() + ", " + sourceId + ", " + targetId + ")";
	}
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
}
