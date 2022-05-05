package cn.torna.dao.entity;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分享环境
 *
 * @author Joker
 * @since 1.0.0
 * @return
 */
@Table(name = "share_environment", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareEnvironment {

    /**  数据库字段：id */
    private Long id;


    /**
     * 分享配置id
     */
    private Long shareConfigId;

    /**
     * 模块环境id
     */
    private Long moduleEnvironmentId;
}
