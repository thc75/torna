package cn.torna.service.event;

import cn.torna.common.enums.ModifySourceEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
@Getter
public class DocAddEvent extends ApplicationEvent {

    private ModifySourceEnum sourceFromEnum;

    public DocAddEvent(Long source, ModifySourceEnum sourceFromEnum) {
        super(source);
        this.sourceFromEnum = sourceFromEnum;
    }

    private DocAddEvent(Long docId) {
        super(docId);
    }

    public Long getDocId() {
        return (Long) getSource();
    }

}
