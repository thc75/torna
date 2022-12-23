package cn.torna.service.listener;

import cn.torna.service.event.DocAddEvent;
import org.springframework.context.ApplicationListener;

/**
 * 新增文档触发监听
 * @author thc
 */
public abstract class DefaultDocAddListener implements ApplicationListener<DocAddEvent> {

}
