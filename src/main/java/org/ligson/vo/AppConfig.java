package org.ligson.vo;

import lombok.Data;
import org.ligson.util.YmlReader;

import java.io.IOException;

@Data
public class AppConfig {
    private AppVo app;
    private static AppConfig instance;

    public synchronized static AppConfig getInstance() throws IOException {
        if (instance == null) {
            YmlReader ymlReader = new YmlReader();
            instance = ymlReader.getAppConfig();
        }
        return instance;
    }
}
