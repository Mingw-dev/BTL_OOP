package com.example.ilms.dto.announcement;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementCreateRequestDTO {
    @NotBlank(message = "Tiêu đề không được để trống") //là một annotation dùng để kiểm tra validation (xác thực dữ liệu),
    // thường dùng trong DTO để đảm bảo một chuỗi không được null, không được rỗng, và không chỉ chứa khoảng trắng.
    private String title;
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
