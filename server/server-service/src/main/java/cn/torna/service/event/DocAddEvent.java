package cn.torna.service.event;

import cn.torna.common.enums.SourceFromEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
@Getter
public class DocAddEvent extends ApplicationEvent {

    private SourceFromEnum sourceFromEnum;

    public DocAddEvent(Long source, SourceFromEnum sourceFromEnum) {
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
