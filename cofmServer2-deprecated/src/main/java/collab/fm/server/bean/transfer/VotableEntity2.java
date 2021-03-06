package collab.fm.server.bean.transfer;

import java.util.HashSet;
import java.util.Set;

public class VotableEntity2 extends Entity2 {
	// Voters of the YES vote (1-vote)
	protected Set<Long> v1 = new HashSet<Long>();
	// Voters of the NO vote (0-vote)
	protected Set<Long> v0 = new HashSet<Long>();
	
	public Set<Long> getV1() {
		return v1;
	}
	public void setV1(Set<Long> v1) {
		this.v1 = v1;
	}
	public Set<Long> getV0() {
		return v0;
	}
	public void setV0(Set<Long> v0) {
		this.v0 = v0;
	}
	public void addV1(Long id) {
		v1.add(id);
	}
	public void addV0(Long id) {
		v0.add(id);
	}
	
}
