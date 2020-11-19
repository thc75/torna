package torna.common.enums;

/**
 * 角色，visitor：访客，dev：开发者，leader：项目组长
 * @author tanghc
 */
public enum RoleEnum {

    VISITOR, DEV, LEADER;

    public static RoleEnum of(String code) {
        RoleEnum[] values = RoleEnum.values();
        for (RoleEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return this.name().toLowerCase();
    }
}
