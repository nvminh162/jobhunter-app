package com.nvminh162.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nvminh162.jobhunter.domain.dto.RestResponse;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse httpServletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpServletResponse.getStatus();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        /*
         * *** Fix Bug: ***
         * RestResponse can't be cast to class String
         * (Nếu là String || Resource thì không format)
         */
        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        // case: error
        if (status >= 400) {
            return body;
        } else {
            // case: success
            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : null);
        }

        return res;
    }

    /*
     * Vì sao lại return true?
     * => Vì tôi muốn override (format) lại phản hồi của API!
     * (?) Ngoại lệ từng controller thì hãy check if else để return false không
     * override (format) lại phản hồi của API!
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
}
