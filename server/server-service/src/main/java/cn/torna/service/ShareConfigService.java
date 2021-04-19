package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.ShareConfigTypeEnum;
import cn.torna.common.enums.StatusEnum;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.PasswordUtil;
import cn.torna.dao.entity.ShareConfig;
import cn.torna.dao.entity.ShareContent;
import cn.torna.dao.mapper.ShareConfigMapper;
import cn.torna.service.dto.ShareConfigDTO;
import com.gitee.fastmybatis.core.query.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@Service
public class ShareConfigService extends BaseService<ShareConfig, ShareConfigMapper> {

    @Autowired
    private ShareContentService shareContentService;

    public void add(ShareConfigDTO shareConfigDTO, User user) {
        ShareConfig shareConfig = new ShareConfig();
        shareConfig.setModuleId(shareConfigDTO.getModuleId());
        shareConfig.setType(shareConfigDTO.getType());
        shareConfig.setIsAll(shareConfigDTO.getIsAll());
        shareConfig.setRemark(shareConfigDTO.getRemark());
        shareConfig.setCreatorName(user.getNickname());
        shareConfig.setStatus(StatusEnum.ENABLE.getStatus());
        if (shareConfigDTO.getType() == ShareConfigTypeEnum.ENCRYPT.getType()) {
            String pwd = PasswordUtil.getRandomSimplePassword(4);
            shareConfig.setPassword(pwd);
        } else {
            shareConfig.setPassword("");
        }
        this.save(shareConfig);
        this.saveContent(shareConfig, shareConfigDTO);
    }

    public void update(ShareConfigDTO shareConfigDTO) {
        ShareConfig shareConfig = this.getById(shareConfigDTO.getId());
        shareConfig.setModuleId(shareConfigDTO.getModuleId());
        shareConfig.setType(shareConfigDTO.getType());
        shareConfig.setIsAll(shareConfigDTO.getIsAll());
        shareConfig.setRemark(shareConfigDTO.getRemark());
        if (shareConfigDTO.getType() == ShareConfigTypeEnum.ENCRYPT.getType()) {
            if (StringUtils.isBlank(shareConfig.getPassword())) {
                String pwd = PasswordUtil.getRandomSimplePassword(4);
                shareConfig.setPassword(pwd);
            }
        } else {
            shareConfig.setPassword("");
        }
        this.update(shareConfig);
        this.saveContent(shareConfig, shareConfigDTO);
    }

    public List<ShareContent> listContent(long id) {
        return shareContentService.list("share_config_id", id);
    }


    private void saveContent(ShareConfig shareConfig, ShareConfigDTO shareConfigDTO) {
        // 先删除之前的
        this.deleteContent(shareConfig);
        if (shareConfigDTO.getIsAll() == Booleans.TRUE) {
            return;
        }
        Date now = new Date();
        List<ShareContent> tobeSave = shareConfigDTO.getContent()
                .stream()
                .map(content -> {
                    ShareContent shareContent = new ShareContent();
                    Long parentId = content.getParentId();
                    if (parentId == null) {
                        parentId = 0L;
                    }
                    shareContent.setDocId(content.getDocId());
                    shareContent.setParentId(parentId);
                    shareContent.setIsShareFolder(content.getIsShareFolder());
                    shareContent.setShareConfigId(shareConfig.getId());
                    shareContent.setIsDeleted(Booleans.FALSE);
                    shareContent.setGmtCreate(now);
                    shareContent.setGmtModified(now);
                    return shareContent;
                })
                .collect(Collectors.toList());
        if (tobeSave.isEmpty()) {
            return;
        }
        shareContentService.getMapper().saveBatch(tobeSave);
    }

    private void deleteContent(ShareConfig shareConfig) {
        Query query = new Query()
                .eq("share_config_id", shareConfig.getId());
        shareContentService.getMapper().deleteByQuery(query);
    }


}