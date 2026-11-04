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
    VERIFY_TOKEN_RESET_PASSWORD_SUCCESS(HttpStatus.OK, "Token hợp lệ, bạn có thể đặt lại mật khẩu"),
    CHANGED_PASSWORD_SUCCESS(HttpStatus.OK, "Đổi mật khẩu thành công, vui lòng đăng nhập lại"),

    // User Module
    GET_USER_SUCCESS(HttpStatus.OK, "Lấy thông tin người dùng thành công"),
    UPDATE_PROFILE_SUCCESS(HttpStatus.OK, "Cập nhật hồ sơ thành công"),
    CREATE_USER_SUCCESS(HttpStatus.OK, "Tạo người dùng mới thành công"),

    // Assignment Module
    GET_ASSIGNMENTS_SUCCESS(HttpStatus.OK, "Lấy danh sách bài tập thành công"),
    GET_ASSIGNMENT_SUCCESS(HttpStatus.OK, "Lấy thông tin bài tập thành công"),
    CREATE_ASSIGNMENT_SUCCESS(HttpStatus.CREATED, "Tạo bài tập thành công"),
    UPDATE_ASSIGNMENT_SUCCESS(HttpStatus.OK, "Cập nhật bài tập thành công"),
    // Submission Module
    GET_SUBMISSIONS_SUCCESS(HttpStatus.OK, "Lấy danh sách bài nộp thành công"),
    GET_SUBMISSION_SUCCESS(HttpStatus.OK, "Lấy bài nộp thành công"),
    CREATE_SUBMISSION_SUCCESS(HttpStatus.CREATED, "Nộp bài thành công"),
    UPDATE_SUBMISSION_SUCCESS(HttpStatus.OK, "Cập nhật bài nộp thành công"),
    GRADE_SUBMISSION_SUCCESS(HttpStatus.OK, "Chấm bài thành công"),
    BATCH_GRADE_SUBMISSIONS_SUCCESS(HttpStatus.OK, "Chấm bài theo batch thành công"),
    BATCH_GRADE_SUBMISSIONS_PARTIAL_SUCCESS(HttpStatus.MULTI_STATUS, "Đã xử lý batch, có bài nộp không chấm được"),
    // Class Module
    CLASS_CREATED(HttpStatus.CREATED, "Tạo lớp học thành công"),
    ENROLL_CLASS_SUCCESS(HttpStatus.OK, "Tham gia lớp học thành công"),
    GET_CLASSES_SUCCESS(HttpStatus.OK, "Lấy danh sách lớp học thành công"),
    GET_CLASS_SUCCESS(HttpStatus.OK, "Lấy thông tin lớp học thành công"),
    UPDATE_CLASS_STATUS_SUCCESS(HttpStatus.OK, "Cập nhật trạng thái lớp học thành công"),

    // Class Member Module
    GET_CLASS_MEMBERS_SUCCESS(HttpStatus.OK, "Lấy danh sách thành viên lớp thành công"),
    GET_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Lấy thông tin thành viên lớp thành công"),
    APPROVE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Phê duyệt thành viên vào lớp thành công"),
    CHANGE_CLASS_MEMBER_ROLE_SUCCESS(HttpStatus.OK, "Thay đổi vai trò thành viên thành công"),
    REMOVE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Xóa thành viên khỏi lớp thành công"),
    LEAVE_CLASS_SUCCESS(HttpStatus.OK, "Rời lớp học thành công"),
    INVITE_CLASS_MEMBER_SUCCESS(HttpStatus.OK, "Gửi lời mời tham gia lớp học thành công"),
    ACCEPT_CLASS_INVITATION_SUCCESS(HttpStatus.OK, "Chấp nhận lời mời tham gia lớp học thành công"),

    // Announcement Module
    CREATE_ANNOUCEMENT_SUCCESS(HttpStatus.OK, "Tạo thông báo lớp học thành công"),
    GET_ANNOUCEMENTS_SUCESS(HttpStatus.OK, "Lấy danh sách thông báo thành công"),
    UPDATE_ANNOUNCEMENT_SUCCESS(HttpStatus.OK, "Cập nhật trạng thái thông báo thành công"),

    // Room Module
    DELETE_ROOM_SUCCESS(HttpStatus.OK, "Xóa phòng học thành công"),
    UPDATE_ROOM_SUCCESS(HttpStatus.OK, "Cập nhật thông tin phòng học thành công"),
    GET_ROOMS_SUCCESS(HttpStatus.OK, "Lấy danh sách phòng học thành công"),
    CREATE_ROOMS_SUUCESS(HttpStatus.OK, "Tạo phòng học thành công"),

    // Lesson Session Module
    GET_LESSON_SESSION_SUCCESS(HttpStatus.OK, "Lấy danh sách ca học thành công"),
    CREATE_LESSON_SESSION_SUCCESS(HttpStatus.OK, "Tạo ca học thành công"),

    // Attendance Module
    GET_ATTENDANCES_SUCCESS(HttpStatus.OK, "Lấy danh sách điểm danh ca học thành công"),
    TAKE_ATTENDANCE_SUCCESS(HttpStatus.OK, "Điểm danh ca học thành công"),

    // Course Module
    GET_COURSES_SUCCESS(HttpStatus.OK, "Lấy danh sách môn học thành công"),
    GET_COURSE_SUCCESS(HttpStatus.OK, "Lấy thông tin môn học thành công"),
    GET_USER_COURSES_SUCCESS(HttpStatus.OK, "Lấy danh sách môn học của người dùng thành công"),
    CREATE_COURSE_SUCCESS(HttpStatus.CREATED, "Tạo môn học thành công"),
    UPDATE_COURSE_SUCCESS(HttpStatus.OK, "Cập nhật môn học thành công"),
    DELETE_COURSE_SUCCESS(HttpStatus.OK, "Xóa (ẩn) môn học thành công"),
    RESTORE_COURSE_SUCCESS(HttpStatus.OK, "Khôi phục môn học thành công"),
    UPDATE_COURSE_INSTRUCTOR_SUCCESS(HttpStatus.OK, "Cập nhật giảng viên môn học thành công"),

    // Class Course Module
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