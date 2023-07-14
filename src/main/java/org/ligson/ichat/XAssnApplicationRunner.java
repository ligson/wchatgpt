package org.ligson.ichat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class XAssnApplicationRunner implements ApplicationRunner {
    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int port = serverProperties.getPort() == null ? 8080 : serverProperties.getPort().intValue();
        String contextPath = serverProperties.getServlet().getContextPath();
        String url = "http://127.0.0.1:" + port + contextPath;
        long totalTime = System.currentTimeMillis() - Main.APP_START_TIME;
        log.info("应用已经正常启动(耗时:{}s)，访问地址:{}", totalTime / 1000.0, url);
    }
}
