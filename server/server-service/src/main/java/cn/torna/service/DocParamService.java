package cn.torna.service;

import cn.torna.common.bean.Booleans;
import cn.torna.common.bean.User;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.ParamStyleEnum;
import cn.torna.common.util.DataIdUtil;
import cn.torna.common.util.IdGen;
import cn.torna.dao.entity.DocInfo;
import cn.torna.dao.entity.DocParam;
import cn.torna.dao.entity.EnumInfo;
import cn.torna.dao.mapper.DocParamMapper;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.dto.EnumInfoDTO;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.support.BaseLambdaService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tanghc
 */
@Service
public class DocParamService extends BaseLambdaService<DocParam, DocParamMapper> {

    private static final List<String> COLLECT_TYPE_LIST = Arrays.asList(
            "array", "list", "set", "collection"
    );
    private static final List<String> BOOLEAN_TYPES = Arrays.asList(
            "boolean", "bool"
    );
    private static final List<String> NUMBER_TYPES = Arrays.asList(
            "byte", "short", "int", "long", "integer",
            "int8", "int16", "int32", "int64", "float", "double", "number"
    );

    @Autowired
    private EnumService enumService;

    public DocParam getByDataId(String dataId) {
        return get(DocParam::getDataId, dataId);
    }

    public void saveParams(DocInfo docInfo, List<DocParamDTO> docParamDTOS, ParamStyleEnum paramStyleEnum, User user) {
        // 如果参数是空的，则移除这个类型的所有参数
        if (CollectionUtils.isEmpty(docParamDTOS)) {
            return;
        }
        for (DocParamDTO docParamDTO : docParamDTOS) {
            this.doSave(docParamDTO, 0L, docInfo, paramStyleEnum, user);
        }
    }

    private List<DocParam> listParentParam(long docId, ParamStyleEnum paramStyleEnum) {
        Query query = this.query()
                .eq(DocParam::getDocId, docId)
                .eq(DocParam::getStyle, paramStyleEnum.getStyle())
                .eq(DocParam::getParentId, 0);
        return this.list(query);
    }

    /**
     * 删除参数，同时会删除子节点
     *
     * @param id id
     */
    public void deleteParamDeeply(long id) {
        this.getMapper().deleteById(id);
        this.deleteChildrenDeeply(id);
    }

    /**
     * 递归删除下面所有子节点
     *
     * @param parentId 父id
     */
    private void deleteChildrenDeeply(long parentId) {
        List<DocParam> children = this.list(DocParam::getParentId, parentId);
        for (DocParam child : children) {
            this.deleteParamDeeply(child.getId());
        }
    }

    private void doSave(DocParamDTO docParamDTO, long parentId, DocInfo docInfo, ParamStyleEnum paramStyleEnum, User user) {
        DocParam docParam = new DocParam();
        Long docId = docInfo.getId();
        String dataId = DataIdUtil.getDocParamDataId(docId, parentId, paramStyleEnum.getStyle(), docParamDTO.getName());
        // 如果删除
        if (Booleans.isTrue(docParamDTO.getIsDeleted())) {
            dataId = IdGen.nextId();
        }
        docParamDTO.setParentId(parentId);
        docParam.setId(docParamDTO.getId());
        docParam.setDataId(dataId);
        docParam.setName(docParamDTO.getName());
        docParam.setType(docParamDTO.getType());
        docParam.setRequired(docParamDTO.getRequired());
        docParam.setMaxLength(buildMaxLength(docParamDTO));
        docParam.setExample(docParamDTO.getExample());
        docParam.setDescription(docParamDTO.getDescription());
        docParam.setEnumId(buildEnumId(docInfo.getModuleId(), docParamDTO));
        docParam.setDocId(docId);
        docParam.setParentId(parentId);
        docParam.setStyle(paramStyleEnum.getStyle());
        docParam.setCreatorId(user.getUserId());
        docParam.setCreateMode(user.getOperationModel());
        docParam.setCreatorName(user.getNickname());
        docParam.setModifierId(user.getUserId());
        docParam.setModifyMode(user.getOperationModel());
        docParam.setModifierName(user.getNickname());
        docParam.setOrderIndex(docParamDTO.getOrderIndex());
        docParam.setIsDeleted(docParamDTO.getIsDeleted());
        if (docParam.getDescription() == null) {
            docParam.setDescription("");
        }
        DocParam savedParam;
        if (docParam.getId() == null) {
            savedParam = this.saveParam(docParam);
        } else {
            this.update(docParam);
            savedParam = docParam;
        }
        // 回填ID
        docParamDTO.setId(savedParam.getId());
        List<DocParamDTO> children = docParamDTO.getChildren();
        if (children != null) {
            Long pid = savedParam.getId();
            // 修复NPE问题
            if (pid == null) {
                DocParam exist = getByDataId(savedParam.getDataId());
                if (exist != null) {
                    pid = exist.getId();
                }
            }
            for (DocParamDTO child : children) {
                // 如果父节点被删除，子节点也要删除
                if (docParam.getIsDeleted() == Booleans.TRUE) {
                    child.setIsDeleted(docParam.getIsDeleted());
                }
                if (pid == null) {
                    continue;
                }
                this.doSave(child, pid, docInfo, paramStyleEnum, user);
            }
        }
    }

    private static String buildMaxLength(DocParamDTO docParamDTO) {
        String maxLength = docParamDTO.getMaxLength();
        if (StringUtils.isEmpty(maxLength) || "0".equals(maxLength)) {
            maxLength = "-";
        }
        return CollectionUtils.isEmpty(docParamDTO.getChildren()) ? maxLength : "";
    }

    private Long buildEnumId(long moduleId, DocParamDTO docParamDTO) {
        EnumInfoDTO enumInfoDTO = docParamDTO.getEnumInfo();
        if (enumInfoDTO != null) {
            if (ObjectUtils.isEmpty(enumInfoDTO.getDescription())) {
                enumInfoDTO.setDescription(docParamDTO.getDescription());
            }
            enumInfoDTO.setModuleId(moduleId);
            EnumInfo enumInfo = enumService.saveEnumInfo(enumInfoDTO);
            return enumInfo.getId();
        }
        Long enumId = docParamDTO.getEnumId();
        if (enumId == null) {
            enumId = 0L;
        }
        return enumId;
    }

    public DocParam saveParam(DocParam docParam) {
        if (docParam.getDescription() == null) {
            docParam.setDescription("");
        }
        if (docParam.getExample() == null) {
            docParam.setExample("");
        }
        this.getMapper().saveParam(docParam);
        return docParam;
    }

    public void deletePushParam(List<Long> docIdList) {
        // 删除文档对应的参数
        Query paramDelQuery = this.query()
                .in(DocParam::getDocId, docIdList)
                .eq(DocParam::getCreateMode, OperationMode.OPEN.getType());
        this.deleteByQuery(paramDelQuery);
    }


    public static JSONObject createExample(List<DocParamDTO> docParams) {
        return doCreateExample(docParams);
    }

    private static JSONObject doCreateExample(List<DocParamDTO> params) {
        JSONObject responseJson = new JSONObject();
        for (DocParamDTO row : params) {
            if (Objects.equals(row.getIsDeleted(), Booleans.TRUE)) {
                continue;
            }
            Object val;
            List<DocParamDTO> children = row.getChildren();
            if (!ObjectUtils.isEmpty(children)) {
                JSONObject childrenValue = doCreateExample(children);
                if (isArrayType(row.getType())) {
                    val = isNestArrayType(row.getType()) ? buildNestList(childrenValue)
                            : Collections.singletonList(childrenValue);
                } else {
                    val = childrenValue;
                }
            } else {
                // 单值
                String example = row.getExample();
                String type = row.getType();
                Object exampleObj = "";
                if (StringUtils.isEmpty(example)) {
                    exampleObj = getDefaultExample(type);
                } else {
                    // 解析出数字，布尔，数组示例值
                    if (isNumberType(type)) {
                        exampleObj = NumberUtils.toInt(example, 0);
                    } else if (isBooleanType(type)) {
                        exampleObj = BooleanUtils.toBoolean(example);
                    } else if (isNumArray(type, example)) {
                        exampleObj = parseNumArray(example);
                    } else if (isStrArray(type, example)) {
                        exampleObj = parseStrArray(example);
                    } else if (isNestArrayType(type)) {
                        exampleObj = getNestArrayValue(type, example);
                    }
                }
                val = exampleObj;
            }
            responseJson.put(row.getName(), val);
        }
        return responseJson;
    }

    private static Object getNestArrayValue(String type, String example) {
        String type_ = type.toLowerCase();
        if (ObjectUtils.isEmpty(example)) {
            return type_.indexOf("string") > -1 ? buildNestList("string") : buildNestList(1);
        }
        if (type_.indexOf("int") > -1 ||
                type_.indexOf("long") > -1 ||
                type_.indexOf("decimal") > -1 ||
                type_.indexOf("float") > -1 ||
                type_.indexOf("double") > -1 ||
                type_.indexOf("byte") > -1 ||
                type_.indexOf("short") > -1
        ) {
            return buildNestList(NumberUtils.toInt(example));
        }
        if (type_.indexOf("bool") > -1) {
            return buildNestList(true, false);
        }
        return buildNestList(example);
    }

    private static boolean isNumArray(String type, String example) {
        if (isArrayString(example) || (Objects.equals(type, "array"))) {
            example = example.substring(1, example.length() - 1);
            String[] arr = example.split(",");
            for (String num : arr) {
                if (!NumberUtils.isDigits(num)) {
                    return false;
                }
            }
            return true;
        }
        return Objects.equals(type, "num_array");
    }

    private static boolean isStrArray(String type, String example) {
        if (isArrayString(example) || (Objects.equals(type, "array"))) {
            example = example.substring(1, example.length() - 1);
            String[] arr = example.split(",");
            for (String num : arr) {
                if (!NumberUtils.isDigits(num)) {
                    return true;
                }
            }
            return true;
        }
        return Objects.equals(type, "num_array");
    }


    private static List<?> parseStrArray(String val) {
        if (ObjectUtils.isEmpty(val) || Objects.equals(val, "[]")) {
            return Collections.emptyList();
        }
        String str = val;
        if (isArrayString(str)) {
            str = str.substring(1, str.length() - 1);
        }
        String[] arr = str.split(",");
        return Stream.of(arr)
                .map(item -> {
                    String el = item.trim();
                    if (el.startsWith("\"") || el.startsWith("\'")) {
                        el = el.substring(1);
                    }
                    if (el.endsWith("\"") || el.endsWith("\'")) {
                        el = el.substring(0, el.length() - 1);
                    }
                    return el;
                })
                .collect(Collectors.toList());

    }

    private static List<?> parseNumArray(String val) {
        if (ObjectUtils.isEmpty(val) || Objects.equals(val, "[]")) {
            return Collections.emptyList();
        }
        String str = val;
        if (isArrayString(str)) {
            str = str.substring(1, str.length() - 1);
        }
        String[] arr = str.split("\\D+");
        List<Integer> list = Stream.of(arr).map(v -> NumberUtils.toInt(v, 0)).collect(Collectors.toList());
        return list;
    }

    private static boolean isArrayString(String example) {
        if (example == null) {
            return false;
        }
        return example.startsWith("[") && example.endsWith("]");
    }


    public static boolean isArrayType(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return false;
        }
        type = type.toLowerCase();
        for (String t : COLLECT_TYPE_LIST) {
            if (type.contains(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否嵌套list
     */
    private static boolean isNestArrayType(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return false;
        }
        return type.startsWith("List<List<") ||
                type.indexOf("[][]") > -1 ||
                type.startsWith("Collection<Collection<") ||
                type.startsWith("List<Collection<") ||
                type.startsWith("Collection<List<") ||
                type.startsWith("List<Set<") ||
                type.startsWith("Set<Set<");
    }

    private static boolean isNumberType(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return false;
        }
        String typeLower = type.toLowerCase();
        for (String numberType : NUMBER_TYPES) {
            if (Objects.equals(numberType, typeLower)) {
                return true;
            }
        }
        return false;
    }

    private static List<List<?>> buildNestList(Object... val) {
        return Collections.singletonList(Arrays.asList(val));
    }

    private static boolean isBooleanType(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return false;
        }
        String typeLower = type.toLowerCase();
        for (String booleanType : BOOLEAN_TYPES) {
            if (Objects.equals(booleanType, typeLower)) {
                return true;
            }
        }
        return false;
    }


    private static Object getDefaultExample(String type) {
        if (ObjectUtils.isEmpty(type)) {
            return "";
        }
        String typeLower = type.toLowerCase();
        if (isNumberType(type)) {
            return "0";
        }
        if (isBooleanType(type)) {
            return "false";
        }
        if (isNestArrayType(type)) {
            Object val = type.toLowerCase().indexOf("string") > -1 ? "string value" : 1;
            return buildNestList(val);
        }
        Object example;
        switch (typeLower) {
            case "string":
                example = "string";
                break;
            case "map":
            case "hashmap":
            case "dict":
            case "dictionary":
            case "json":
            case "obj":
            case "object":
                example = new HashMap<>();
                break;
            case "collection":
            case "list":
            case "set":
            case "arr":
            case "array":
                example = new ArrayList<>();
                break;
            case "array[string]":
                example = Collections.singletonList("string");
                break;
            case "array[byte]":
            case "array[short]":
            case "array[integer]":
            case "array[long]":
            case "array[decimal]":
                example = Collections.singletonList(0);
                break;
            case "array[float]":
            case "array[double]":
                example = Collections.singletonList(1.2);
                break;
            case "array[boolean]":
                example = Collections.singletonList(false);
                break;
            case "array[object]":
                example = Collections.singletonList(new HashMap<>());
                break;
            default: {
                example = new HashMap<>();
            }
        }
        return example instanceof String ? example : JSONObject.toJSONString(example);
    }

}
