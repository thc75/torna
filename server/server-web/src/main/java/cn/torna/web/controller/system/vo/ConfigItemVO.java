package cn.torna.web.controller.system.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author thc
 */
@Getter
@Setter
public class ConfigItemVO {
    private String key;
    private String value;
    private String remark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigItemVO that = (ConfigItemVO) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ConfigItemVO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
