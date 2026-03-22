package wang.seamas.scratch.mpe.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

public interface IListQueryRepository<T> extends IBaseQueryRepository<T> {

    default List<T> list(Object queryParam, QueryWrapper<T> wrapper) {
        if (wrapper == null) {
            wrapper = getQueryWrapper();
        }
        initQueryWrapper(wrapper, queryParam);

        return getMapper().selectList(wrapper);
    }

    default List<T> list(Object queryParam) {
        return list(queryParam, null);
    }
}
