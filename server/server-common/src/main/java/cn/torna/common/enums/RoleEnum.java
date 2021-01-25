package cn.torna.common.enums;

/**
 * 角色，guest：访客，dev：开发者，admin：管理员
 * @author tanghc
 */
public enum RoleEnum {

    GUEST, DEV, ADMIN;

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
