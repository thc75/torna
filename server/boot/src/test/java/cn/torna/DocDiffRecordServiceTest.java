package cn.torna;

import cn.torna.common.util.CopyUtil;
import cn.torna.service.DocDiffRecordService;
import cn.torna.service.DocInfoService;
import cn.torna.service.dto.DocInfoDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author thc
 */
public class DocDiffRecordServiceTest extends TornaApplicationTests {

    @Autowired
    DocDiffRecordService docDiffRecordService;

    @Autowired
    DocInfoService docInfoService;

    @Test
    public void doCompare() {
        DocInfoDTO docInfoDTOOld = docInfoService.getDocDetail(2);
        DocInfoDTO docInfoDTONew = CopyUtil.deepCopy(docInfoDTOOld, DocInfoDTO.class);

        docInfoDTONew.setName("获取分类信息2");
//        docInfoDTONew.setHttpMethod("PUT");
        docInfoDTONew.setContentType("www");
        docInfoDTONew.setDeprecated("已废弃");

        docDiffRecordService.doCompare(docInfoDTOOld, docInfoDTONew);
    }
}
