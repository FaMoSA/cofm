package collab.storage;

import java.util.Arrays;
import java.util.List;

import collab.data.bean.Feature;
import collab.data.bean.Operation;

/**
 * Mock class for unit test. Almost do nothing.
 * @author Yi Li
 *
 */
public class DataProviderMock implements DataProvider {

	@Override
	public Operation commitOperation(Operation op) {
		op.setId(1);
		return op;
	}

	@Override
	public Feature getFeatureById(Integer id) {
		Feature feat = new Feature();
		feat.setId(id);
		return feat;
	}

	@Override
	public Feature getFeatureByName(String name) {
		Feature feat = new Feature();
		feat.setId(1);
		return feat;
	}

	@Override
	public Integer getFeatureIdByName(String name) {
		return 1;
	}

	@Override
	public List<Feature> getRecentFeatures(Integer beginId) {
		Feature feat = new Feature();
		feat.setId(beginId);
		Feature feat2 = new Feature();
		feat2.setId(beginId + 1);
		return Arrays.asList(feat, feat2);
	}

	@Override
	public List<Operation> getRecentOperations(Integer beginId) {
		Operation op = new Operation();
		op.setId(beginId);
		Operation op2 = new Operation();
		op2.setId(beginId + 1);
		return Arrays.asList(op, op2);
	}

	@Override
	public Integer getUserIdByName(String username) {
		return 1;
	}

	@Override
	public boolean updateFeature(Feature f) {
		return true;
	}

}
