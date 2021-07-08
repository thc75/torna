package cn.torna.manager.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author tanghc
 */
@Service
public class TornaTransactionManager {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    /**
     * 手动开启事务处理，处理失败回滚事务
     * @param runner 处理业务
     * @param onError 出错时调用
     * @param <T> 返回参数类型
     * @return 返回处理结果
     */
    public <T> T execute(Supplier<T> runner, Consumer<Exception> onError) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("torna-push");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            T result = runner.get();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            onError.accept(e);
            return null;
        }
    }

}
