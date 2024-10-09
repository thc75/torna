package cn.torna.service.metersphere.v3.openapi;

import com.google.common.collect.Sets;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.Set;

public class OpenApiGenerator {

    public JsonObject generate(OpenAPI openApi) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new NamedExclusionStrategy(Sets.newHashSet("exampleSetFlag", "specVersion")))
                .setPrettyPrinting()
                .create();
        JsonElement jsonElement = gson.toJsonTree(openApi);
        return jsonElement.getAsJsonObject();
    }

    /**
     * 按照字段名称过滤的策略
     */
    private static class NamedExclusionStrategy implements ExclusionStrategy {

        private final Set<String> skipFields;

        public NamedExclusionStrategy(Set<String> skipFields) {
            this.skipFields = skipFields;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return skipFields.contains(fieldAttributes.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }
}
