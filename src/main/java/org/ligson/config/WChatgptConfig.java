package org.ligson.config;

import org.ligson.fw.annotation.BootBean;
import org.ligson.fw.annotation.BootConfig;
import org.ligson.serializer.CruxSerializer;
import org.ligson.serializer.jackson.JacksonSerializer;
import org.ligson.util.MyHttpClient;
import org.ligson.vo.AppConfig;
import org.ligson.wx.WXClient;

import java.io.IOException;

@BootConfig
public class WChatgptConfig {
    @BootBean
    public AppConfig appConfig() {
        try {
            return AppConfig.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BootBean
    public CruxSerializer cruxSerializer() {
        return new JacksonSerializer();
    }

    @BootBean
    public MyHttpClient myHttpClient() {
        return new MyHttpClient();
    }

    @BootBean
    public WXClient wxClient() {
        return new WXClient();
    }
}
