package cn.torna.service.metersphere.v3.state;

import lombok.Data;

@Data
public class MSOrganization {
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
        if (obj instanceof MSOrganization) {
            return obj.hashCode() == this.hashCode();
        }
        return false;
    }

    public MSOrganization(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
