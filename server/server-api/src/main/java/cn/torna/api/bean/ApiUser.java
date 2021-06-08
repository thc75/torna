package cn.torna.api.bean;

import cn.torna.common.bean.User;
import cn.torna.common.enums.OperationMode;
import cn.torna.common.enums.UserStatusEnum;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ApiUser implements User {

    private Long id;

    private String nickname = "OpenAPI";

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
