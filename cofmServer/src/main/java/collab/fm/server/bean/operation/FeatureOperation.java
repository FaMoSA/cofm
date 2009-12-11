package collab.fm.server.bean.operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.persistence.FeatureDao;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;

/**
 * TODO: Origin vote and implicit votes should be transactional. <br/>
 * 
 * Class FeatureOperation defines the structure and meaning of operations which affect 
 * a feature or its attributes, including create_feature, add_name, add_description, and
 * set_optionality. <br/>
 * The logic of FeatureOperation.apply(): <br/>
 *  1) create_feature: If the feature ID is not null, it means this is a vote to an existed feature.
 *  Otherwise, try to create a new feature. First, check the name to ensure there's no other features with the same name.
 *  Then create (insert) the new feature via the persistence layer, which returns the generated
 *  database identifier. Finally, the database ID sets the featureId field in the operation. <br/>
 *  2) add_name/description, set_optionality: Check the feature's ID, and then invoke voteName/Description/Optionality. <br/> 
 *  3) If vote NO to a feature, then all relationships that contains the feature are voted NO. In addition, all
 *  attributes of the feature are voted NO as well. <br/>
 *  4) A voteExistence(YES) is implied when create a new feature. <br/>
 *  5) If vote YES to name or description of a feature, then a voteExistence(YES) is implied. <br/>
 *  6) Vote to optionality always implies YES to feature. 
 *  
 * @author Yi Li
 *
 */
public class FeatureOperation extends Operation {

	static Logger logger = Logger.getLogger(FeatureOperation.class);
	
	private Long featureId;
	private String value;
	
	public FeatureOperation() {
		
	}
	
	public Operation clone() {
		FeatureOperation op = new FeatureOperation();
		this.copyTo(op);
		return op;
	}
	
	public List<Operation> apply() throws BeanPersistenceException, InvalidOperationException {
		if (!valid()) {
			throw new InvalidOperationException("Invalid op fields.");
		}
		List<Operation> result = null;
		if (Resources.OP_ADD_DES.equals(name)) {
			result = applyAddDes();
		} else if (Resources.OP_ADD_NAME.equals(name)) {
			result = applyAddName();
		} else if (Resources.OP_CREATE_FEATURE.equals(name)) {
			result = applyCreateFeature();
		} else if (Resources.OP_SET_OPT.equals(name)) {
			result = applySetOpt();
		} else {
			throw new InvalidOperationException("Invalid op name: " + name);
		}
		if (result == null) {
			result = new ArrayList<Operation>();
		}
		result.add(this.clone());
		return result;
		
	}
	
	public boolean valid() {
		if (super.valid() && userid != null) {
			if (Resources.OP_CREATE_FEATURE.equals(name)) {
				if (vote.equals(false)) {
					return featureId != null;
				}
				return featureId != null || value != null;
			} else {
				return featureId != null;
			}
		}
		return false;
	}
	
	protected void copyTo(FeatureOperation op) {
		super.copyTo(op);
		op.setFeatureId(this.getFeatureId());
		op.setValue(this.getValue());
	}
	
	private List<Operation> applyAddDes() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId, false);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteDescription(value, vote, userid);
		return ImplicitVoteOperation.makeOperation(this, feature).apply();
	}
	
	private List<Operation> applyAddName() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId, false);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteName(value, vote, userid);
		return ImplicitVoteOperation.makeOperation(this, feature).apply();
	}
	
	private List<Operation> applyCreateFeature() throws BeanPersistenceException, InvalidOperationException {
		if (featureId == null) {
			if (vote.equals(false)) {
				throw new InvalidOperationException("Invalid vote: NO to inexisted feature.");
			}
			// Check if a feature with same name has already existed.
			Feature featureWithSameName = DaoUtil.getFeatureDao().getByName(value);
			if (featureWithSameName == null) {
				featureWithSameName = new Feature();
			}
			featureWithSameName.voteName(value, vote, userid);
			featureWithSameName.vote(true, userid);
			featureWithSameName = DaoUtil.getFeatureDao().save(featureWithSameName);
			featureId = featureWithSameName.getId();
			return null;
		}
		
		Feature feature = DaoUtil.getFeatureDao().getById(featureId, false);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		return ImplicitVoteOperation.makeOperation(this, feature).apply();
	}
	
	private List<Operation> applySetOpt() throws BeanPersistenceException, InvalidOperationException {
		Feature feature = DaoUtil.getFeatureDao().getById(featureId, false);
		if (feature == null) {
			throw new InvalidOperationException("No feature has ID: " + featureId);
		}
		feature.voteOptionality(vote, userid);
		return ImplicitVoteOperation.makeOperation(this, feature).apply();
	}
	
	public String toString() {
		return super.toString() + " " + featureId + " " + value;
	}
	
	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}