package cn.torna.api.bean;

import cn.torna.common.bean.User;
import cn.torna.common.enums.OperationMode;
import lombok.Data;

/**
 * @author tanghc
 */
@Data
public class ApiUser implements User {

    private Long id;

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
        return "OpenAPI";
    }

    @Override
    public String getToken() {
        return "";
    }
}
