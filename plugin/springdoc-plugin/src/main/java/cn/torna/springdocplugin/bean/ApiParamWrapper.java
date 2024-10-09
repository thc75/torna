package cn.torna.springdocplugin.bean;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.Optional;

/**
 * @author tanghc
 */
public class ApiParamWrapper {
    private final Optional<Parameter> apiParamOptional;

    public ApiParamWrapper(Parameter apiParam) {
        this.apiParamOptional = Optional.ofNullable(apiParam);
    }

    public String getName() {
        return apiParamOptional.map(Parameter::name).orElse("");
    }

    public String getType() {
        return apiParamOptional.map(parameter -> parameter.schema().type()).orElse("");
    }

    public String getExample() {
        return apiParamOptional.map(Parameter::example).orElse("");
    }

    public String getDescription() {
        return apiParamOptional.map(Parameter::description).orElse("");
    }

    public boolean getRequired() {
        return apiParamOptional.map(Parameter::required).orElse(false);
    }
}
