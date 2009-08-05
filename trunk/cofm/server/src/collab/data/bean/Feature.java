package collab.data.bean;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Feature extends Votable {
	
	private class VotableString extends Votable {		
		public VotableString(String value) {
			super(value);
		}
	}
	
	private class VotableBoolean extends Votable {
		public VotableBoolean() {
			super(new Boolean(true));
		}
	}
	
	private Long id;
	private VotableBoolean mandatory = new VotableBoolean();
	private ConcurrentLinkedQueue<VotableString> names = new ConcurrentLinkedQueue<VotableString>();
	private ConcurrentLinkedQueue<VotableString> descriptions = new ConcurrentLinkedQueue<VotableString>();
	private ConcurrentLinkedQueue<Feature> requiring = new ConcurrentLinkedQueue<Feature>();
	private ConcurrentLinkedQueue<Feature> excluding = new ConcurrentLinkedQueue<Feature>();
	private ConcurrentLinkedQueue<Feature> children = new ConcurrentLinkedQueue<Feature>();
	
	public Feature() {
		super();
	}

	public Long getId() {
		return id;
	}

	private void setId(Long id) { // for Hibernate
		this.id = id;
	}
}
