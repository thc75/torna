package cn.torna.dao.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;

import lombok.Data;


/**
 * 表名：ms_module_config
 * 备注：MeterSphere模块配置
 *
 * @author thc
 */
@Table(name = "ms_module_config", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class MsModuleConfig {

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long id;

    /**
     * module.id
     */
    private Long moduleId;

    /**
     * 默认覆盖
     */
    private Byte msCoverModule;

    /**
     * MeterSphere模块id
     */
    private String msModuleId;

    /**
     * MeterSphere项目id
     */
    private String msProjectId;

    private String name;


}
