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
    ENROLL_CLASS_SUCCESS(HttpStatus.OK, "Tham gia lớp học thành công"),
    GET_CLASSES_SUCCESS(HttpStatus.OK, "Lấy danh sách lớp học thành công"),
    GET_CLASS_MEMBERS_SUCCESS(HttpStatus.OK, "Lấy danh sách thành viên lớp thành công"),
    GET_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Lấy thông tin thành viên lớp thành công"),
    APPROVE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Phê duyệt thành viên vào lớp thành công"),
    CHANGE_CLASS_MEMBER_ROLE_SUCCESS(HttpStatus.OK, "Thay đổi vai trò thành viên thành công"),
    REMOVE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Xóa thành viên khỏi lớp thành công"),
    LEAVE_CLASS_SUCCESS(HttpStatus.OK, "Rời lớp học thành công"),
    INVITE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Gửi lời mời tham gia lớp học thành công"),
    ACCEPT_CLASS_INVITATION_SUCCESS(HttpStatus.OK, "Chấp nhận lời mời tham gia lớp học thành công"),

    GET_COURSES_SUCCESS(HttpStatus.OK, "Lấy danh sách môn học thành công"),
    GET_COURSE_SUCCESS(HttpStatus.OK, "Lấy thông tin môn học thành công"),
    GET_USER_COURSES_SUCCESS(HttpStatus.OK, "Lấy danh sách môn học của người dùng thành công"),
    CREATE_COURSE_SUCCESS(HttpStatus.CREATED, "Tạo môn học thành công"),
    UPDATE_COURSE_SUCCESS(HttpStatus.OK, "Cập nhật môn học thành công"),
    DELETE_COURSE_SUCCESS(HttpStatus.OK, "Xóa (ẩn) môn học thành công"),
    RESTORE_COURSE_SUCCESS(HttpStatus.OK, "Khôi phục môn học thành công"),
    GET_CLASS_COURSES_SUCCESS(HttpStatus.OK, "Lấy danh sách môn học của lớp thành công"),
    ASSIGN_COURSE_TO_CLASS_SUCCESS(HttpStatus.CREATED, "Gán môn học vào lớp thành công"),
    BATCH_ASSIGN_COURSES_SUCCESS(HttpStatus.OK, "Gán nhiều môn học vào lớp hoàn tất"),

    // Question Module
    GET_QUESTIONS_SUCCESS(HttpStatus.OK, "Lấy danh sách câu hỏi thành công"),
    GET_QUESTION_DETAIL_SUCCESS(HttpStatus.OK, "Lấy thông tin chi tiết câu hỏi thành công"),
    CREATE_QUESTION_SUCCESS(HttpStatus.CREATED, "Tạo câu hỏi thành công"),
    UPDATE_QUESTION_SUCCESS(HttpStatus.OK, "Cập nhật câu hỏi thành công"),
    DELETE_QUESTION_SUCCESS(HttpStatus.OK, "Xóa câu hỏi thành công"),

    // Question Concept Module
    GET_CONCEPTS_SUCCESS(HttpStatus.OK, "Lấy danh sách khái niệm kiến thức thành công"),
    GET_CONCEPT_SUCCESS(HttpStatus.OK, "Lấy chi tiết khái niệm kiến thức thành công"),
    CONCEPT_CREATED(HttpStatus.CREATED, "Tạo khái niệm kiến thức thành công"),
    CONCEPT_UPDATED(HttpStatus.OK, "Cập nhật khái niệm kiến thức thành công"),
    CONCEPT_DELETED(HttpStatus.OK, "Xóa khái niệm kiến thức thành công");

    private final HttpStatus status;
    private final String message;
}