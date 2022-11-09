package cn.torna;

import cn.torna.api.bean.ApiUser;
import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.DocDiffModifySourceEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.service.DocDiffDetailService;
import cn.torna.service.DocDiffRecordService;
import cn.torna.service.DocInfoService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author thc
 */
public class DocDiffRecordServiceTest extends TornaApplicationTests {

    @Autowired
    DocDiffDetailService docDiffDetailService;

    @Autowired
    DocDiffRecordService docDiffRecordService;

    @Autowired
    DocInfoService docInfoService;

    static ApiUser user = new ApiUser();
    static {
        user.setNickname("jim");
    }


    @Test
    public void doCompare() {
        DocInfoDTO docInfoDTOOld = docInfoService.getDocDetail(2);
        DocInfoDTO docInfoDTONew = CopyUtil.deepCopy(docInfoDTOOld, DocInfoDTO.class);

        docInfoDTONew.setName("获取分类信息2");
//        docInfoDTONew.setHttpMethod("PUT");
        docInfoDTONew.setContentType("www");
        docInfoDTONew.setDeprecated("已废弃");
        List<DocParamDTO> headerParams = docInfoDTONew.getHeaderParams();
        // update, 修改第一个请求头的类型
        if (!CollectionUtils.isEmpty(headerParams)) {
            DocParamDTO docParamDTO = headerParams.get(0);
            docParamDTO.setType("integer");
            docParamDTO.setRequired(Booleans.FALSE);
            docParamDTO.setExample("aaaakkk");
        }

        // add param
        DocParamDTO docParamDTO = new DocParamDTO();
        docParamDTO.setName("myHead");
        docParamDTO.setType("string");
        docParamDTO.setDescription("新增header");
        docParamDTO.setExample("111");
        headerParams.add(docParamDTO);

        docDiffRecordService.doDocDiffNow(docInfoDTOOld.getMd5(), docInfoDTONew, DocDiffModifySourceEnum.PUSH, user
            , docDiffRecordService::processDocDiff
        );
    }

    @Test
    public void doCompareRemove() {
        DocInfoDTO docInfoDTOOld = docInfoService.getDocDetail(2);
        DocInfoDTO docInfoDTONew = CopyUtil.deepCopy(docInfoDTOOld, DocInfoDTO.class);

        List<DocParamDTO> headerParams = docInfoDTONew.getHeaderParams();
        // remove
        if (!CollectionUtils.isEmpty(headerParams)) {
            headerParams.clear();
        }

        docDiffRecordService.doDocDiffNow(docInfoDTOOld.getMd5(), docInfoDTONew, DocDiffModifySourceEnum.PUSH, user
                , docDiffRecordService::processDocDiff);
    }
}
