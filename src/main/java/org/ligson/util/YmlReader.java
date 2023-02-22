package org.ligson.util;

import lombok.Getter;
import org.ligson.vo.AppConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;

@Getter
public class YmlReader {
    private AppConfig appConfig;

    public YmlReader() throws IOException {
        String path = YmlReader.class.getClassLoader().getResource("application.yml").getFile();
        Yaml yaml = new Yaml();
        appConfig = yaml.loadAs(new FileReader(path), AppConfig.class);
    }

    public static void main(String[] args) throws Exception {
        YmlReader ymlReader = new YmlReader();
        AppConfig app = ymlReader.getAppConfig();
        System.out.println(app);
    }
}
