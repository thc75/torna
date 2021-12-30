package cn.torna.common.util;

import cn.torna.common.bean.TreeAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author tanghc
 */
public class TreeUtil {

    /**
     * list转换成tree
     * @param list
     * @param parentId 当前父节点id
     * @param <T>
     * @return
     */
    public static <T extends TreeAware<T, ?>> List<T> convertTree(List<T> list, Object parentId) {
        if (parentId == null) {
            parentId = 0L;
        }
        if (list == null) {
            return Collections.emptyList();
        }
        List<T> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            if (Objects.equals(item.getParentId(), parentId)) {
                List<T> children = convertTree(list, item.getId());
                item.setChildren(children);
                temp.add(item);
            }
        }
        return temp;
    }

    public static <I, T extends TreeAware<T, I>> void initParent(List<T> list, I parentId, Map<I, T> idMap) {
        Objects.requireNonNull(parentId, "parentId can not null");
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);
            if (Objects.equals(item.getParentId(), parentId)) {
                T parent = idMap.get(item.getParentId());
                item.setParent(parent);
                initParent(list, item.getId(), idMap);
            }
        }
    }
}
