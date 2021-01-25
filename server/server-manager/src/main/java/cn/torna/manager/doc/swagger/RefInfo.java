package cn.torna.manager.doc.swagger;

public class RefInfo {
    private boolean isArray;
    private String ref;
    private String rawRef;

    public boolean getIsArray() {
        return isArray;
    }

    public void setIsArray(boolean array) {
        isArray = array;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRawRef() {
        return rawRef;
    }

    public void setRawRef(String rawRef) {
        this.rawRef = rawRef;
    }

    @Override
    public String toString() {
        return "RefInfo{" +
                "isArray=" + isArray +
                ", ref='" + ref + '\'' +
                ", rawRef='" + rawRef + '\'' +
                '}';
    }
}