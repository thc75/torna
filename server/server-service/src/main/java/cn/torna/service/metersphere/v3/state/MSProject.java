package cn.torna.service.metersphere.v3.state;

import lombok.Data;

@Data
public class MSProject {
    private String name;
    private String id;
    private Boolean versionEnable;

    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MSProject) {
            if (obj.hashCode() == this.hashCode()) {
                return true;
            }
        }
        return false;
    }
}
