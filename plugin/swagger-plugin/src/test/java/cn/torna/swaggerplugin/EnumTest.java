package cn.torna.swaggerplugin;

import io.swagger.annotations.ApiModelProperty;
import org.junit.Test;

public class EnumTest {

    @Test
    public void test() {
        Class<GenderEnum> genderEnumClass = GenderEnum.class;
        Enum[] enumConstants = genderEnumClass.getEnumConstants();
        for (Enum enumConstant : enumConstants) {
            System.out.println(enumConstant.name() + "=" + enumConstant.ordinal());
        }
    }

    public enum GenderEnum {
        @ApiModelProperty(value = "男")
        MALE(1),
        FEMALE(0)
        ;

        private final int gender;

        GenderEnum(int gender) {
            this.gender = gender;
        }
    }
}
