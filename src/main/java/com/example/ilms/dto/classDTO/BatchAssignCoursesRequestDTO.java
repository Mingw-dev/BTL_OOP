package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAssignCoursesRequestDTO {

    @NotEmpty(message = "Danh sách môn học không được để trống")
    @Size(max = 50, message = "Mỗi lần gán tối đa 50 môn học")
    private List<UUID> courseIds;

    @NotBlank(message = "Học kỳ không được để trống")
    @Size(max = 50, message = "Học kỳ tối đa 50 ký tự")
    private String semester;

    @NotBlank(message = "Niên khóa không được để trống")
    @Size(max = 20, message = "Niên khóa tối đa 20 ký tự")
    private String academicYear;
}