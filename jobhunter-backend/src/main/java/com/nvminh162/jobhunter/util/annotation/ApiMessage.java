package com.nvminh162.jobhunter.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// HĐ trong quá trình chạy dự án
@Retention(RetentionPolicy.RUNTIME)
// Dùng cho Method
@Target(ElementType.METHOD)
public @interface ApiMessage {
    String value();
}
