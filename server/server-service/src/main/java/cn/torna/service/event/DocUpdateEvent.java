package cn.torna.service.event;

import cn.torna.common.enums.SourceFromEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
@Getter
public class DocUpdateEvent extends ApplicationEvent {

    private String oldMd5;

    private SourceFromEnum sourceFromEnum;

    public DocUpdateEvent(Long docId, String oldMd5, SourceFromEnum sourceFromEnum) {
        this(docId);
        this.oldMd5 = oldMd5;
        this.sourceFromEnum = sourceFromEnum;
    }

    private DocUpdateEvent(Long docId) {
        super(docId);
    }

    public Long getDocId() {
        return (Long) getSource();
    }

}
