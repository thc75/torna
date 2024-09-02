package cn.torna.service.metersphere.v3.model.state;

import lombok.Data;

@Data
public class CoverModule {
    private String name;
    private String id;

    public CoverModule(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CoverModule) {
            return obj.hashCode() == this.hashCode();
        }
        return false;
    }
}
