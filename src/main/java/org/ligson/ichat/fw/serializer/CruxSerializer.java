package org.ligson.ichat.fw.serializer;

public interface CruxSerializer {
    <T> String serialize(T t);

    <T> T deserialize(String content, Class<T> tClazz);
}
