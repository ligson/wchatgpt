package org.ligson.ichat.context;

import org.ligson.ichat.domain.User;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {
    private ThreadLocal<User> userContext = new ThreadLocal<>();

    public void setCurrentUser(User user) {
        userContext.set(user);
    }

    public User getCurrentUser() {
        return userContext.get();
    }
}
