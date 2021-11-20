/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.torna.sdk.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanghc
 */
public class ClassUtil {

    private static final Map<String, Class<?>> CLASS_GENERIC_TYPE_CACHE = new HashMap<>(16);

    /**
     * 返回定义类时的泛型参数的类型. <br>
     * 如:定义一个BookManager类<br>
     * <code>{@literal public BookManager extends GenricManager<Book,Address>}{...} </code>
     * <br>
     * 调用getSuperClassGenricType(getClass(),0)将返回Book的Class类型<br>
     * 调用getSuperClassGenricType(getClass(),1)将返回Address的Class类型
     *
     * @param clazz 从哪个类中获取
     * @param index 泛型参数索引,从0开始
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) throws IndexOutOfBoundsException {
        String cacheKey = clazz.getName() + index;
        Class<?> cachedClass = CLASS_GENERIC_TYPE_CACHE.get(cacheKey);
        if (cachedClass != null) {
            return cachedClass;
        }

        Type genType = clazz.getGenericSuperclass();

        // 没有泛型参数
        if (!(genType instanceof ParameterizedType)) {
            throw new RuntimeException("class " + clazz.getName() + " 没有指定父类泛型");
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

            if (index >= params.length || index < 0) {
                throw new RuntimeException("泛型索引不正确，index:" + index);
            }
            if (!(params[index] instanceof Class)) {
                throw new RuntimeException(params[index] + "不是Class类型");
            }

            Class<?> retClass = (Class<?>) params[index];
            // 缓存起来
            CLASS_GENERIC_TYPE_CACHE.put(cacheKey, retClass);

            return retClass;
        }
    }

}
