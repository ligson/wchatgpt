package org.ligson.ichat.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.context.SessionContext;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.serializer.CruxSerializer;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.WebResult;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    private static final String[] ignore_urls = new String[]{"/wchatgpt-be/api/sys/.*",
            "/wchatgpt-be/api/auth/.*",
            "/wchatgpt-be/user-images/.*",
            "/wchatgpt-be/api/user/upgrade.*",
            "/wchatgpt-be/api/user/resetPassword.*",
            //static file
            ".*(html)$", ".*(js)$", ".*(css)$", "/wchatgpt-be/css/.*", "/wchatgpt-be/img/.*", "/wchatgpt-be/js/.*"};
    private final UserService userService;
    private final SessionContext sessionContext;
    private final CruxSerializer serializer;

    public AuthInterceptor(UserService userService,
                           SessionContext sessionContext,
                           CruxSerializer serializer) {
        this.userService = userService;
        this.sessionContext = sessionContext;
        this.serializer = serializer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String url = request.getRequestURI();
        for (String ignoreUrl : ignore_urls) {
            if (url.matches(ignoreUrl)) {
                return true;
            }
        }
        String token = request.getHeader("token");
        User user = userService.getLoginUserByToken(token);
        if (user == null) {
            WebResult webResult = WebResult.newErrorInstance("用户已经过期，请重新登录");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().println(serializer.serialize(webResult));
            return false;
        }
        sessionContext.setCurrentUser(user);
        return true;
    }
}