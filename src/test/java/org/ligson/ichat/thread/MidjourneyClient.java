package org.ligson.ichat.thread;

import org.ligson.ichat.util.MyHttpClient;

import java.io.File;
import java.io.IOException;

public class MidjourneyClient {

    private static final String MIDJOURNEY_URL = "http://midjourney.com/api/render?url=https://www.example.com&width=1024&delay=2000&format=png";

    public static void main(String[] args) throws IOException {

        MyHttpClient myHttpClient = new MyHttpClient(null);

        File file = myHttpClient.download(MIDJOURNEY_URL, "test", "/Users/lijinsheng/workspace/coderwk/myideawk/wchatgpt/user-data/user-images");
        System.out.println(file.getAbsolutePath());

    }
}
