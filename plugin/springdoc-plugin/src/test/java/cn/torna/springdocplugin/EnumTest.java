package cn.torna.springdocplugin;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(name = "男")
        MALE(1),
        FEMALE(0)
        ;

        private final int gender;

        GenderEnum(int gender) {
            this.gender = gender;
        }
    }
}
