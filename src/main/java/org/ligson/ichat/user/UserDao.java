package org.ligson.ichat.user;

import org.ligson.ichat.fw.simplecrud.dao.CrudDao;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudDao<User> {

}
