package org.ligson.thread;

import org.ligson.vo.AppConfig;

public class ChatTest {
    private static AppConfig appConfig;

    public static void chatgpt() {
        //String skToken = "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";
        //String skToken = "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";

//       /* OpenAiClient openAiClient = new OpenAiClient(appConfig.getApp().getOpenai().getSkToken());
//        ModelResult result = openAiClient.models();
//        for (Model datum : result.getData()) {
//            log.debug(datum.getId());
//        }
//        CompletionsReq completionsReq = new CompletionsReq();
//        completionsReq.setModel("text-davinci-003");
//        completionsReq.setPrompt("你好吗？");
//        CompletionsRes result2 = openAiClient.completions(completionsReq);
//        System.out.println(result2);*/
    }

    public static void turing() {
       /* TuringClient turingClient = new TuringClient(appConfig.getApp().getTuring().getUserId(), appConfig.getApp().getTuring().getApiKey());
        TuringResult turingResult = turingClient.chat("你好吗?");
        System.out.println(turingResult);*/
    }
}
