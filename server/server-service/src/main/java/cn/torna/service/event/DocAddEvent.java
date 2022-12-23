package cn.torna.service.event;

import cn.torna.dao.entity.DocInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author thc
 */
public class DocAddEvent extends ApplicationEvent {

    public DocAddEvent(DocInfo source) {
        super(source);
    }

    public DocInfo getDocInfo() {
        return (DocInfo) getSource();
    }
}
