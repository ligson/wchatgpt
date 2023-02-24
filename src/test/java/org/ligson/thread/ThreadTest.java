package org.ligson.thread;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ThreadTest {
    public static String thread() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "ok";
    }

    public static void main(String[] args) {


        //5s

       CompletableFuture.supplyAsync(new Supplier<String>() {
           @Override
           public String get() {
               return thread();
           }
       }).whenComplete(new BiConsumer<String, Throwable>() {
           @Override
           public void accept(String s, Throwable throwable) {

           }
       });
    }
}
