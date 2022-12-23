package cn.torna.service.event;

import cn.torna.dao.entity.DocInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
public class DocUpdateEvent extends ApplicationEvent {

    private String oldMd5;

    public DocUpdateEvent(DocInfo source, String oldMd5) {
        this(source);
        this.oldMd5 = oldMd5;
    }

    private DocUpdateEvent(DocInfo source) {
        super(source);
    }

    public DocInfo getDocInfo() {
        return (DocInfo) getSource();
    }

    public String getOldMd5() {
        return oldMd5;
    }
}
