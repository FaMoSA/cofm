package collab.fm.server.bean.entity;

public interface Votable {
	public void vote(boolean yes, Long userid);
	public boolean equals(Votable v);
}