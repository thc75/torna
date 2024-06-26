package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.common.bean.ReduceTask;
import cn.torna.common.enums.MockRequestDataTypeEnum;
import cn.torna.common.enums.MockResultTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.MockConfig;
import cn.torna.dao.mapper.MockConfigMapper;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.NameValueDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;


/**
 * @author tanghc
 */
@Service
@Slf4j
public class MockConfigService extends BaseService<MockConfig, MockConfigMapper> {

    public static final String DEFAULT_DATA_ID = "default";

    @Resource
    @Lazy
    private DocInfoService docInfoService;


    public List<MockConfig> listMockConfig(Long docId) {
        return this.list("doc_id", docId);
    }

    public MockConfig getByDataId(String dataId) {
        return get("data_id", dataId);
    }

    public static String getDataKvContent(List<NameValueDTO> dataKv) {
        if (CollectionUtils.isEmpty(dataKv)) {
            return "[]";
        }
        dataKv.sort(Comparator.comparing(NameValueDTO::getName));
        return JSON.toJSONString(dataKv);
    }

    @Async
    public void createModuleDocDefaultMock(Long moduleId) {
        int poolSize = EnvironmentKeys.TORNA_PUSH_PROCESS_NUM.getInt();
        int executeSize = EnvironmentKeys.TORNA_PUSH_EXECUTE_SIZE.getInt();
        ForkJoinPool forkJoinPool = new ForkJoinPool(poolSize);

        List<Long> docIds = docInfoService.listBySpecifiedColumns(Collections.singletonList("id"), Query.create().eq("module_id", moduleId), Long.class);
        ReduceTask<Long> reduceTask = new ReduceTask<>(docIds, 0, docIds.size(), executeSize, subList -> {
            for (Long docId : subList) {
                this.createDocDefaultMock(docId);
            }
        });
        forkJoinPool.invoke(reduceTask);
    }

    public int getNextVersion(Long docId) {
        Query query = Query.create().eq("doc_id", docId)
                .orderby("version", Sort.DESC);
        MockConfig mockConfig = get(query);
        return Optional.ofNullable(mockConfig).map(MockConfig::getVersion).orElse(0) + 1;
    }

    @Async
    public void createDocDefaultMock(Long docId) {
        DocInfoDTO docInfoDTO = docInfoService.getDocDetail(docId);
        Query query = new Query()
                .eq("doc_id", docId)
                .eq("data_id", DEFAULT_DATA_ID);
        MockConfig mockConfig = this.get(query);
        boolean save = false;
        if (mockConfig == null) {
            save = true;
            mockConfig = new MockConfig();
            mockConfig.setName(docInfoDTO.getDocName() + "-default");
            mockConfig.setDataId(DEFAULT_DATA_ID);
            mockConfig.setVersion(0);
            mockConfig.setPath("default/" + StringUtils.trimLeadingCharacter(docInfoDTO.getUrl(), '/'));
            mockConfig.setIp("");
            mockConfig.setRequestData("[]");
            mockConfig.setRequestDataType(MockRequestDataTypeEnum.JSON.getType());
            mockConfig.setHttpStatus(200);
            mockConfig.setDelayMills(0);
            mockConfig.setResultType(MockResultTypeEnum.CUSTOM.getType());
            mockConfig.setResponseHeaders("[{\"name\":\"Content-Type\",\"value\":\"application/json;charset=UTF-8\"}]");
            String responseJson = buildResponseJson(docInfoDTO);
            mockConfig.setResponseBody(responseJson);
            mockConfig.setDocId(docInfoDTO.getId());
            mockConfig.setRemark("系统插入");
            mockConfig.setCreatorId(0L);
            mockConfig.setCreatorName("sys");
            mockConfig.setModifierId(0L);
            mockConfig.setModifierName("sys");
        } else {
            String responseBody = mergeResponseBody(docInfoDTO, mockConfig.getResponseBody());
            mockConfig.setResponseBody(responseBody);
        }

        if (save) {
            this.save(mockConfig);
        } else {
            this.update(mockConfig);
        }
    }

    private String buildResponseJson(DocInfoDTO docInfoDTO) {
        String responseJson = ConvertService.buildJson(docInfoDTO.getResponseParams(), true);
        Byte isResponseArray = docInfoDTO.getIsResponseArray();
        if (Objects.equals(isResponseArray, Booleans.TRUE)) {
            responseJson = "[" + responseJson + "]";
        }
        return responseJson;
    }

    private String mergeResponseBody(DocInfoDTO docInfoDTO, String oldResponseBody) {
        try {
            String responseJson = buildResponseJson(docInfoDTO);
            if (StringUtils.isEmpty(oldResponseBody) || Objects.equals(responseJson, oldResponseBody)) {
                return responseJson;
            }
            oldResponseBody = oldResponseBody.trim();
            // [] {}
            // 判断第一字符是否一样，不一样用新的
            if (!responseJson.substring(0, 1).equals(oldResponseBody.substring(0, 1))) {
                return responseJson;
            }
            if (responseJson.startsWith("[")) {
                JSONArray arrayNew = JSON.parseArray(responseJson);
                JSONArray arrayOld = JSON.parseArray(oldResponseBody);
                if (arrayOld.isEmpty() || arrayNew.isEmpty()) {
                    return arrayNew.toString(SerializerFeature.PrettyFormat);
                }
                return mergeJsonObj(arrayNew.getJSONObject(0), arrayOld.getJSONObject(0));
            } else {
                JSONObject jsonNew = JSON.parseObject(responseJson);
                JSONObject jsonOld = JSON.parseObject(oldResponseBody);
                return mergeJsonObj(jsonNew, jsonOld);
            }
        } catch (Exception e) {
            log.error("mergeResponseBody error", e);
            return oldResponseBody;
        }
    }

    private String mergeJsonObj(JSONObject jsonNew, JSONObject jsonOld) {
        for (Map.Entry<String, Object> entryNew : jsonNew.entrySet()) {
            String key = entryNew.getKey();
            Object valueNew = entryNew.getValue();
            // 如果是新加的key
            if (!jsonOld.containsKey(key)) {
                jsonOld.put(key, valueNew);
            } else {
                // 如果已存在，递归调用子节点
                Object valueOld = jsonOld.get(key);
                // 类型不一样，用新的
                if (valueNew.getClass() != valueOld.getClass()) {
                    jsonOld.put(key, valueNew);
                } else if ((valueOld instanceof JSONObject) && (valueNew instanceof JSONObject)) {
                    JSONObject jsonNewChild = (JSONObject) valueNew;
                    JSONObject jsonOldChild = (JSONObject) valueOld;
                    mergeJsonObj(jsonNewChild, jsonOldChild);
                }
            }
        }
        removeKey(jsonNew, jsonOld);
        return jsonOld.toString(SerializerFeature.PrettyFormat);
    }

    private void removeKey(JSONObject jsonNew, JSONObject jsonOld) {
        Iterator<Map.Entry<String, Object>> iteratorOld = jsonOld.entrySet().iterator();

        while (iteratorOld.hasNext()) {
            Map.Entry<String, Object> entryOld = iteratorOld.next();
            String key = entryOld.getKey();
            Object valueOld = entryOld.getValue();
            // 新的key没有，说明已经被删除
            if (!jsonNew.containsKey(key)) {
                iteratorOld.remove();
            } else {
                Object valueNew = jsonNew.get(key);
                if ((valueOld instanceof JSONObject) && (valueNew instanceof JSONObject)) {
                    JSONObject jsonNewChild = (JSONObject) valueNew;
                    JSONObject jsonOldChild = (JSONObject) valueOld;
                    removeKey(jsonNewChild, jsonOldChild);
                }
            }
        }
    }

    public static String buildDataId(String path, String dataKvContent, String dataJson) {
        return DigestUtils.md5DigestAsHex(path.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildDataId(String path) {
        return buildDataId(path, "", "");
    }

}
