package cn.torna.service.event;

import cn.torna.common.enums.ModifySourceEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
@Getter
public class DocUpdateEvent extends ApplicationEvent {

    private String oldMd5;

    private ModifySourceEnum sourceFromEnum;

    public DocUpdateEvent(Long docId, String oldMd5, ModifySourceEnum sourceFromEnum) {
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
