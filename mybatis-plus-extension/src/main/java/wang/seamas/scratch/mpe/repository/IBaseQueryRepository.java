package wang.seamas.scratch.mpe.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.util.ReflectionUtils;
import wang.seamas.scratch.mpe.annotation.Query;
import wang.seamas.scratch.utils.CommonReflectionUtil;
import wang.seamas.scratch.utils.CommonStringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public interface IBaseQueryRepository<T> extends BaseMapper<T> {

    default QueryWrapper<T> getQueryWrapper() {
        return new QueryWrapper<T>();
    }

    default void initQueryWrapper(QueryWrapper<T> queryWrapper, Object queryParam) {
        Field[] allFields = CommonReflectionUtil.getAllFields(queryParam.getClass());

        Arrays.stream(allFields).filter(field -> field.isAnnotationPresent(Query.class))
                .forEach(field -> {
                    Query queryAnnotation = field.getAnnotation(Query.class);
                    String column = queryAnnotation.column();
                    if (column.isEmpty()) {
                        column = CommonStringUtils.toSnakeCase(field.getName());
                    }
                    try {
                        ReflectionUtils.makeAccessible(field);
                        Object value = field.get(queryParam);
                        queryAnnotation.value().wrap(queryWrapper, column, value);
                    } catch (IllegalAccessException ignored) {

                    }
                });

    }

}
