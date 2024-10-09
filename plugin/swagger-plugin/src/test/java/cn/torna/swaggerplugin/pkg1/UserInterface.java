package cn.torna.swaggerplugin.pkg1;

import io.swagger.annotations.Api;

/**
 * @author 六如
 */
@Api
public interface UserInterface {

    String getUserName();
    String getAge(Long userId);

}
