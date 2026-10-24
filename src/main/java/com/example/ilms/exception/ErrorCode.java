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
    // Class
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"),
    CLASS_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy thành viên trong lớp học"),
    CLASS_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "Mã lớp học không tồn tại"),
    CLASS_CODE_INACTIVE(HttpStatus.FORBIDDEN, "Mã lớp học hiện không hoạt động"),
    ALREADY_CLASS_MEMBER(HttpStatus.CONFLICT, "Bạn đã là thành viên của lớp học"),
    CLASS_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    CLASS_MEMBER_NOT_PENDING(HttpStatus.CONFLICT, "Thành viên không ở trạng thái chờ duyệt"),
    CLASS_OWNER_ROLE_CANNOT_CHANGE(HttpStatus.CONFLICT, "Không thể thay đổi vai trò của chủ sở hữu lớp học"),
    CLASS_OWNER_CANNOT_BE_REMOVED(HttpStatus.CONFLICT, "Không thể xóa chủ sở hữu khỏi lớp học"),
    CLASS_MEMBER_CANNOT_LEAVE(HttpStatus.CONFLICT, "Giảng viên hoặc chủ sở hữu không thể tự rời lớp học"),
    // Invitation
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy lời mời"),
    INVITATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "Lời mời đang chờ xử lý đã tồn tại"),
    INVITATION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "Lời mời này đã được xử lý"),
    INVITATION_EXPIRED(HttpStatus.GONE, "Lời mời đã hết hạn"),
    INVITATION_EMAIL_MISMATCH(HttpStatus.FORBIDDEN, "Lời mời này không dành cho tài khoản hiện tại"),
    INVALID_INVITATION_ROLE(HttpStatus.BAD_REQUEST, "Vai trò trong lời mời không hợp lệ"),
    //    TOKEN
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Mã không hợp lệ hoặc đã hết hạn"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Mã đã hết hạn"),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy môn học"),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền xem môn học của người dùng này"),
    COURSE_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "Môn học đã được gán cho lớp này"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}