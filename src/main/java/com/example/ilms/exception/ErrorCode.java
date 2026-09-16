package com.example.ilms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    USER_EXISTED(HttpStatus.CONFLICT, "Tên tài khoản đã tồn tại"),
    EMAIL_EXISTED(HttpStatus.CONFLICT, "Email đã được đăng ký"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Vai trò người dùng không tồn tại"),
    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "Mật khẩu không chính xác"),
    PASSWORD_CANNOT_BE_OLD_PASSWORD(HttpStatus.UNAUTHORIZED, "Mật khẩu mới không được trùng với mật khẩu cũ"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Yêu cầu xác thực tài khoản"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác"),
    USER_DISABLED(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị khóa"),
    //    TOKEN
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Mã không hợp lệ hoặc đã hết hạn"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Mã đã hết hạn"),

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy môn học"),
    COURSE_CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Mã môn học đã tồn tại trong hệ thống"),
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"),
    CLASS_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Môn học không tồn tại trong lớp này"),
    CLASS_COURSE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Môn học đã được gán cho lớp này"),
    TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy giảng viên");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}