package org.ligson.ichat.fw.cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.ligson.ichat.fw.filter.FilterOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 本地请求内缓存
 */
@Slf4j
@Component
public class LocalRequestCache {

    public static final String USER_CONTEXT = ObjectUtils.CONST("user-context");
    public static final String REQUEST_CONTEXT = ObjectUtils.CONST("request-context");

    private static final ThreadLocal<Map<String, Object>> inheritableThreadCache = InheritableThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ThreadLocal<Map<String, Set<String>>> inheritableThreadContextCache = InheritableThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ThreadLocal<Map<String, Set<String>>> inheritableThreadContextChangeCache = InheritableThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ThreadLocal<Map<String, Object>> threadCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public <T> void setAttr(String key, T value) {
        setAttr(key, value, false);
    }

    public <T> T getAttr(String key) {
        return getAttr(key, false);
    }

    public <T> void setAttr(String key, T value, boolean threadVisible) {
        log.debug("【LocalRequestCache】本地缓存设置key:{},value:{}", key, value);
        if (threadVisible) {
            inheritableThreadCache.get().put(key, value);
        } else {
            threadCache.get().put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttr(String key, boolean threadVisible) {
        if (threadVisible) {
            if (inheritableThreadCache.get().containsKey(key)) {
                return (T) inheritableThreadCache.get().get(key);
            } else {
                return null;
            }
        } else {
            if (threadCache.get().containsKey(key)) {
                return (T) threadCache.get().get(key);
            } else {
                return null;
            }
        }
    }

    @Order(FilterOrder.SERVLET_FILTER_LOCAL_REQUEST_CACHE_ORDER)
    @Component
    static class LocalRequestCacheCleaner extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            try {
                filterChain.doFilter(request, response);
            } finally {
                log.debug("【LocalRequestCache】请求{}结束,本地缓存清理", request.getRequestURI());
                inheritableThreadCache.remove();
                threadCache.remove();
                inheritableThreadContextCache.remove();
                inheritableThreadContextChangeCache.remove();
            }
        }
    }

    public void resetContextChangeCache() {
        inheritableThreadContextChangeCache.remove();
    }

    public Set<String> getContext(String key) {
        return inheritableThreadContextCache.get().get(key);
    }

    public void setContext(String key, String name) {
        Set<String> contexts = inheritableThreadContextCache.get().get(key);
        if (CollectionUtils.isEmpty(contexts)) {
            contexts = new HashSet<>();
            inheritableThreadContextCache.get().put(key, contexts);
        }
        contexts.add(name);
    }

    public void addContext(String key, Collection<String> names) {
        Set<String> contexts = inheritableThreadContextChangeCache.get().get(key);
        if (CollectionUtils.isEmpty(contexts)) {
            contexts = new HashSet<>();
            inheritableThreadContextChangeCache.get().put(key, contexts);
        }
        contexts.addAll(names);
    }

    public Set<String> getChangeContext(String key) {
        return inheritableThreadContextChangeCache.get().get(key);
    }
}
