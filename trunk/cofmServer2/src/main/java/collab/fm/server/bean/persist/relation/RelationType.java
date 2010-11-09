package collab.fm.server.bean.persist.relation;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.transfer.DataItem2;

public class RelationType extends ElementType {
	
	// Whether the relation is hierarchical.
	protected boolean hierarchical;
	
	// Whether the relation is directed.
	protected boolean directed;
	
	@Override
	public void transfer(DataItem2 item) {
		
	}

	public boolean isHierarchical() {
		return hierarchical;
	}

	public void setHierarchical(boolean hierarchical) {
		this.hierarchical = hierarchical;
	}
	
	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}
	
}
