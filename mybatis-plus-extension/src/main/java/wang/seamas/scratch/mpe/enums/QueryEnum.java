package wang.seamas.scratch.mpe.enums;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import wang.seamas.scratch.mpe.interfaces.WrapQuery;

import java.util.Collection;
import java.util.Collections;

@Slf4j
public enum QueryEnum implements WrapQuery {

    EQ {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.eq(StringUtils.hasText(s), column, value);
            } else {
                wrapper.eq(value != null, column, value);
            }
        }
    },

    NE {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.ne(StringUtils.hasText(s), column, value);
            } else {
                wrapper.ne(value != null, column, value);
            }
        }
    },
    LIKE {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.like(StringUtils.hasText(s), column, value);
            }
        }
    },
    NOT_LIKE {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.notLike(StringUtils.hasText(s), column, value);
            }
        }
    },
    LEFT_LIKE{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.likeLeft(StringUtils.hasText(s), column, value);
            }
        }
    },
    NOT_LEFT_LIKE{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.notLikeLeft(StringUtils.hasText(s), column, value);
            }
        }
    },
    RIGHT_LIKE{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.likeRight(StringUtils.hasText(s), column, value);
            }
        }
    },
    NOT_RIGHT_LIKE{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.notLikeRight(StringUtils.hasText(s), column, value);
            }
        }
    },
    GT{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.gt(StringUtils.hasText(s), column, value);
            } else {
                wrapper.gt(value != null, column, value);
            }
        }
    },
    GE {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.ge(StringUtils.hasText(s), column, value);
            } else {
                wrapper.ge(value != null, column, value);
            }
        }
    },

    LT{
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.lt(StringUtils.hasText(s), column, value);
            } else {
                wrapper.lt(value != null, column, value);
            }
        }
    },
    LE {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value instanceof String s) {
                wrapper.le(StringUtils.hasText(s), column, value);
            } else {
                wrapper.le(value != null, column, value);
            }
        }
    },
    IN {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value != null) {
                if (value instanceof Collection<?> c)  {
                    wrapper.in(!c.isEmpty(), column, c);
                } else if (value.getClass().isArray()) {
                    Object[] array = (Object[]) value;
                    wrapper.in(array.length > 0, column, (Object[]) value);
                } else {
                    wrapper.in(column, Collections.singletonList(value));
                }
            }
        }
    },
    NOT_IN {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value != null) {
                if (value instanceof Collection<?> c)  {
                    wrapper.notIn(!c.isEmpty(), column, c);
                } else if (value.getClass().isArray()) {
                    Object[] array = (Object[]) value;
                    wrapper.notIn(array.length > 0, column, (Object[]) value);
                } else {
                    wrapper.notIn(column, Collections.singletonList(value));
                }
            }
        }
    },
    BETWEEN {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value != null) {
                if (value instanceof Collection<?> c) {
                    Object[] array = c.toArray(new Object[0]);
                    if (array.length == 2) {
                        wrapper.between(column, array[0], array[1]);
                    } else {
                        log.info("between expect 2 parameters, but got :{}", array.length);
                    }
                } else if (value.getClass().isArray()) {
                    Object[] array = (Object[]) value;
                    if (array.length == 2) {
                        wrapper.between(column, array[0], array[1]);
                    } else {
                        log.info("between expect 2 parameters, but got :{}", array.length);
                    }
                } else {
                    log.info("between expect collection or array, but got :{}", value.getClass());
                }
            }
        }
    },
    NOT_BETWEEN {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            if (value != null) {
                if (value instanceof Collection<?> c) {
                    Object[] array = c.toArray(new Object[0]);
                    if (array.length == 2) {
                        wrapper.notBetween(column, array[0], array[1]);
                    } else {
                        log.info("not between expect 2 parameters, but got :{}", array.length);
                    }
                } else if (value.getClass().isArray()) {
                    Object[] array = (Object[]) value;
                    if (array.length == 2) {
                        wrapper.notBetween(column, array[0], array[1]);
                    } else {
                        log.info("not between expect 2 parameters, but got :{}", array.length);
                    }
                } else {
                    log.info("not between expect collection or array, but got :{}", value.getClass());
                }
            }
        }
    },
    IS_NULL {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            wrapper.isNull(value != null, column);
        }

    },
    NOT_NULL {
        @Override
        public void wrap(QueryWrapper<?> wrapper, String column, Object value) {
            wrapper.isNotNull(value != null, column);
        }
    };

}
