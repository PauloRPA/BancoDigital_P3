package com.prpa.bancodigital.config.security;

import com.prpa.bancodigital.exception.ApiException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
        defaultErrorAttributes.put("type", ApiException.IANA_TYPE_PREFIX + "forbidden");
        renameAttribute(defaultErrorAttributes, "error", "title");
        defaultErrorAttributes.put("status", defaultErrorAttributes.remove("status"));
        renameAttribute(defaultErrorAttributes, "message", "detail");
        renameAttribute(defaultErrorAttributes, "path", "instance");
        defaultErrorAttributes.remove("timestamp");
        return defaultErrorAttributes;
    }

    private void renameAttribute(Map<String, Object> errorAttributes, String oldName, String newName) {
        errorAttributes.put(newName, errorAttributes.remove(oldName).toString());
    }
}
