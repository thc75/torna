package cn.torna.service.metersphere.v3.state;

import lombok.Data;

@Data
public class MSModule {
    private String name;
    private String id;

    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MSModule) {
            return obj.hashCode() == this.hashCode();
        }
        return false;
    }
}
