package cn.torna.manager.doc;

import java.util.List;

/**
 * @author tanghc
 */
public interface IParam {
    String getName();
    String getType();
    boolean getRequired();
    String getMaxLength();
    String getExample();
    String getDescription();

    <T extends IParam> List<T> getChildren();
}
