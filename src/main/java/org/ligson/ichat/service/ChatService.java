package org.ligson.ichat.service;

public interface ChatService {
    String chat(String contextId, String question);

    String img(String contextId, String question);
}
