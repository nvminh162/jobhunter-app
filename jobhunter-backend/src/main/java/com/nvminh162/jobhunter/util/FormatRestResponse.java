package com.nvminh162.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nvminh162.jobhunter.dto.ResResponseDTO;
import com.nvminh162.jobhunter.util.annotation.ApiMessage;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    // Không recommend sử dụng can thiếp vào request sẽ ảnh hưởng thư viện thứ 3 (VD: Swagger)
    // Phải if else để xử lý riêng lẻ => không tối ưu
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

        // can thiệp vào lời gọi request thì hệ thống chỉ APPLICATION_JSON
        /* https://stackoverflow.com/questions/44121648/controlleradvice-responsebodyadvice-failed-to-enclose-a-string-response
         * Dễ bug vì spring sẽ chạy hết Converter để set accept header
         */
        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(status);

        /*
         * *** Fix Bug: ***
         * RestResponse can't be cast to String
         * (Nếu là String || Resource thì không format)
         */
        /*
         * Đầu vào là String, java spring sử dụng 1 đầu hàm: HttpMessageConverter
         * => StringHttpMessageConverter
         * Khi nén ResResponseDTO<Object> => BUG
         */
        /*
         * Mỗi lời gọi request có 1 accept header (vd: text/html, application/xhtml+xml)
         */
        /*
         * Fix:
         * Nếu request gửi lên là không phải APPLICATION_JSON (không phải object) thì cho nó bth.
         * => không muốn thao tác với Object
         * *** Khi yêu cầu trả ra không phải là 1 đối tượng thì xử lý bth (không can thiệp)
         */
        /* if (!MediaType.APPLICATION_JSON.equals(selectedContentType)) {
            return body;
        } */

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        // FormatRestResponse.java
        // Nếu phục vụ cho swagger thì không làm gì cả
        /*
         * Bug: do sử dụng restResponse nên return bị bọc data: { }
         * đối với swagger thì nó không cần
         */
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
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
