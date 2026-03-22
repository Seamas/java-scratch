package wang.seamas.scratch.mpe.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import wang.seamas.scratch.dto.PaginationResponse;

import java.util.function.Function;
import java.util.stream.Collectors;

public class IPageToPagination {

    public static <T, X> PaginationResponse<T> toPagination(IPage<X> page, Function<X, T> apply) {
        return new PaginationResponse<T>(page.getRecords().stream().map(apply).collect(Collectors.toList()),
                (int)page.getTotal(),
                (int)page.getCurrent(),
                (int)page.getSize());
    }
}
