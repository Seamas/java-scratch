package wang.seamas.scratch.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CommonReflectionUtil {

    private static final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>(); // Changed from private static Map<Class, Field[]> fieldCache = new ConcurrentHashMap<>();


    public static Field[] getAllFields(Class<?> clazz) {
        if (!fieldCache.containsKey(clazz)) {
            fieldCache.put(clazz, fetchFields(clazz.getSuperclass()));
        }
        return fieldCache.get(clazz);
    }

    private static Field[] fetchFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, fields::add);
        return fields.toArray(new Field[0]);
    }
}
