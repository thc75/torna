package cn.torna.service.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
public class DocAddEvent extends ApplicationEvent {

    public DocAddEvent(Long docId) {
        super(docId);
    }

    public Long getDocId() {
        return (Long) getSource();
    }
}
