package cn.torna.service;

import cn.torna.common.bean.EnvironmentKeys;
import cn.torna.service.dto.DocDiffDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author thc
 */
@Component
@EnableScheduling
public class ScheduleTask {

    @Autowired
    private DocDiffRecordService docDiffRecordService;


    @Scheduled(initialDelay = 1000, fixedDelay = 15 * 1000)
    public void saveDocDiff() {
        String value = EnvironmentKeys.TORNA_SCHEDULE_SAVE_DOC_DIFF_POLL_SIZE.getValue();
        int size = NumberUtils.toInt(value);
        List<DocDiffDTO> docDiffDTOS = DocDiffContext.poll(size);
        for (DocDiffDTO docDiffDTO : docDiffDTOS) {
            docDiffRecordService.processDocDiff(docDiffDTO);
        }
    }


}
