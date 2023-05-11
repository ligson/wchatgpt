package org.ligson.ichat.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.ligson.ichat.serializer.CruxSerializer;
import org.ligson.ichat.serializer.jackson.JacksonSerializer;
import org.ligson.ichat.util.MyHttpClient;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class WChatgptConfig {

    @Bean
    public CruxSerializer cruxSerializer(ObjectMapper objectMapper) {
        return new JacksonSerializer(objectMapper);
    }

    @Bean
    public MyHttpClient myHttpClient(CruxSerializer cruxSerializer) {
        return new MyHttpClient(cruxSerializer);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer cruxJackson2ObjectMapperBuilderCustomizer() {
        return b -> b.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                //空对象不序列化
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(BigDecimal.class, ToStringSerializer.instance);
    }

}
