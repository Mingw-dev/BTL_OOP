package com.example.ilms.dto.chapter;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Cả hai trường đều tùy chọn: trường nào null thì giữ nguyên giá trị cũ.
// Muốn đổi thứ tự chương dùng API riêng PATCH /chapters/{chapterId}/order.
@Getter
@Setter
public class ChapterUpdateRequestDTO {

    @Size(max = 255, message = "Tên chương học không được vượt quá 255 ký tự")
    private String title;

    private String description;
}