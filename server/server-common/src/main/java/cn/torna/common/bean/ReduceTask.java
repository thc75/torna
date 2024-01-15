package cn.torna.common.bean;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

/**
 * @author thc
 */
@AllArgsConstructor
public class ReduceTask<T> extends RecursiveTask<Void> {

    private static final long serialVersionUID = 1989952553733755887L;

    private List<T> list;

    // 计算的起始位置
    private int startIndex;
    // 计算的结束位置
    private int endIndex;

    // 每次处理几条数据
    private int executeSize;

    // 执行器
    private Consumer<List<T>> executor;

    @Override
    protected Void compute() {
        int len = endIndex - startIndex;
        // 数组长度小于n，无法拆分，开始运算
        if (len <= executeSize) {
            return execute();
        }
        // 拆分左边的子任务
        ReduceTask<T> leftTask = new ReduceTask<>(list, startIndex, startIndex + executeSize, executeSize, executor);
        // 将子任务加入到ForkJoinPool中去
        leftTask.fork();
        // 创建右边的任务
        ReduceTask<T> rightTask = new ReduceTask<>(list, startIndex + executeSize, endIndex, executeSize, executor);
        // 执行右边的任务
        rightTask.compute();
        // 读取左边的子任务结果，这里会阻塞
        leftTask.join();
        return null;
    }


    private Void execute() {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> extList = list.subList(startIndex, endIndex);
        executor.accept(extList);
        return null;
    }
}
