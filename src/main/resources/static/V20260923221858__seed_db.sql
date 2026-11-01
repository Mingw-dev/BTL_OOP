-- Tắt kiểm tra khóa ngoại để tránh lỗi thứ tự khi insert
SET FOREIGN_KEY_CHECKS = 0;

-- ==============================================================================
-- PHẦN 1: HỆ THỐNG PHÂN QUYỀN VÀ NGƯỜI DÙNG (Bảng 1, 2, 3, 4, 37)
-- ==============================================================================

-- 1. Bảng roles (Vai trò)
INSERT INTO `roles` (`id`, `code`, `name`, `description`, `created_at`, `updated_at`) VALUES
                                                                                          (UNHEX('11111111111111111111111111111111'), 'ROLE_ADMIN', 'Quản trị viên', 'Quản trị hệ thống', NOW(), NOW()),
                                                                                          (UNHEX('22222222222222222222222222222222'), 'ROLE_TEACHER', 'Giảng viên', 'Giảng viên', NOW(), NOW()),
                                                                                          (UNHEX('33333333333333333333333333333333'), 'ROLE_STUDENT', 'Học viên', 'Sinh viên PTIT', NOW(), NOW());

-- 3. Bảng permissions (Quyền hạn)
INSERT INTO `permissions` (`id`, `code`, `name`, `module`, `description`, `created_at`, `updated_at`) VALUES
                                                                                                          (UNHEX('00000000000000000000000000000001'), 'COURSE_CREATE', 'Tạo khóa học', 'COURSE', 'Tạo mới khóa học', NOW(), NOW()),
                                                                                                          (UNHEX('00000000000000000000000000000002'), 'COURSE_VIEW', 'Xem khóa học', 'COURSE', 'Xem nội dung', NOW(), NOW());

-- 4. Bảng role_permissions (Liên kết Vai trò - Quyền hạn)
INSERT INTO `role_permissions` (`role_id`, `permission_id`, `created_at`) VALUES
                                                                              (UNHEX('22222222222222222222222222222222'), UNHEX('00000000000000000000000000000001'), NOW()),
                                                                              (UNHEX('33333333333333333333333333333333'), UNHEX('00000000000000000000000000000002'), NOW());

-- 2. Bảng users (Người dùng)
INSERT INTO `users` (`id`, `role_id`, `first_name`, `middle_name`, `last_name`, `email`, `password`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                                                               (UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), UNHEX('22222222222222222222222222222222'), 'Hương', 'Thị', 'Trần', 'huongtt@ptit.edu.vn', '$2a$10$Dk4t', 'ACTIVE', NOW(), NOW()),
                                                                                                                                               (UNHEX('12345678901234567890123456789012'), UNHEX('33333333333333333333333333333333'), 'Nam', 'Hoàng', 'Nguyễn', 'namnh.student@gmail.com', '$2a$10$Dk4t', 'ACTIVE', NOW(), NOW());

-- 37. Bảng verification_tokens (Token xác thực tài khoản)
INSERT INTO `verification_tokens` (`id`, `user_id`, `token`, `email`, `type`, `status`, `expires_at`, `created_at`) VALUES
    (UNHEX('F2F2F2F2F2F2F2F2F2F2F2F2F2F2F2F2'), UNHEX('12345678901234567890123456789012'), 'verify-email-namnh-999', 'namnh.student@gmail.com', 'EMAIL_VERIFICATION', 'VERIFIED', '2026-08-01 23:59:59', NOW());


-- ==============================================================================
-- PHẦN 2: QUẢN LÝ KHÓA HỌC VÀ LỚP HỌC (Bảng 5, 6, 7, 8, 9, 10, 24, 26)
-- ==============================================================================

-- 5. Bảng courses (Khóa học)
INSERT INTO `courses` (`id`, `course_code`, `name`, `description`, `visibility`, `owner_id`, `created_at`, `updated_at`) VALUES
    (UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), 'INT1306', 'Cơ sở dữ liệu', 'Khóa học CSDL', 'PUBLIC', UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), NOW(), NOW());

-- 6. Bảng classes (Lớp học)
INSERT INTO `classes` (`id`, `class_code`, `name`, `title`, `description`, `visibility`, `require_approval`, `is_code_active`, `owner_id`, `start_at`, `end_at`, `created_at`, `updated_at`) VALUES
    (UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), 'D24CQCN01-B', 'Lớp D24 CNTT 1', 'Lớp tín chỉ D24CQCN01-B', 'Lớp học CNTT', 'PUBLIC', 1, 1, UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), '2026-08-15', '2026-12-30', NOW(), NOW());

-- 8. Bảng class_courses (Môn học của lớp)
INSERT INTO `class_courses` (`id`, `class_id`, `course_id`, `assigned_at`, `status`) VALUES
    (UNHEX('CCCC1111CCCC1111CCCC1111CCCC1111'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), NOW(), 'ACTIVE');

-- 7. Bảng class_members (Thành viên lớp học)
INSERT INTO `class_members` (`id`, `class_id`, `user_id`, `role`, `status`, `joined_at`) VALUES
                                                                                             (UNHEX('BBBB1111BBBB1111BBBB1111BBBB1111'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'TEACHER', 'ACTIVE', NOW()),
                                                                                             (UNHEX('BBBB2222BBBB2222BBBB2222BBBB2222'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('12345678901234567890123456789012'), 'STUDENT', 'ACTIVE', NOW());

-- 9. Bảng chapters (Chương học)
INSERT INTO `chapters` (`id`, `course_id`, `title`, `description`, `order_index`, `is_published`, `created_at`, `updated_at`) VALUES
    (UNHEX('DDDD1111DDDD1111DDDD1111DDDD1111'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), 'Chương 1: Tổng quan', 'Giới thiệu CSDL', 1, 1, NOW(), NOW());

-- 10. Bảng lessons (Bài học)
INSERT INTO `lessons` (`id`, `chapter_id`, `title`, `content`, `lesson_type`, `video_url`, `duration_minutes`, `order_index`, `is_published`, `created_at`, `updated_at`) VALUES
    (UNHEX('EEEE1111EEEE1111EEEE1111EEEE1111'), UNHEX('DDDD1111DDDD1111DDDD1111DDDD1111'), 'Bài 1: Khái niệm CSDL', '<p>Giới thiệu CSDL</p>', 'VIDEO', 'https://youtu.be/abc', 45, 1, 1, NOW(), NOW());

-- 24. Bảng class_announcements (Thông báo lớp học)
INSERT INTO `class_announcements` (`id`, `class_id`, `author_id`, `title`, `content`, `is_pinned`, `created_at`, `updated_at`) VALUES
    (UNHEX('D1D1D1D1D1D1D1D1D1D1D1D1D1D1D1D1'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'Lịch thi giữa kỳ', 'Tuần sau thi nhé!', 1, NOW(), NOW());

-- 26. Bảng invitations (Lời mời tham gia lớp học)
INSERT INTO `invitations` (`id`, `class_id`, `invite_by`, `email`, `role`, `token`, `status`, `expires_at`, `created_at`) VALUES
    (UNHEX('F1F1F1F1F1F1F1F1F1F1F1F1F1F1F1F1'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'test@ptit.edu.vn', 'STUDENT', 'token-123', 'PENDING', '2026-10-15', NOW());


-- ==============================================================================
-- PHẦN 3: LỊCH TRÌNH VÀ ĐIỂM DANH (Bảng 33, 36, 20, 19)
-- ==============================================================================

-- 33. Bảng rooms (Phòng học)
INSERT INTO `rooms` (`id`, `name`, `type`, `description`) VALUES
    (UNHEX('1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A'), 'Phòng 301-A2', 'THEORY', 'Khu A2 PTIT');

-- 36. Bảng time_slots (Ca học / Kíp học)
INSERT INTO `time_slots` (`id`, `name`, `start_time`, `end_time`, `start_shift`, `end_shift`, `created_at`, `updated_at`) VALUES
    (UNHEX('2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A'), 'Kíp 1 (Sáng)', '07:00:00', '09:00:00', 1, 2, NOW(), NOW());

-- 20. Bảng lesson_sessions (Buổi học)
INSERT INTO `lesson_sessions` (`id`, `room_id`, `class_id`, `time_slot_id`, `session_date`, `note`) VALUES
    (UNHEX('3A3A3A3A3A3A3A3A3A3A3A3A3A3A3A3A'), UNHEX('1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A2A'), '2026-10-10', 'Học lý thuyết');

-- 19. Bảng attendances (Điểm danh)
INSERT INTO `attendances` (`id`, `session_id`, `user_id`, `status`, `check_in_time`, `note`, `created_at`, `updated_at`) VALUES
    (UNHEX('4A4A4A4A4A4A4A4A4A4A4A4A4A4A4A4A'), UNHEX('3A3A3A3A3A3A3A3A3A3A3A3A3A3A3A3A'), UNHEX('12345678901234567890123456789012'), 'PRESENT', '2026-10-10 06:55:00', 'Đúng giờ', NOW(), NOW());


-- ==============================================================================
-- PHẦN 4: TÀI LIỆU, BÀI TẬP VÀ ĐÁNH GIÁ (Bảng 11, 15, 12, 13, 14, 25)
-- ==============================================================================

-- 11. Bảng materials (Tài liệu học tập)
INSERT INTO `materials` (`id`, `course_id`, `lesson_id`, `uploaded_by`, `title`, `description`, `file_url`, `file_type`, `file_size_bytes`, `is_downloadable`, `created_at`, `updated_at`) VALUES
    (UNHEX('FFFF1111FFFF1111FFFF1111FFFF1111'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), UNHEX('EEEE1111EEEE1111EEEE1111EEEE1111'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'Slide Bài 1', 'Slide PDF', 'url/slide.pdf', 'pdf', 2048, 1, NOW(), NOW());

-- 15. Bảng assignments (Bài tập về nhà)
INSERT INTO `assignments` (`id`, `lesson_id`, `title`, `description`, `max_score`, `due_date`, `allow_late_submission`, `file_attachment_url`, `created_at`, `updated_at`) VALUES
    (UNHEX('DDDD3333DDDD3333DDDD3333DDDD3333'), UNHEX('EEEE1111EEEE1111EEEE1111EEEE1111'), 'Bài tập 3NF', 'Chuẩn hóa 3NF', 10.00, '2026-10-20', 1, 'url/hw.pdf', NOW(), NOW());

-- 12. Bảng assessments (Kỳ đánh giá/Bài tập lớn)
INSERT INTO `assessments` (`id`, `class_id`, `course_id`, `created_by`, `title`, `created_at`, `updated_at`) VALUES
    (UNHEX('AAAA4444AAAA4444AAAA4444AAAA4444'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'Báo cáo BTL', NOW(), NOW());

-- 13. Bảng submissions (Bài nộp)
INSERT INTO `submissions` (`id`, `assessment_id`, `user_id`, `attempt_number`, `max_attempts`, `status`, `submission_text`, `submitted_at`) VALUES
    (UNHEX('BBBB4444BBBB4444BBBB4444BBBB4444'), UNHEX('AAAA4444AAAA4444AAAA4444AAAA4444'), UNHEX('12345678901234567890123456789012'), 1, 3, 'SUBMITTED', 'Em nộp bài', NOW());

-- 14. Bảng gradings (Chấm điểm)
INSERT INTO `gradings` (`id`, `submission_id`, `grader_id`, `score`, `feedback`, `graded_at`, `created_at`, `updated_at`) VALUES
    (UNHEX('CCCC3333CCCC3333CCCC3333CCCC3333'), UNHEX('BBBB4444BBBB4444BBBB4444BBBB4444'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 8.50, 'Làm tốt', NOW(), NOW(), NOW());

-- 25. Bảng grade_reports (Bảng điểm tổng hợp)
INSERT INTO `grade_reports` (`id`, `class_member_id`, `attendance_score`, `midterm_score`, `final_score`, `created_at`, `updated_at`) VALUES
    (UNHEX('E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1E1'), UNHEX('BBBB2222BBBB2222BBBB2222BBBB2222'), 10.00, 8.50, NULL, NOW(), NOW());


-- ==============================================================================
-- PHẦN 5: NGÂN HÀNG CÂU HỎI VÀ THI CỬ (Bảng 18, 28, 16, 17, 32, 31, 30, 29)
-- ==============================================================================

-- 18. Bảng questions (Ngân hàng câu hỏi)
INSERT INTO `questions` (`id`, `course_id`, `chapter_id`, `created_by`, `question_text`, `question_type`, `difficulty`, `explanation`, `source_type`, `is_verified_by_teacher`, `created_at`, `updated_at`) VALUES
    (UNHEX('5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), UNHEX('DDDD1111DDDD1111DDDD1111DDDD1111'), UNHEX('CCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDD'), 'Khóa chính là gì?', 'SINGLE_CHOICE', 'EASY', 'Định danh bản ghi', 'MANUAL', 1, NOW(), NOW());

-- 28. Bảng question_concepts (Concept câu hỏi)
INSERT INTO `question_concepts` (`id`, `question_id`, `concept_id`, `relevance_weight`) VALUES
    (UNHEX('6A6A6A6A6A6A6A6A6A6A6A6A6A6A6A6A'), UNHEX('5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A'), 'KHOA_CHINH', 1.00);

-- 16. Bảng exams (Kỳ thi)
INSERT INTO `exams` (`id`, `description`, `exam_type`, `duration_minutes`, `total_score`, `start_time`, `end_time`, `is_published`, `created_at`, `updated_at`) VALUES
    (UNHEX('7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A'), 'Thi cuối kỳ', 'FINAL', 60, 10.00, '2026-12-25 08:00:00', '2026-12-25 09:00:00', 1, NOW(), NOW());

-- 17. Bảng exam_questions (Câu hỏi trong đề thi)
INSERT INTO `exam_questions` (`id`, `exam_id`, `question_id`, `score`, `order_index`, `added_method`, `created_at`) VALUES
    (UNHEX('8A8A8A8A8A8A8A8A8A8A8A8A8A8A8A8A'), UNHEX('7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A'), UNHEX('5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A'), 2.00, 1, 'MANUAL', NOW());

-- 32. Bảng quizzes (Trắc nghiệm ngắn)
INSERT INTO `quizzes` (`id`, `class_id`, `lesson_id`, `course_id`, `title`, `duration_minutes`, `passing_score`, `max_attempts`, `is_published`, `created_at`, `updated_at`) VALUES
    (UNHEX('9A9A9A9A9A9A9A9A9A9A9A9A9A9A9A9A'), UNHEX('AAAA1111AAAA1111AAAA1111AAAA1111'), UNHEX('EEEE1111EEEE1111EEEE1111EEEE1111'), UNHEX('EEEEEEEEEEEEEEEEFFFFFFFFFFFFFFFF'), 'Quiz bài 1', 15, 5.00, 1, 1, NOW(), NOW());

-- 31. Bảng quiz_questions (Câu hỏi Quiz)
INSERT INTO `quiz_questions` (`id`, `quiz_id`, `bank_question_id`, `question_text`, `question_type`, `score`, `order_index`, `created_at`) VALUES
    (UNHEX('A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1'), UNHEX('9A9A9A9A9A9A9A9A9A9A9A9A9A9A9A9A'), UNHEX('5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A5A'), 'Khóa chính là gì?', 'SINGLE_CHOICE', 1.00, 1, NOW());

-- 30. Bảng quiz_options (Đáp án Quiz)
INSERT INTO `quiz_options` (`id`, `question_id`, `option_text`, `is_correct`, `order_index`) VALUES
    (UNHEX('B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1'), UNHEX('A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1'), 'Thuộc tính định danh duy nhất', 1, 1);

-- 29. Bảng quiz_answers (Bài làm trắc nghiệm)
INSERT INTO `quiz_answers` (`id`, `submission_id`, `question_id`, `selected_option_id`, `answer_text`, `is_correct`, `earned_score`) VALUES
    (UNHEX('C1C1C1C1C1C1C1C1C1C1C1C1C1C1C1C1'), UNHEX('BBBB4444BBBB4444BBBB4444BBBB4444'), UNHEX('A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1A1'), UNHEX('B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1'), NULL, 1, 1.00);


-- ==============================================================================
-- PHẦN 6: HỆ THỐNG AI & TRACKING (Bảng 21, 22, 23, 27, 34, 35)
-- ==============================================================================

-- 21. Bảng ai_chat_sessions (Phiên Chat AI)
INSERT INTO `ai_chat_sessions` (`id`, `user_id`, `title`, `started_at`, `last_updated_at`) VALUES
    (UNHEX('1C1C1C1C1C1C1C1C1C1C1C1C1C1C1C1C'), UNHEX('12345678901234567890123456789012'), 'Hỏi đáp chuẩn 3NF', NOW(), NOW());

-- 22. Bảng ai_chat_messages (Tin nhắn AI)
INSERT INTO `ai_chat_messages` (`id`, `session_id`, `content`, `message_type`, `sent_at`, `user_feedback`, `feedback_rating`) VALUES
    (UNHEX('2C2C2C2C2C2C2C2C2C2C2C2C2C2C2C2C'), UNHEX('1C1C1C1C1C1C1C1C1C1C1C1C1C1C1C1C'), 'AI giải thích chuẩn 3NF?', 'USER', NOW(), NULL, NULL);

-- 34. Bảng student_error_patterns (Phân tích lỗi)
INSERT INTO `student_error_patterns` (`id`, `user_id`, `knowledge_entity_id`, `error_description`, `frequency`, `last_occurred_at`) VALUES
    (UNHEX('4C4C4C4C4C4C4C4C4C4C4C4C4C4C4C4C'), UNHEX('12345678901234567890123456789012'), 'CHUAN_HOA_3NF', 'Nhầm lẫn khóa phụ và chính', 2, NOW());

-- 35. Bảng student_knowledge_heatmaps (Bản đồ nhiệt kiến thức)
INSERT INTO `student_knowledge_heatmaps` (`id`, `user_id`, `knowledge_entity_id`, `mastery_level`, `last_assessed_at`, `created_at`) VALUES
    (UNHEX('5C5C5C5C5C5C5C5C5C5C5C5C5C5C5C5C'), UNHEX('12345678901234567890123456789012'), 'CHUAN_HOA_3NF', 0.850, NOW(), NOW());

-- 23. Bảng ai_study_recommendations (Khuyến nghị học tập)
INSERT INTO `ai_study_recommendations` (`id`, `user_id`, `knowledge_entity_id`, `priority_level`, `status`, `recommendation_note`, `created_at`, `updated_at`) VALUES
    (UNHEX('7C7C7C7C7C7C7C7C7C7C7C7C7C7C7C7C'), UNHEX('12345678901234567890123456789012'), 'CHUAN_HOA_3NF', 'MEDIUM', 'PENDING', 'Ôn tập lại chuẩn 3NF', NOW(), NOW());

-- 27. Bảng material_chunks (Đoạn tài liệu cho AI)
INSERT INTO `material_chunks` (`id`, `chunk_index`, `token_count`, `content_text`, `created_at`) VALUES
    (UNHEX('8C8C8C8C8C8C8C8C8C8C8C8C8C8C8C8C'), 1, 120, '3NF yêu cầu không có phụ thuộc bắc cầu...', NOW());

-- Bật lại kiểm tra khóa ngoại sau khi hoàn tất
SET FOREIGN_KEY_CHECKS = 1;