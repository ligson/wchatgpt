package org.ligson.ichat.job;

import org.ligson.ichat.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserJob {

    @Autowired
    private UserDao userDao;

    //2小时维护一次用户使用次数
    @Scheduled(fixedDelay = 2 * 60 * 60 * 1000)
    public void syncUserTimes() {
        userDao.syncUserTimes();
    }
}
