package torna.api.bean;

import lombok.Data;
import torna.common.bean.User;
import torna.common.enums.OperationMode;

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
