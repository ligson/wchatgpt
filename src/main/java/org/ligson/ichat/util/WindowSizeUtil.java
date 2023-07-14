package org.ligson.ichat.util;

import org.ligson.ichat.fw.context.SessionContext;
import org.ligson.ichat.domain.WindowSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WindowSizeUtil {

    @Autowired
    private SessionContext sessionContext;

    public String getStyle() {
        String fmt = "style=\"width: %dpx; height: %dpx;\"";
        WindowSize windowSize = sessionContext.getWindowSize();
        int winWidth = windowSize.getWidth();
        int winHeight = windowSize.getHeight();
        int imageWidth = 960;
        int imageHeight = 1568;
        int num = 10;
        for (int i = 10; i > 1; i--) {
            if ((imageWidth * (i * 0.1f)) < winWidth && (imageHeight * (i * 0.1f)) < winHeight) {
                num = i;
                break;
            }
        }
        float w = imageWidth * (num * 0.1f);
        float h = imageHeight * (num * 0.1f);
        return String.format(fmt, Math.round(w), Math.round(h));
    }
}
