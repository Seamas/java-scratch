package wang.seamas.scratch.mpe.interfaces;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public interface WrapQuery {

    void wrap(QueryWrapper<?> wrapper, String column, Object value);
}
