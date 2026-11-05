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

    // Token
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Mã không hợp lệ hoặc đã hết hạn"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Mã đã hết hạn"),
    //Assignment
    ASSIGNMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"),
    LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bài học"),
    ASSIGNMENT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu bài tập không hợp lệ"),
    ASSIGNMENT_CONFLICT(HttpStatus.CONFLICT, "Trạng thái hoặc dữ liệu bài tập không cho phép thực hiện thao tác"),
    // Submission Module
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bài nộp"),
    SUBMISSION_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu bài nộp hoặc chấm điểm không hợp lệ"),
    SUBMISSION_ALREADY_EXISTS(HttpStatus.CONFLICT, "Đã có bài nộp cho bài tập này"),
    SUBMISSION_ALREADY_GRADED(HttpStatus.CONFLICT, "Bài nộp đã được chấm, không thể sửa hoặc hủy"),
    SUBMISSION_ATTEMPTS_EXHAUSTED(HttpStatus.CONFLICT, "Đã hết lượt nộp bài"),
    SUBMISSION_DEADLINE_PASSED(HttpStatus.CONFLICT, "Đã quá hạn thực hiện thao tác với bài nộp"),
    SUBMISSION_CLASS_READ_ONLY(HttpStatus.CONFLICT, "Lớp đã lưu trữ hoặc liên kết môn học không còn ACTIVE"),
    SUBMISSION_DATA_INCONSISTENT(HttpStatus.CONFLICT, "Dữ liệu bài tập, bài nộp hoặc kết quả chấm không nhất quán"),
    SUBMISSION_WRITE_CONFLICT(HttpStatus.CONFLICT, "Không thể ghi kết quả do xung đột dữ liệu hoặc giao dịch"),
    //Grading
    GRADING_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy kết quả chấm"),
    GRADING_ALREADY_EXISTS(HttpStatus.CONFLICT, "Bài nộp đã có kết quả chấm"),
    GRADING_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu chấm điểm không hợp lệ"),
    GRADING_DATA_INCONSISTENT(HttpStatus.CONFLICT, "Dữ liệu bài tập, bài thi, bài nộp hoặc kết quả chấm không nhất quán"),
    GRADING_WRITE_CONFLICT(HttpStatus.CONFLICT, "Không thể ghi kết quả chấm do xung đột dữ liệu hoặc giao dịch"),
    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy môn học"),
    COURSE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Bạn không có quyền xem môn học của người dùng này"),
    COURSE_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "Môn học đã được gán cho lớp này"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu"),
    COURSE_FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa môn học này"),
    COURSE_RESTORE_FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền khôi phục môn học"),
    COURSE_NOT_DELETED(HttpStatus.BAD_REQUEST, "Môn học chưa bị xóa nên không thể khôi phục"),
    COURSE_INSTRUCTOR_FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền thay đổi giảng viên của môn học này"),
    COURSE_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền cập nhật môn học này"),
    INVALID_COURSE_DATA(HttpStatus.BAD_REQUEST, "Dữ liệu cập nhật môn học không hợp lệ"),

    // Chapter
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy chương học"),
    CHAPTER_FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền quản lý chương học của môn học này"),
    INVALID_CHAPTER_DATA(HttpStatus.BAD_REQUEST, "Dữ liệu chương học không hợp lệ"),

    // Question
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy câu hỏi hoặc câu hỏi đã bị xóa"),
    CONCEPT_NOT_FOUND(HttpStatus.NOT_FOUND, "Nhãn kiến thức không tồn tại trong hệ thống"),
    INVALID_QUESTION_DATA(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào của câu hỏi không hợp lệ"),
    MISSING_CORRECT_ANSWER(HttpStatus.BAD_REQUEST, "Câu hỏi bắt buộc phải có đáp án đúng"),
    QUESTION_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "Câu hỏi này đã bị vô hiệu hóa hoặc không còn hoạt động"),

    // Question Concept
    CONCEPT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Câu hỏi này đã được gắn nhãn kiến thức này rồi"),

    // Lesson Session
    INVAILD_LESSONSESSION(HttpStatus.BAD_REQUEST, "Ca học không hợp lệ"),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Ca học không tồn tại"),
    TEACHER_SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "Giáo viên bị trùng lịch"),
    INVALID_REQUESTS(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    DUPLICATE_ATTENDANCE_STUDENT(HttpStatus.BAD_REQUEST, "Học sinh bị trùng trong danh sách điểm danh"),
    ACCOUNT_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "Tài khoản chưa được kích hoạt"),
    CLASS_MEMBER_NOT_STUDENT(HttpStatus.BAD_REQUEST,"Thành viên lớp học không có vai trò là học sinh"),

    // Announcement
    ANNOUNCEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Thông báo không tồn tại"),

    // Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Phòng học không tồn tại"),
    ROOM_SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "Phòng học bị trùng");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}