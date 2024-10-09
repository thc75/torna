package cn.torna.service.gen.dto;

import cn.torna.service.dto.DocParamDTO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author 六如
 */
@Data
public class DocVar {
    /**
     * 数据库字段：id
     */
    private Long id;

    /**
     * 唯一id，接口规则：md5(module_id:parent_id:url:http_method)。分类规则：md5(module_id:parent_id:name), 数据库字段：doc_key
     */
    private String docKey;

    /**
     * 文档名称, 数据库字段：name
     */
    private String name;

    /**
     * 文档描述, 数据库字段：description
     */
    private String description;

    /**
     * 维护人, 数据库字段：author
     */
    private String author;

    /**
     * 访问URL, 数据库字段：url
     */
    private String url;

    /**
     * 版本号
     */
    private String version;

    /**
     * http方法, 数据库字段：http_method
     */
    private String httpMethod;

    /**
     * contentType, 数据库字段：content_type
     */
    private String contentType;

    private String queryString;

    private List<DocParamDTO> pathParams = Collections.emptyList();

    private List<DocParamDTO> headerParams = Collections.emptyList();

    private List<DocParamDTO> queryParams = Collections.emptyList();

    private List<DocParamDTO> requestParams = Collections.emptyList();

    private List<DocParamDTO> responseParams = Collections.emptyList();

    /**
     * 请求示例
     */
    private String requestExample;

    /**
     * 响应示例
     */
    private String responseExample;

}
