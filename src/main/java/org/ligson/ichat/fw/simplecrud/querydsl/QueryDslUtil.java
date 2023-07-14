package org.ligson.ichat.fw.simplecrud.querydsl;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.ligson.ichat.fw.ex.InnerException;
import org.ligson.ichat.fw.simplecrud.dao.CrudDaoManager;
import org.ligson.ichat.fw.simplecrud.operator.QueryOperator;
import org.ligson.ichat.fw.simplecrud.vo.QueryCondition;
import org.ligson.ichat.fw.simplecrud.vo.QueryField;
import org.ligson.ichat.fw.util.ServiceLocator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unchecked")
public class QueryDslUtil {
    private static QuerydslBindingsFactory bindingsFactory;
    private static ConversionService conversionService;

    public static final int EXP_MAX_SIZE = 800;

    public static Predicate createPredicate(QueryCondition queryCondition, Class<?> entityClazz) {

        BooleanBuilder predicate = new BooleanBuilder();
        PathBuilder<?> entityPath = new PathBuilder<>(entityClazz, toLowerCamelCase(entityClazz.getSimpleName()));
        if (null != queryCondition) {
            TypeInformation<?> typeInformation = TypeInformation.of(entityClazz);
            for (QueryField queryField : queryCondition.getQueryFields()) {
                Class<?> fieldType = Objects.requireNonNull(getPath(queryField.getName(), typeInformation)).getType();
                Object fieldValue = conversionService.convert(queryField.getValue(), fieldType);
                if (queryField.getOperator() == QueryOperator.EQ) {
                    predicate.and(entityPath.get(queryField.getName()).eq(fieldValue));
                } else if (queryField.getOperator() == QueryOperator.NE) {
                    predicate.and(entityPath.get(queryField.getName()).ne(fieldValue));
                } else if (queryField.getOperator() == QueryOperator.IN) {
                    entityPath.get(queryField.getName()).in();
                }
            }
        }
        return predicate;
    }

    public static String toLowerCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }


    /***
     * in超过1000条件分割
     * @param ids id
     * @param entityName 实体名
     * @param fieldName 实体字段
     */
    @SneakyThrows
    public static Predicate getInPartition(String ids, String entityName, String fieldName) {
        String[] idLongList = ids.split(",");
        CrudDaoManager gpm = ServiceLocator.getBean(CrudDaoManager.class);
        Class<?> entityClz = null;
        if (gpm != null) {
            entityClz = gpm.entityType(entityName);
        }
        PropertyDescriptor pd = null;
        if (entityClz != null) {
            pd = BeanUtils.getPropertyDescriptor(entityClz, fieldName);
        }
        if (pd == null) {
            throw new InnerException(String.format("实体上:%s没有属性:%s", entityName, fieldName));
        }
        if (pd.getPropertyType() == Long.class) {
            List<Long> idList = new ArrayList<>();
            for (String s : idLongList) {
                idList.add(Long.parseLong(s));
            }
            return getInPartition(idList, entityName, fieldName);
        } else {
            List<String> idList = new ArrayList<>(Arrays.asList(idLongList));
            return getInStringPartition(idList, entityName, fieldName);
        }
    }

    public static Predicate getInPartition(List<Long> ids, String entityName, String fieldName) {
        return getInStringPartition(ids.stream().map(String::valueOf).collect(Collectors.toList()), entityName, fieldName);
    }

    /***
     * in超过1000条件分割
     * @param ids id
     * @param entityName 实体名
     * @param fieldName 实体字段
     */
    public static Predicate getInStringPartition(List<String> ids, String entityName, String fieldName) {
        // 使用in条件拼接
        log.debug("{}.{} 的条件数为{}没有达到临时表使用条件，使用in条件拼接", entityName, fieldName, ids.size());
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        List<List<String>> partition = Lists.partition(ids, EXP_MAX_SIZE);
        CrudDaoManager gpm = ServiceLocator.getBean(CrudDaoManager.class);
        Class<?> domainType = null;
        if (gpm != null) {
            domainType = gpm.entityType(entityName);
        }
        TypeInformation<?> typeInformation = TypeInformation.of(domainType);
        for (List<String> idsList : partition) {
            createExtraPredicate(convertStringToExpressStr(idsList, fieldName), typeInformation).ifPresent(booleanBuilder::or);
        }
        return booleanBuilder;
    }

    private static String convertStringToExpressStr(List<String> ids, String fieldName) {
        return fieldName + " in (" + StringUtils.join(ids, ",") + ")";
    }


    public static Optional<Predicate> createExtraPredicate(String express, TypeInformation<?> domainType) {
        if (express.contains(">")) {
            return createExtraPredicate0(express, domainType, ">", ComparableExpressionBase.class, "gt");
        } else if (express.contains("<")) {
            return createExtraPredicate0(express, domainType, "<", ComparableExpressionBase.class, "lt");
        } else if (express.contains("=")) {
            return createExtraPredicate0(express, domainType, "=", SimpleExpression.class, "eq");
        } else if (express.contains(" like ")) {
            return createExtraPredicate0(express, " like ", domainType, "like", StringExpression.class, "like");
        } else if (express.contains(" not in ")) {
            return createExtraPredicate0(express, " not in ", domainType, "not in", SimpleExpression.class, "notIn");
        } else if (express.contains(" in ")) {
            return createExtraPredicate0(express, " in ", domainType, "in", SimpleExpression.class, "in");
        } else if (express.contains(" is ")) {
            if (express.endsWith("is null")) {
                return createExtraPredicate0(express, " is null", domainType, "is null", SimpleExpression.class, "isNull");
            } else if (express.endsWith("is not null")) {
                return createExtraPredicate0(express, " is not null", domainType, "is not null", SimpleExpression.class, "isNotNull");
            }
        }
        return Optional.empty();
    }

    @SneakyThrows
    private static Optional<Predicate> createExtraPredicate0(String express, TypeInformation<?> domainType, String ops,
                                                             Class<?> pathType, String methodName) {
        return createExtraPredicate0(express, ops, domainType, ops,
                pathType, methodName);
    }

    @SneakyThrows
    private static Optional<Predicate> createExtraPredicate0(String express, String splitOps, TypeInformation<?> domainType, String ops,
                                                             Class<?> pathType, String methodName) {
        String[] tmp = express.split(splitOps);
        String source = tmp[0].trim();
        String value = "";
        if (tmp.length > 1) {
            value = tmp[1].trim();
        }
        Path<?> path = getPath(source, domainType);
        if (null == path) {
            return Optional.empty();
        }
        log.debug("path 为：{}", path);
        if (pathType.isAssignableFrom(path.getClass())) {
            if ("in".equals(methodName) || "notIn".equals(methodName)) {
                value = value.substring(1, value.length() - 1);
                //对值超过1000，Oracle SQL报错解决
                //Predicate predicate = QdslUtils.getInPartition(value, domainType.getType().getSimpleName(), source);
                //对值超过1000，Oracle SQL报错解决
                List<List<String>> partition = null;
//                        Lists.partition(Arrays.asList(value.split(",")), EXP_MAX_SIZE);
                BooleanBuilder booleanBuilder = new BooleanBuilder();
                for (List<String> ids : partition) {
//                    booleanBuilder.or((Predicate) org.apache.commons.beanutils.MethodUtils.invokeMethod(path, methodName, conversionService.convert(String.join(",", ids), Array.newInstance(path.getType(), 1).getClass())));
                }
                return Optional.of(booleanBuilder);
            } else if ("isNull".equals(methodName) || "isNotNull".equals(methodName)) {
                return Optional.of((Predicate) MethodUtils.invokeMethod(path, methodName, null));
            } else {
                if ("like".equals(methodName)) {
                    int startIdx = express.indexOf(" like ");
                    value = express.substring(startIdx + 6);
                    //value = value.substring(1, value.length() - 1);
                }
                return null;
//                return Optional.of((Predicate) MethodUtils.invokeMethod(path, methodName,
//                        conversionService.convert(value, path.getType())));
            }

        } else {
            throw new InnerException(source + " 无法使用 " + ops + " 操作符");
        }
    }

    static Path<?> reifyPath(EntityPathResolver resolver, PropertyPath path, Optional<Path<?>> base) {

        Optional<Path<?>> map = base.filter(it -> it instanceof CollectionPathBase)
                .map(CollectionPathBase.class::cast)
                .map(CollectionPathBase::any)
                .map(Path.class::cast)
                .map(it -> reifyPath(resolver, path, Optional.of(it)));

        return map.orElseGet(() -> {

            Path<?> entityPath = base.orElseGet(() -> resolver.createPath(path.getOwningType().getType()));

            Field field = ReflectionUtils.findField(entityPath.getClass(), path.getSegment());
            Object value = ReflectionUtils.getField(field, entityPath);

            PropertyPath next = path.next();

            if (next != null) {
                return reifyPath(resolver, next, Optional.of((Path<?>) value));
            }

            return (Path<?>) value;
        });
    }

    public static Path<?> getPath(String source, TypeInformation<?> domainType) {
        // soource 为 _ 时，PropertyPath.from 会抛异常
        if ("_".equals(source)) {
            return null;
        }

        try {
            PropertyPath propertyPath = PropertyPath.from(source, domainType);
            EntityPathResolver entityPathResolver = bindingsFactory.getEntityPathResolver();
            return reifyPath(entityPathResolver, propertyPath, Optional.empty());
        } catch (PropertyReferenceException o_O) {
            return null;
        }
    }

    @Component
    private static class BeanAware {
        BeanAware(QuerydslBindingsFactory bindingsFactory,
                  @Qualifier("mvcConversionService")
                  ConversionService conversionService
        ) {

            QueryDslUtil.bindingsFactory = bindingsFactory;
            QueryDslUtil.conversionService = conversionService;
        }
    }

}
