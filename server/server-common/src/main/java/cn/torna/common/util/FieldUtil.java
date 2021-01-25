package cn.torna.common.util;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author tanghc
 */
public class FieldUtil {
	/**
	 * 字段是否被transient关键字修饰或有@Transient注解
	 * @param field 字段
	 * @return 是返回true
	 */
	public static boolean isTransientField(Field field) {
		Transient annotation = field.getAnnotation(Transient.class);
		return annotation != null || Modifier.isTransient(field.getModifiers());
	}

}
