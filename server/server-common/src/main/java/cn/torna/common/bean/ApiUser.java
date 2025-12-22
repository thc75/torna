package cn.torna.common.bean;

import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.UserStatusEnum;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ApiUser implements User {

    public static final long ID = 99999L;

    public static final ApiUser DEFAULT_USER = new ApiUser();

    private Long id = ID;

    private String nickname = "OpenAPI";

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getUserId() {
        return id;
    }

    @Override
    public byte getOperationModel() {
        return OperationMode.OPEN.getType();
    }

    @Override
    public boolean isSuperAdmin() {
        return false;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Byte getStatus() {
        return UserStatusEnum.ENABLE.getStatus();
    }

    @Override
    public String getToken() {
        return "";
    }

}
