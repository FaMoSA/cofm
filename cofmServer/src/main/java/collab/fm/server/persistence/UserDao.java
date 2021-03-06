package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.User;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface UserDao extends GenericDao<User, Long> {
	public User getByName(String name) throws BeanPersistenceException, StaleDataException;
	public User checkThenGet(User user) throws BeanPersistenceException, StaleDataException;
	public List getAll() throws BeanPersistenceException, StaleDataException;
}
