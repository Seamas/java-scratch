package wang.seamas.scratch.mpe.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import wang.seamas.scratch.dto.PaginationRequest;
import wang.seamas.scratch.utils.CommonReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;

public interface IPageQueryRepository<T> extends IBaseQueryRepository<T> {

    default <P extends PaginationRequest> IPage<T> page(P queryParam, QueryWrapper<T> wrapper) {
        IPage<T> pageParam = new Page<>();
        pageParam.setCurrent(queryParam.getPageIndex());
        pageParam.setSize(queryParam.getPageSize());

        if (wrapper == null) {
            wrapper = getQueryWrapper();
        }
        initQueryWrapper(wrapper, queryParam);

        if (StringUtils.hasText(queryParam.getSort())) {
            // 获得 T 的实际类型，
            @SuppressWarnings("unchecked")
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            // 判断 T 是否有 queryParam.getSort() 字段
            Field[] allFields = CommonReflectionUtil.getAllFields(entityClass);
            boolean b = Arrays.stream(allFields).anyMatch(field -> field.getName().equals(queryParam.getSort()));
            if (b) {
                final String asc = "asc";
                if (asc.equalsIgnoreCase(queryParam.getOrder())) {
                    wrapper.orderByAsc(queryParam.getSort());
                } else {
                    wrapper.orderByDesc(queryParam.getSort());
                }
            } else {
                throw new RuntimeException("sql注入风险, 不允许排序：" + queryParam.getSort());
            }
        }

        return getMapper().selectPage(pageParam, wrapper);
    }


    default <P extends PaginationRequest>  IPage<T> page(P queryParam) {
        return page(queryParam, null);
    }
}
