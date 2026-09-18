package com.example.ilms.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Auth Module
    REGISTER_SUCCESS(HttpStatus.CREATED, "Đăng ký tài khoản thành công"),
    LOGIN_SUCCESS(HttpStatus.OK, "Đăng nhập thành công"),
    LOGOUT_SUCCESS(HttpStatus.OK, "Đăng xuất thành công"),
    REFRESH_TOKEN_SUCCESS(HttpStatus.OK, "Làm mới token thành công"),
    SEND_NEW_PASSWORD_SUCCESS(HttpStatus.OK, "Mật khẩu mới đã được gửi đến email của bạn"),
    SEND_EMAIL_RESET_PASSWORD_SUCCESS(HttpStatus.OK, "Đường link đặt lại mật khẩu đã được gửi"),
    VERIFY_TOKEN_RESET_PASSWORD_SUCCESS(HttpStatus.OK,"Token hợp lệ, bạn có thể đặt lại mật khẩu"),
    CHANGED_PASSWORD_SUCCESS(HttpStatus.OK,"Đổi mật khẩu thành công, vui lòng đăng nhập lại"),
    // User Module
    GET_USER_SUCCESS(HttpStatus.OK, "Lấy thông tin người dùng thành công"),
    UPDATE_PROFILE_SUCCESS(HttpStatus.OK, "Cập nhật hồ sơ thành công"),
    CREATE_USER_SUCCESS(HttpStatus.OK, "Tạo người dùng mới thành công"),
    // Course & Class Module
    CLASS_CREATED(HttpStatus.CREATED, "Tạo lớp học thành công"),
    ENROLL_CLASS_SUCCESS(HttpStatus.OK, "Tham gia lớp học thành công");

    private final HttpStatus status;
    private final String message;
}