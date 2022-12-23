package cn.torna.service.listener;

import cn.torna.service.event.DocUpdateEvent;
import org.springframework.context.ApplicationListener;

/**
 * 文档被修改触发监听
 * @author thc
 */
public abstract class DefaultDocUpdateListener implements ApplicationListener<DocUpdateEvent> {

}
