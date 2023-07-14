package org.ligson.ichat.fw.serializer.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.fw.serializer.CruxSerializer;

@Slf4j
public class JacksonSerializer implements CruxSerializer {
    private final ObjectMapper objectMapper;

    public JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
