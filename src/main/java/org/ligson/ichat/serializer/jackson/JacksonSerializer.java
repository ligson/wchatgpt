package org.ligson.ichat.serializer.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.serializer.CruxSerializer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JacksonSerializer implements CruxSerializer {
    private ObjectMapper objectMapper;

    public JacksonSerializer() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public <T> String serialize(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error(t + "序列化失败:" + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> T deserialize(String content, Class<T> tClazz) {
        try {
            return objectMapper.readValue(content, tClazz);
        } catch (JsonProcessingException e) {
            log.error(content + "反序列化类型：" + tClazz.getSimpleName() + "失败:" + e.getMessage(), e);
            return null;
        }
    }
}
