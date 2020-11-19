package torna.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Slf4j
public class CopyUtil {

    public static void copyProperties(Object from, Object to) {
        BeanUtils.copyProperties(from, to);
    }

    public static <T> T copyBean(Object from, Supplier<T> supplier) {
        Objects.requireNonNull(from);
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static <T> T copyBeanNullable(Object from, Supplier<T> supplier) {
        if (from == null) {
            return supplier.get();
        }
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static <T> T copyBean(Object from, Supplier<T> supplier, Consumer<T> after) {
        if (from == null) {
            return null;
        }
        T to = supplier.get();
        BeanUtils.copyProperties(from, to);
        after.accept(to);
        return to;
    }

    public static <T> List<T> copyList(List<?> fromList, Supplier<T> toElement) {
        if (fromList == null) {
            return Collections.emptyList();
        }
        return fromList.stream()
                .map(source -> {
                    T target = toElement.get();
                    BeanUtils.copyProperties(source, target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    public static <E, R> List<R> copyList(List<E> fromList, Function<E, R> function) {
        if (fromList == null) {
            return Collections.emptyList();
        }
        return fromList.stream()
                .map(source -> {
                    R target = function.apply(source);
                    BeanUtils.copyProperties(source, target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    public static <T> List<T> copyList(List<?> fromList, Supplier<T> toElement, Consumer<T> after) {
        if (fromList == null) {
            return Collections.emptyList();
        }
        return fromList.stream()
                .map(source -> {
                    T target = toElement.get();
                    BeanUtils.copyProperties(source, target);
                    after.accept(target);
                    return target;
                })
                .collect(Collectors.toList());
    }
}
