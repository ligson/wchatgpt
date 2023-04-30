package org.ligson.ichat.context;

import org.ligson.ichat.domain.User;
import org.ligson.ichat.domain.WindowSize;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {
    private final ThreadLocal<User> userContext = new ThreadLocal<>();
    private final ThreadLocal<WindowSize> windowSizeContext = new ThreadLocal<>();

    public void setCurrentUser(User user) {
        userContext.set(user);
    }

    public User getCurrentUser() {
        return userContext.get();
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
