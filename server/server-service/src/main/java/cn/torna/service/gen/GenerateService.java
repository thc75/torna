package cn.torna.service.gen;

import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.GenTemplate;
import cn.torna.dao.mapper.GenTemplateMapper;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocParamService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import cn.torna.service.gen.dto.DocVar;
import cn.torna.service.gen.dto.GenParamDTO;
import cn.torna.service.gen.dto.GenResultDTO;
import cn.torna.service.gen.dto.SysVar;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.ext.code.util.VelocityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author 六如
 */
@Service
@Slf4j
public class GenerateService {


    @Resource
    private DocInfoService docInfoService;

    @Resource
    private GenTemplateMapper genTemplateMapper;


    /**
     * 生成代码
     *
     * @param genParamDTO 参数
     * @return 返回内容
     */
    public GenResultDTO generate(GenParamDTO genParamDTO) {
        String templateContent = genTemplateMapper.query()
                .eq(GenTemplate::getId, genParamDTO.getTemplateId())
                .getValue(GenTemplate::getContent);

        VelocityContext context = new VelocityContext();
        DocVar docVar = buildDocVar(genParamDTO.getDocId());
        context.put("doc", docVar);
        context.put("sys", new SysVar());

        String result = null;
        try {
            result = VelocityUtil.generate(context, templateContent);
        } catch (Exception e) {
            log.error("生成代码错误，genParamDTO={}", genParamDTO, e);
            result = "生成错误：" + e.getMessage();
        }
        GenResultDTO genResultDTO = new GenResultDTO();
        genResultDTO.setDocName(docVar.getName());
        genResultDTO.setContent(result);

        return genResultDTO;
    }

    private DocVar buildDocVar(Long docId) {
        DocInfoDTO docDetail = docInfoService.getDocDetail(docId);
        DocVar docVar = CopyUtil.copyBean(docDetail, DocVar::new);

        docVar.setRequestParams(TreeUtil.convertTree(docDetail.getRequestParams(), 0L));
        docVar.setResponseParams(TreeUtil.convertTree(docDetail.getResponseParams(), 0L));

        JSONObject requestExample = DocParamService.createExample(docVar.getRequestParams());
        docVar.setRequestExample(JSON.toJSONString(requestExample));
        JSONObject responseExample = DocParamService.createExample(docVar.getResponseParams());
        docVar.setResponseExample(JSON.toJSONString(responseExample));
        String queryString = buildQueryString(docDetail);
        docVar.setQueryString(queryString);
        return docVar;
    }

    private String buildQueryString(DocInfoDTO docDetail) {
        List<DocParamDTO> queryParams = docDetail.getQueryParams();
        if (CollectionUtils.isEmpty(queryParams)) {
            return "";
        }
        return queryParams.stream()
                .map(param -> param.getName() + "=" + param.getExample())
                .collect(Collectors.joining("&"));
    }


}
