package cn.torna.service;


import cn.torna.service.dto.DocDiffDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author thc
 */
public class DocDiffContext {

    private static final Queue<DocDiffDTO> QUEUE = new LinkedBlockingQueue<>();


    public static void addQueue(DocDiffDTO docDiff) {
        QUEUE.offer(docDiff);
    }

    public static List<DocDiffDTO> poll(int limit) {
        if (QUEUE.isEmpty()) {
            return Collections.emptyList();
        }
        int size = Math.min(QUEUE.size(), limit);
        List<DocDiffDTO> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(QUEUE.poll());
        }
        return list;
    }

}
