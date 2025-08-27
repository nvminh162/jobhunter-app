package com.nvminh162.jobhunter.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.nvminh162.jobhunter.dto.ResResponseDTO;

/* 
 * Giải thích cơ chế Exception Y - #68 (3p)
 */
@RestControllerAdvice // (@ControllerAdvice + @ResponseBody)
public class GlobalException {
    // Exception define
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class
    })
    public ResponseEntity<ResResponseDTO<Object>> handleIdException(Exception ex) {
        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception occurs...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // 404
    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public ResponseEntity<ResResponseDTO<Object>> handleNotFoundException(Exception ex) {
        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("404 Not found. URL may not exist...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // Exception for validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResResponseDTO<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            StorageException.class
    })
    public ResponseEntity<ResResponseDTO<Object>> handleFileUploadException(Exception ex) {
        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(ex.getMessage());
        res.setError("Exception upload file...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            PermissionException.class
    })
    public ResponseEntity<ResResponseDTO<Object>> PermissionException(Exception ex) {
        ResResponseDTO<Object> res = new ResResponseDTO<Object>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setMessage(ex.getMessage());
        res.setError("Forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }
}
