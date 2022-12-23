package cn.torna;

import cn.torna.api.bean.ApiUser;
import cn.torna.common.bean.Booleans;
import cn.torna.common.enums.SourceFromEnum;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocSnapshot;
import cn.torna.service.DocDiffDetailService;
import cn.torna.service.DocDiffRecordService;
import cn.torna.service.DocInfoService;
import cn.torna.service.DocSnapshotService;
import cn.torna.service.dto.DocInfoDTO;
import cn.torna.service.dto.DocParamDTO;
import com.alibaba.fastjson.JSON;
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

    @Autowired
    DocSnapshotService docSnapshotService;

    static ApiUser user = new ApiUser();
    static {
        user.setNickname("jim");
    }


    @Test
    public void doCompare() {
        DocSnapshot docSnapshot = docSnapshotService.getByMd5("6ed6c538296f61273d9fdd28f75c3dc9");
        DocInfoDTO docInfoDTOOld = JSON.parseObject(docSnapshot.getContent(), DocInfoDTO.class);
        docInfoDTOOld.setMd5("6ed6c538296f61273d9fdd28f75c3dc9");
        DocInfoDTO docInfoDTONew = CopyUtil.deepCopy(docInfoDTOOld, DocInfoDTO.class);

        docInfoDTONew.setName("获取分类信息2");
//        docInfoDTONew.setHttpMethod("PUT");
        docInfoDTONew.setContentType("form");
        docInfoDTONew.setDeprecated("已废弃2");
        List<DocParamDTO> headerParams = docInfoDTONew.getHeaderParams();
        // update, 修改第一个请求头的类型
        if (!CollectionUtils.isEmpty(headerParams)) {
            DocParamDTO docParamDTO = headerParams.get(0);
            docParamDTO.setType("string");
            docParamDTO.setRequired(Booleans.TRUE);
            docParamDTO.setExample("xxxxxx");
        }

        // add param
        DocParamDTO docParamDTO = new DocParamDTO();
        docParamDTO.setName("myHead1");
        docParamDTO.setType("string");
        docParamDTO.setDescription("新增header1");
        docParamDTO.setExample("222");
        headerParams.add(docParamDTO);

        String docMd5 = DocInfoService.getDocMd5(docInfoDTONew);
        docInfoDTONew.setMd5(docMd5);

        docDiffRecordService.doDocDiffNow(docInfoDTOOld.getMd5(), docInfoDTONew, SourceFromEnum.FORM, user
            , docDiffRecordService::processDocDiff
        );
    }

    /**
     * 演示
     */
    @Test
    public void doCompareRemove() {
        DocInfoDTO docInfoDTOOld = docInfoService.getDocDetail(2);
        DocInfoDTO docInfoDTONew = CopyUtil.deepCopy(docInfoDTOOld, DocInfoDTO.class);

        List<DocParamDTO> headerParams = docInfoDTONew.getHeaderParams();
        // remove
        if (!CollectionUtils.isEmpty(headerParams)) {
            headerParams.clear();
        }

        docDiffRecordService.doDocDiffNow(docInfoDTOOld.getMd5(), docInfoDTONew, SourceFromEnum.PUSH, user
                , docDiffRecordService::processDocDiff);
    }
}
