package org.ligson.ichat.chatlog;

import org.ligson.ichat.fw.simplecrud.dao.CrudDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogDao extends CrudDao<ChatLog> {
}
