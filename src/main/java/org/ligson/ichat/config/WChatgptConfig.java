package org.ligson.ichat.config;

import org.ligson.ichat.util.MyHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WChatgptConfig {

    @Bean
    public MyHttpClient myHttpClient() {
        return new MyHttpClient();
    }
}
