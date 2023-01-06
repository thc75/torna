package cn.torna.service;

import cn.torna.common.enums.DocStatusEnum;
import cn.torna.service.dto.SystemConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 升级
 * @author tanghc
 */
@Service
@Slf4j
public class UpgradeProService extends UpgradeService {

    private static final int PRO_VERSION = 20000;

    private static final String TORNA_PRO_VERSION_KEY = "tornapro.version";

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 升级
     */
    @Transactional(rollbackFor = Exception.class)
    public void upgrade() {
        upgradePro();
    }

    /**
     * 企业版升级
     */
    private void upgradePro() {
        int oldVersion = getProVersion();
        doUpgradePro(oldVersion);
        // 最后更新当前版本到数据库
        saveProVersion(oldVersion);
    }


    private void doUpgradePro(int oldVersion) {
        if (oldVersion < 20000) {
            createTable("doc_snapshot", "upgrade/pro/2.0_ddl_doc_snapshot.txt");
            createTable("debug_script", "upgrade/pro/2.0_ddl_debug_script.txt");
            createTable("doc_diff_record", "upgrade/pro/2.0_ddl_doc_diff_record.txt");
            createTable("doc_diff_detail", "upgrade/pro/2.0_ddl_doc_diff_detail.txt");
            addColumn("doc_info", "status", "ALTER TABLE `doc_info` ADD COLUMN `status` TINYINT NULL DEFAULT '" + DocStatusEnum.TODO.getStatus() + "' COMMENT '文档状态' AFTER `is_locked`");
            runSql("UPDATE doc_info SET status=" + DocStatusEnum.DONE.getStatus());
        }
    }

    private void saveProVersion(int oldVersion) {
        if (oldVersion != PRO_VERSION) {
            SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
            systemConfigDTO.setConfigKey(TORNA_PRO_VERSION_KEY);
            systemConfigDTO.setConfigValue(String.valueOf(PRO_VERSION));
            systemConfigDTO.setRemark("当前内部版本号。不要删除这条记录！！");
            systemConfigService.setConfig(systemConfigDTO);
        }
    }

    private int getProVersion() {
        String version = systemConfigService.getConfigValue(TORNA_PRO_VERSION_KEY, "0");
        return NumberUtils.toInt(version);
    }


}
