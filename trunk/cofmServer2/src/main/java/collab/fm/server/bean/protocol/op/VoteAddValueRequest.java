package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.entity.AttributeSet;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

// Vote or add value to a Model or a Feature
public class VoteAddValueRequest extends Request {

	private Long modelId;
	private Long featureId;
	private String attr;
	private String val;
	private Boolean yes;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddValueProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	public Long getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public Boolean getYes() {
		return yes;
	}
	public void setYes(Boolean yes) {
		this.yes = yes;
	}
	
	private static class VoteAddValueProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddValueRequest)) return false;
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			if (r.getModelId() == null && r.getFeatureId() == null) return false;
			return r.getAttr() != null && r.getVal() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_value operation");
			}
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			DefaultResponse rsp = new DefaultResponse(r);
			
			AttributeSet target = null;
			// If it is an operation on the Feature Model
			if (r.getFeatureId() == null) {
				target = DaoUtil.getModelDao().getById(r.getModelId(), false);
				if (target == null) {
					throw new InvalidOperationException("Invalid feature model ID: " + r.getModelId());
				}
			} else {
				// It is an operation on the Feature
				target = DaoUtil.getFeatureDao().getById(r.getFeatureId(), false);
				if (target == null) {
					throw new InvalidOperationException("Invalid feature ID: " + r.getFeatureId());
				}
			}
			
			Attribute a = target.getAttribute(r.getAttr());
			if (a == null) {
				throw new InvalidOperationException("Invalid attribute name: " + r.getAttr());
			}
			// If the attribute is NOT allow to global replicated, we should check if the same value existed
			if (!a.isEnableGlobalDupValues()) {
				AttributeSet as2 = null;
				if (r.getFeatureId() == null) {
					as2 = DaoUtil.getModelDao().getByAttrValue(r.getAttr(), r.getVal());
				} else {
					as2 = DaoUtil.getFeatureDao().getByAttrValue(r.getModelId(), r.getAttr(), r.getVal());
				}
				if (as2 != null) { // if the same value exists, then the target must be this object.
					target = as2;
				}
			}
			
			target.voteOrAddValue(r.getAttr(), r.getVal(), r.getYes(), r.getRequesterId());
			if (r.getFeatureId() == null) {
				DaoUtil.getModelDao().save((Model)target);
			} else {
				DaoUtil.getFeatureDao().save((Feature)target);
			}
			
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		private Long modelId;
		private Long featureId;
		private String attr;
		private String val;
		private Boolean yes;
		
		public DefaultResponse(VoteAddValueRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setFeatureId(r.getFeatureId());
			this.setAttr(r.getAttr());
			this.setVal(r.getVal());
			this.setYes(r.getYes());
		}
		
		public Long getModelId() {
			return modelId;
		}
		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}
		public Long getFeatureId() {
			return featureId;
		}
		public void setFeatureId(Long featureId) {
			this.featureId = featureId;
		}
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public Boolean getYes() {
			return yes;
		}
		public void setYes(Boolean yes) {
			this.yes = yes;
		}
	}
}