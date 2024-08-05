package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.UserSubscribeTypeEnum;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.IdUtil;
import cn.torna.dao.entity.*;
import cn.torna.dao.mapper.ProjectReleaseDocMapper;
import cn.torna.dao.mapper.ProjectReleaseMapper;
import cn.torna.service.dto.ProjectReleaseBindDocDTO;
import cn.torna.service.dto.ProjectReleaseDTO;
import com.gitee.fastmybatis.core.query.LambdaQuery;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qiuyu
 */
@Service
public class ProjectReleaseService extends BaseLambdaService<ProjectRelease, ProjectReleaseMapper> {

    @Resource
    private ProjectReleaseMapper projectReleaseMapper;
    @Resource
    private ProjectReleaseDocMapper projectReleaseDocMapper;
    @Resource
    private UserSubscribeService userSubscribeService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private DocInfoService docInfoService;

    /**
     * 版本列表
     *
     * @param userId    当前用户id
     * @param projectId 项目id
     * @param releaseNo 版本号（查询参数）
     * @param status    版本状态（查询参数）
     * @return List<ProjectReleaseDTO>
     * @author qiuyu
     **/
    public List<ProjectReleaseDTO> pageProjectRelease(long userId, long projectId, String releaseNo, Integer status) {

        Query projectReleaseQuery = LambdaQuery.create(ProjectRelease.class)
                .eq(ProjectRelease::getProjectId, projectId)
                .like(StringUtils.hasText(releaseNo), ProjectRelease::getReleaseNo, releaseNo)
                .eq(Objects.nonNull(status), ProjectRelease::getStatus, status)
                .orderByDesc(ProjectRelease::getId);

        List<ProjectRelease> projectReleases = projectReleaseMapper.list(projectReleaseQuery);
        List<ProjectReleaseDTO> projectReleaseDTOS = CopyUtil.copyList(projectReleases, ProjectReleaseDTO::new);
        if (CollectionUtils.isEmpty(projectReleaseDTOS)) {
            return new ArrayList<>(0);
        }
        // 查询版本关联的文档
        List<ProjectReleaseDoc> projectReleaseDocs = projectReleaseDocMapper.list(
                LambdaQuery.create(ProjectReleaseDoc.class)
                        .eq(ProjectReleaseDoc::getProjectId, projectId));
        Map<Long, List<ProjectReleaseDoc>> releaseIdMap = new HashMap<>(0);
        if (!CollectionUtils.isEmpty(projectReleaseDocs)) {
            releaseIdMap = projectReleaseDocs.stream().collect(Collectors.groupingBy(ProjectReleaseDoc::getReleaseId));
        }
        // 查询用户版本订阅列表
        List<UserSubscribe> userSubscribes = userSubscribeService.listUserSubscribe(userId, UserSubscribeTypeEnum.RELEASE);
        Map<Long, Long> sourceIdMap = new HashMap<>(0);
        if (!CollectionUtils.isEmpty(userSubscribes)) {
            sourceIdMap = userSubscribes.stream().collect(Collectors.toMap(
                    UserSubscribe::getSourceId,
                    UserSubscribe::getUserId,
                    (v1, v2) -> v2));
        }
        // 遍历封装版本信息
        Map<Long, Long> finalSourceIdMap = sourceIdMap;
        Map<Long, List<ProjectReleaseDoc>> finalReleaseIdMap = releaseIdMap;
        projectReleaseDTOS.forEach(projectReleaseDTO -> {
            // 设置添加 关联文档
            List<ProjectReleaseDoc> listByReleaseId = finalReleaseIdMap.get(projectReleaseDTO.getId());
            if (!CollectionUtils.isEmpty(listByReleaseId)) {
                Map<Long, List<Long>> map = listByReleaseId.stream().collect(
                        Collectors.groupingBy(ProjectReleaseDoc::getModuleId,
                                Collectors.mapping(ProjectReleaseDoc::getSourceId, Collectors.toList())));
                projectReleaseDTO.setModuleSourceIdMap(toEncode(map));
            } else {
                projectReleaseDTO.setModuleSourceIdMap(new HashMap<>(0));
            }
            // 封装关注状态
            projectReleaseDTO.setIsSubscribe(finalSourceIdMap.get(projectReleaseDTO.getId()) != null);

        });
        return projectReleaseDTOS;
    }

    /**
     * 将Map中的Id转换为HashId
     *
     * @param map key和value均为Long类型的id
     * @return Map<List < String>>
     * @author qiuyu
     **/
    private Map<String, List<String>> toEncode(Map<Long, List<Long>> map) {
        Map<String, List<String>> encodedMap = new HashMap<>();
        if (CollectionUtils.isEmpty(map)) {
            return encodedMap;
        }
        map.forEach((k, v) -> {
            String encodedKey = IdUtil.encode(k);
            List<String> encodedValues = v.stream()
                    .map(IdUtil::encode)
                    .collect(Collectors.toList());
            encodedMap.put(encodedKey, encodedValues);
        });
        return encodedMap;
    }

    /**
     * 新增项目版本
     *
     * @param projectId         项目id
     * @param releaseNo         版本号
     * @param releaseDesc       版本描述
     * @param status            版本状态
     * @param dingdingWebhook   钉钉机器人webhook
     * @param moduleSourceIdMap 绑定的模块映射的文档集合
     * @author qiuyu
     **/
    @Transactional(rollbackFor = Exception.class)
    public void addProjectRelease(long projectId, String releaseNo, String releaseDesc,
                                  int status, String dingdingWebhook, Map<String, List<String>> moduleSourceIdMap) {
        Query projectReleaseQuery = LambdaQuery.create(ProjectRelease.class)
                .eq(ProjectRelease::getProjectId, projectId)
                .eq(ProjectRelease::getReleaseNo, releaseNo);
        ProjectRelease byQuery = projectReleaseMapper.get(projectReleaseQuery);
        if (byQuery != null) {
            throw new BizException("该版本号在此项目已存在");
        }
        ProjectRelease projectRelease = new ProjectRelease();
        projectRelease.setProjectId(projectId);
        projectRelease.setReleaseNo(releaseNo);
        projectRelease.setReleaseDesc(releaseDesc);
        projectRelease.setStatus(status == 1 ? 1 : 0);
        projectRelease.setDingdingWebhook(dingdingWebhook);
        projectRelease.setIsDeleted(Booleans.FALSE);
        projectReleaseMapper.save(projectRelease);
        // 保存关联
        saveBatchReleaseDocs(projectId, projectRelease.getId(), moduleSourceIdMap);
    }

    /**
     * 修改项目版本
     *
     * @param id                版本id
     * @param releaseDesc       版本描述
     * @param status            版本状态
     * @param dingdingWebhook   钉钉机器人webhook
     * @param moduleSourceIdMap 绑定的模块映射的文档集合
     * @author qiuyu
     **/
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectRelease(long id, String releaseDesc, int status, String dingdingWebhook,
                                     Map<String, List<String>> moduleSourceIdMap) {
        ProjectRelease projectRelease = projectReleaseMapper.getById(id);
        if (projectRelease == null) {
            throw new BizException("该版本号在此项目不存在");
        }
        projectRelease.setReleaseDesc(releaseDesc);
        projectRelease.setStatus(status == 1 ? 1 : 0);
        projectRelease.setDingdingWebhook(dingdingWebhook);
        projectReleaseMapper.update(projectRelease);

        Query deleteQuery = LambdaQuery.create(ProjectReleaseDoc.class)
                .eq(ProjectReleaseDoc::getProjectId, projectRelease.getProjectId())
                .eq(ProjectReleaseDoc::getReleaseId, projectRelease.getId());
        projectReleaseDocMapper.deleteByQuery(deleteQuery);
        // 保存关联
        saveBatchReleaseDocs(projectRelease.getProjectId(), projectRelease.getId(), moduleSourceIdMap);
    }

    /**
     * 修改版本状态
     *
     * @param id     版本id
     * @param status 状态
     * @author qiuyu
     **/
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(long id, int status) {
        ProjectRelease projectRelease = projectReleaseMapper.getById(id);
        if (projectRelease == null) {
            throw new BizException("该版本号在此项目不存在");
        }
        projectRelease.setStatus(status == 1 ? 1 : 0);
        projectReleaseMapper.update(projectRelease);
    }

    /**
     * 删除记录
     *
     * @param id 版本id
     * @author qiuyu
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByReleaseId(long id) {
        ProjectRelease projectRelease = projectReleaseMapper.getById(id);
        if (projectRelease == null) {
            throw new BizException("无效的版本ID");
        }
        projectReleaseMapper.deleteById(id);
        Query deleteQuery = LambdaQuery.create(ProjectReleaseDoc.class)
                .eq(ProjectReleaseDoc::getProjectId, projectRelease.getProjectId())
                .eq(ProjectReleaseDoc::getReleaseId, projectRelease.getId());
        projectReleaseDocMapper.deleteByQuery(deleteQuery);
        // 取消版本的关注记录
        userSubscribeService.cancelSubscribe(UserSubscribeTypeEnum.RELEASE, projectRelease.getId());
    }


    /**
     * 查看绑定文档
     *
     * @param projectId 项目id
     * @param releaseId 版本id
     * @author qy
     **/
    public List<ProjectReleaseBindDocDTO> bindList(long projectId, long releaseId) {
        List<ProjectReleaseBindDocDTO> result = new ArrayList<>();
        Query query = LambdaQuery.create(ProjectReleaseDoc.class)
                .eq(ProjectReleaseDoc::getProjectId, projectId)
                .eq(ProjectReleaseDoc::getReleaseId, releaseId);
        List<ProjectReleaseDoc> list = projectReleaseDocMapper.list(query);
        if (!CollectionUtils.isEmpty(list)) {
            Map<Long, List<ProjectReleaseDoc>> muduleId2ListMap = list.stream().collect(Collectors.groupingBy(ProjectReleaseDoc::getModuleId));

            // 获取模块名称
            Set<Long> moduleIds = muduleId2ListMap.keySet();
            List<Module> modules = moduleService.list(LambdaQuery.create(Module.class).in(Module::getId, moduleIds));
            Map<Long, String> moduleId2Name = modules.stream().collect(Collectors.toMap(Module::getId, Module::getName, (v1, v2) -> v2));

            // 获取接口名称
            List<Long> docIds = list.stream().map(ProjectReleaseDoc::getSourceId).distinct().collect(Collectors.toList());
            List<DocInfo> docInfos = docInfoService.list(LambdaQuery.create(DocInfo.class).in(DocInfo::getId, docIds));
            Map<Long, DocInfo> docInfoMap = docInfos.stream().collect(Collectors.toMap(DocInfo::getId, Function.identity(), (v1, v2) -> v2));

            // 封装关联文档
            if (!CollectionUtils.isEmpty(moduleId2Name)) {
                moduleId2Name.forEach((k, v) -> {
                    ProjectReleaseBindDocDTO module = new ProjectReleaseBindDocDTO(releaseId);
                    module.setIsFolder(1);
                    module.setId(k);
                    module.setLabel(v);
                    muduleId2ListMap.get(k).forEach(releaseDoc ->{
                        ProjectReleaseBindDocDTO doc = new ProjectReleaseBindDocDTO(releaseId);
                        DocInfo docInfo = docInfoMap.get(releaseDoc.getSourceId());
                        // 只展示接口
                        if (docInfo.getIsFolder() == 0) {
                            doc.setIsFolder(0);
                            doc.setId(docInfo.getId());
                            doc.setLabel(String.format("%s 【%s】 %s",docInfo.getName(), docInfo.getHttpMethod(), docInfo.getUrl()));
                            module.getChildren().add(doc);
                        }
                    });
                    result.add(module);
                });
            }
        }
        return result;
    }

    /**
     * 批量保存版本关联的文档
     *
     * @param projectId         项目id
     * @param releaseId         版本id
     * @param moduleSourceIdMap 绑定的模块映射的文档集合
     * @author qiuyu
     **/
    private void saveBatchReleaseDocs(long projectId, long releaseId, Map<String, List<String>> moduleSourceIdMap) {
        if (!CollectionUtils.isEmpty(moduleSourceIdMap)) {
            List<ProjectReleaseDoc> projectReleaseDocs = new ArrayList<>();
            moduleSourceIdMap.forEach((k, v) -> {
                // 模块id
                Long moduleId = IdUtil.decode(k);
                // 文档ids
                List<Long> sourceIdList = v.stream().distinct().map(IdUtil::decode).filter(Objects::nonNull).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(sourceIdList)) {
                    List<ProjectReleaseDoc> collect = sourceIdList.stream().distinct().map(sourceId -> {
                        ProjectReleaseDoc projectReleaseDoc = new ProjectReleaseDoc();
                        projectReleaseDoc.setProjectId(projectId);
                        projectReleaseDoc.setReleaseId(releaseId);
                        projectReleaseDoc.setModuleId(moduleId);
                        projectReleaseDoc.setSourceId(sourceId);
                        projectReleaseDoc.setIsDeleted(Booleans.FALSE);
                        return projectReleaseDoc;
                    }).collect(Collectors.toList());
                    projectReleaseDocs.addAll(collect);
                }
            });

            // 批量保存
            if (!CollectionUtils.isEmpty(projectReleaseDocs)) {
                projectReleaseDocMapper.saveBatch(projectReleaseDocs);
            }
        }
    }

    /**
     * 获取关联的有效版本
     *
     * @param projectId 项目id
     * @param sourceId  文档id
     * @return List<ProjectReleaseDTO>
     * @author qiuyu
     **/
    public List<ProjectReleaseDTO> relatedValidReleases(long projectId, long sourceId) {
        Query projectReleaseDocQuery = LambdaQuery.create(ProjectReleaseDoc.class)
                .eq(ProjectReleaseDoc::getProjectId, projectId)
                .eq(ProjectReleaseDoc::getSourceId, sourceId);
        List<ProjectReleaseDoc> projectReleaseDocs = projectReleaseDocMapper.list(projectReleaseDocQuery);
        if (CollectionUtils.isEmpty(projectReleaseDocs)) {
            return new ArrayList<>(0);
        }
        List<Long> releaseIds = projectReleaseDocs.stream().map(ProjectReleaseDoc::getReleaseId).collect(Collectors.toList());
        Query projectReleasesQuery = LambdaQuery.create(ProjectRelease.class)
                .eq(ProjectRelease::getProjectId, projectId)
                .eq(ProjectRelease::getStatus, 1)
                .in(ProjectRelease::getId, releaseIds);
        List<ProjectRelease> projectReleases = projectReleaseMapper.list(projectReleasesQuery);
        if (CollectionUtils.isEmpty(projectReleases)) {
            return new ArrayList<>(0);
        }
        return CopyUtil.copyList(projectReleases, ProjectReleaseDTO::new);
    }

}
