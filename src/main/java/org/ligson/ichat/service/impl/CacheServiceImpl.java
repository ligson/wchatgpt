package org.ligson.ichat.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.ligson.ichat.user.User;
import org.ligson.ichat.fw.serializer.CruxSerializer;
import org.ligson.ichat.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {

    private final static String USER_SESSION_CONTEXT_PREFIX = "xchat:session-context:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CruxSerializer cruxSerializer;

    @Override
    public User getLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        String userJson = stringRedisTemplate.boundValueOps(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token).get();
        if (StringUtils.isNotBlank(userJson)) {
            return cruxSerializer.deserialize(userJson, User.class);
        }
        return null;
    }

    @Override
    public int getLoginUserTerminalCount(String userId) {
        Map<Object, Object> userTokensMap = stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + userId).entries();
        return userTokensMap == null ? 0 : userTokensMap.values().size();
    }

    @Override
    public void setUserAndToken(String token, User user) {
        stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + user.getId()).put(token, DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        stringRedisTemplate.boundValueOps(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token).set(cruxSerializer.serialize(user));
    }

    @Override
    public void relieveUserAndToken(String token, User user) {
        stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + user.getId()).delete(token);
        stringRedisTemplate.delete(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token);
    }
}
