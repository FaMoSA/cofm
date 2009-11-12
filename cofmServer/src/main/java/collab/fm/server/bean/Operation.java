package collab.fm.server.bean;

import collab.fm.server.util.Resources;

public class Operation {
	
	public static transient final String[] NAMES = {
		Resources.OP_ADDCHILD,
		Resources.OP_ADDDES,
		Resources.OP_ADDEXCLUDE,
		Resources.OP_ADDNAME,
		Resources.OP_ADDREQUIRE,
		Resources.OP_SETEXT,
		Resources.OP_SETOPT
	};
	
	private Integer id;
	private String name;
	private Integer left;
	private Object right;  // op(left, right);
	private Boolean vote;
	private Integer userid; // the committer id
	
	public Operation() {
		
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String op) {
		this.name = op;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(Integer left) {
		this.left = left;
	}

	public Object getRight() {
		return right;
	}

	public void setRight(Object right) {
		this.right = right;
	}

	public Boolean getVote() {
		return vote;
	}

	public void setVote(Boolean vote) {
		this.vote = vote;
	}
	
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "Operation = {op: " + name +
			   ", left: " + left + 
			   ", right: " + right +
			   ", vote: " + (vote ? "yes" : "no") + 
			   ", userId: " + userid + "}";
	}
	
}