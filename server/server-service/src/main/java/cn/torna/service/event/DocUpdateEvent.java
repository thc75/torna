package cn.torna.service.event;

import cn.torna.dao.entity.DocInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
public class DocUpdateEvent extends ApplicationEvent {

    private String oldMd5;

    public DocUpdateEvent(Long docId, String oldMd5) {
        this(docId);
        this.oldMd5 = oldMd5;
    }

    private DocUpdateEvent(Long docId) {
        super(docId);
    }

    public DocInfo getDocInfo() {
        return (DocInfo) getSource();
    }

    public String getOldMd5() {
        return oldMd5;
    }
}
