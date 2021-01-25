package cn.torna.manager.doc.swagger;

import java.util.List;

/**
 * @author tanghc
 */
public class DocModule {
    private String module;
    private List<DocItem> items;
    private int order;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<DocItem> getItems() {
        return items;
    }

    public void setItems(List<DocItem> items) {
        this.items = items;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
