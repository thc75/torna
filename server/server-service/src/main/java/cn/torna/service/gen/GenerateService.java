package cn.torna.service.gen;

import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.GenTemplate;
import cn.torna.dao.mapper.GenTemplateMapper;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocParamService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.gen.dto.DocVar;
import cn.torna.service.gen.dto.GenParamDTO;
import cn.torna.service.gen.dto.GenResultDTO;
import cn.torna.service.gen.dto.SysVar;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.fastmybatis.core.ext.code.util.VelocityUtil;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author 六如
 */
@Service
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

        String result = VelocityUtil.generate(context, templateContent);

        GenResultDTO genResultDTO = new GenResultDTO();
        genResultDTO.setDocName(docVar.getName());
        genResultDTO.setContent(result);

        return genResultDTO;
    }

    private DocVar buildDocVar(Long docId) {
        DocInfoDTO docDetail = docInfoService.getDocDetail(docId);
        DocVar docVar = CopyUtil.copyBean(docDetail, DocVar::new);
        JSONObject requestExample = DocParamService.createExample(docDetail.getRequestParams());
        docVar.setRequestExample(JSON.toJSONString(requestExample));
        JSONObject responseExample = DocParamService.createExample(docDetail.getResponseParams());
        docVar.setResponseExample(JSON.toJSONString(responseExample));
        return docVar;
    }


}
