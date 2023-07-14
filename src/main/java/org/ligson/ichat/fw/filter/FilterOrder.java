package org.ligson.ichat.fw.filter;

import org.springframework.core.Ordered;

/***
 * filter顺序
 */
public class FilterOrder {
    //Servlet Filter
    //RID<LOG<Statistics
    //Request ID
    //SessionRepositoryFilter.DEFAULT_ORDER = Integer.MIN_VALUE + 50; 必须是sessionId Base64解码后的顺序
    public static final int SERVLET_FILTER_RID_ORDER = Ordered.HIGHEST_PRECEDENCE + 51;
    //request header
    public static final int SERVLET_FILTER_REQUEST_ORDER = Ordered.HIGHEST_PRECEDENCE + 52;
    //ContextIdCreator
    public static final int SERVLET_FILTER_CONTEXT_CREATOR_ORDER = Ordered.HIGHEST_PRECEDENCE + 53;
    //日志
    public static final int SERVLET_FILTER_MDC_ORDER = Ordered.HIGHEST_PRECEDENCE + 54;
    // 服务治理
    public static final int SERVLET_FILTER_SG_ORDER = Ordered.HIGHEST_PRECEDENCE + 55;
    //清除本地缓存
    public static final int SERVLET_FILTER_LOCAL_REQUEST_CACHE_ORDER = Ordered.HIGHEST_PRECEDENCE + 56;

    //----------------------------------------------------
    // 清理threadLocal的操作 只能定义在此分割线的上方
    //----------------------------------------------------

    //IP黑名单
    public static final int SERVLET_FILTER_IP_BLACK_ORDER = Ordered.HIGHEST_PRECEDENCE + 57;
    //URL黑名单
    public static final int SERVLET_FILTER_URL_BLACK_ORDER = Ordered.HIGHEST_PRECEDENCE + 58;
    //request数量统计
    public static final int SERVLET_FILTER_REQUEST_COUNTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 59;
    //统计
    public static final int SERVLET_FILTER_STATISTICS_ORDER = Ordered.HIGHEST_PRECEDENCE + 60;
    //QueryCacheFilter
    public static final int SERVLET_FILTER_QUERY_CACHE_ORDER = Ordered.HIGHEST_PRECEDENCE + 61;

}
