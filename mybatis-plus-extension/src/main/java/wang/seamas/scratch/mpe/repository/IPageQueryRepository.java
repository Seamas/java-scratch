package wang.seamas.scratch.mpe.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import wang.seamas.scratch.dto.PaginationRequest;

public interface IPageQueryRepository<T> extends IBaseQueryRepository<T> {

    default <P extends PaginationRequest> IPage<T> page(P queryParam, QueryWrapper<T> wrapper) {
        IPage<T> pageParam = new Page<>();
        pageParam.setCurrent(queryParam.getPageIndex());
        pageParam.setSize(queryParam.getPageSize());

        if (wrapper == null) {
            wrapper = getQueryWrapper();
        }
        initQueryWrapper(wrapper, queryParam);

        return getMapper().selectPage(pageParam, wrapper);
    }


    default <P extends PaginationRequest>  IPage<T> page(P queryParam) {
        return page(queryParam, null);
    }
}
