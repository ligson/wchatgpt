package org.ligson.ichat.context;

import org.ligson.ichat.cache.LocalRequestCache;
import org.ligson.ichat.common.request.RequestStore;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.domain.WindowSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {
    @Autowired
    private LocalRequestCache localRequestCache;
    @Autowired
    private RequestStore requestStore;
    private final ThreadLocal<WindowSize> windowSizeContext = new ThreadLocal<>();

    public void getContextId() {
        requestStore.getSessionId();
    }

    public void setCurrentUser(User user) {
        localRequestCache.setAttr(SessionContext.class.getSimpleName() + "_user", user);
    }


    public User getCurrentUser() {
        return localRequestCache.getAttr(SessionContext.class.getSimpleName() + "_user");
    }

    public void setAttr(String key, Object object) {
        localRequestCache.setAttr(SessionContext.class.getSimpleName() + "_" + key, object);
    }

    public Object getAttr(String key) {
        return localRequestCache.getAttr(SessionContext.class.getSimpleName() + "_" + key);
    }

    public WindowSize getWindowSize() {
        return windowSizeContext.get();
    }

    public void setWindowSize(WindowSize windowSize) {
        windowSizeContext.set(windowSize);
    }

    public void removeWindowSize() {
        windowSizeContext.remove();
    }
}
