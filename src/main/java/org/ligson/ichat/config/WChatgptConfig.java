package org.ligson.ichat.config;

import org.ligson.ichat.fw.serializer.CruxSerializer;
import org.ligson.ichat.util.MyHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WChatgptConfig {


    @Bean
    public MyHttpClient myHttpClient(CruxSerializer cruxSerializer) {
        return new MyHttpClient(cruxSerializer);
    }


}
