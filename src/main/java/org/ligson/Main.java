package org.ligson;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.FwBooter;
import org.ligson.fw.annotation.BootApp;

@BootApp(packages = "org.ligson")
@Slf4j
public class Main {


    public static void main(String[] args) throws Exception {
        FwBooter.run(Main.class, args);
    }
}
