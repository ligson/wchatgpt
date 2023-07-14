package org.ligson.ichat.fw.request;

import org.ligson.ichat.fw.cache.LocalRequestCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CruxRequestFilterWrapper {

    @Value("#{'${crux.context.no-need-statistics-uris:/.*static.*,/.*templates.*,/.*healthcheck.*,/.*.js,/.*.html,/.*.css,/.*prometheus}'.split(',')}")
    private List<String> noNeedStatisticsUris;
    @Value("${crux.context.key:CruxRequestFilterWrapper-isNeedStatistics}")
    private String key;

    private final LocalRequestCache localRequestCache;

    public CruxRequestFilterWrapper(LocalRequestCache localRequestCache) {
        this.localRequestCache = localRequestCache;
    }

    public boolean isNeedStatistics(String requestURI) {
        Object attr = localRequestCache.getAttr(key, true);
        if (attr != null) {
            return (boolean) attr;
        }
        boolean flag = true;
        long count = noNeedStatisticsUris.stream()
                .filter((pattern) -> (requestURI != null && requestURI.matches(pattern))).count();
        if (count > 0) {
            flag = false;
        }
        localRequestCache.setAttr(key, flag, true);
        return flag;
    }
}
