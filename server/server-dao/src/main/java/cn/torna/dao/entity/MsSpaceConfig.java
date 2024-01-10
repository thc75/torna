package cn.torna.dao.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;


/**
 * 表名：ms_space_config
 * 备注：MeterSphere空间配置
 *
 * @author thc
 */
@Table(name = "ms_space_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class MsSpaceConfig {

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long id;

    /**
     * MeterSphere的access_key
     */
    private String msAccessKey;

    /**
     * MeterSphere服务器地址
     */
    private String msAddress;

    /**
     * MeterSphere的secret_key
     */
    private String msSecretKey;

    /**
     * MeterSphere空间id
     */
    private String msSpaceId;

    /**
     * 空间名称
     */
    private String msSpaceName;

    /**
     * 空间id
     */
    private Long spaceId;


}
