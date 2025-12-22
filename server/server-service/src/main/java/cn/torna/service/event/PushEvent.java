package cn.torna.service.event;

import cn.torna.dao.entity.Module;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;

/**
 * @author 六如
 */
@Getter
public class PushEvent extends ApplicationEvent {

    private final Module module;
    private final Collection<Long> docIds;

    public PushEvent(Module module, Collection<Long> docIds) {
        super(module.getId());
        this.module = module;
        this.docIds = docIds;
    }

}
