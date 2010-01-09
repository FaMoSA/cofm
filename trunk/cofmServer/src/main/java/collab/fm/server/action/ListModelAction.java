package collab.fm.server.action;

import java.util.*;
import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.ModelDescription;
import collab.fm.server.bean.entity.ModelName;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.entity.Votable;
import collab.fm.server.bean.protocol.ListModelRequest;
import collab.fm.server.bean.protocol.ListModelResponse;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.transfer.Model2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ActionException;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ListModelAction extends Action {
	
	private static Logger logger = Logger.getLogger(ListModelAction.class);

	public ListModelAction() {
		super(new String[] { Resources.REQ_LIST_MODEL} );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean doExecute(Request req, ResponseGroup rg)
			throws ActionException, StaleDataException {
		try {
			ListModelRequest request = (ListModelRequest)req;
			
			List<Model> all = null;
			if (request.getSearchWord() == null) {
				all = DaoUtil.getModelDao().getAll();
			} else {
				all = DaoUtil.getModelDao().getBySimilarName(request.getSearchWord());
			}
			
			ListModelResponse lmr = new ListModelResponse();
			lmr.setModels(new ArrayList<Model2>());
			
			if (all != null) {
				for (Model m: all) {
					lmr.getModels().add(m.transfer());
				}
			}
			lmr.setName(Resources.RSP_SUCCESS);
			
			rg.setBack(lmr);
			
			return true;
		} catch (BeanPersistenceException e) {
			logger.warn("Bean Persistence Failed.", e);
			throw new ActionException(e);
		} 
	}

}
