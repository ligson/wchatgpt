package org.ligson.config;

import org.ligson.util.MyHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WChatgptConfig {

    @Bean
    public MyHttpClient myHttpClient() {
        return new MyHttpClient();
    }
}
