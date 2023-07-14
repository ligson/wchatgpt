package org.ligson.ichat.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserJob {


    //2小时维护一次用户使用次数
    @Scheduled(fixedDelay = 2 * 60 * 60 * 1000)
    public void syncUserTimes() {
        //userDao.syncUserTimes();
    }
}
