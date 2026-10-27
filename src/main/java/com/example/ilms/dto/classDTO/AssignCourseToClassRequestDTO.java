package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignCourseToClassRequestDTO {

    @NotNull(message = "ID môn học không được để trống")
    private UUID courseId;

    private UUID teacherId;

    @NotBlank(message = "Học kỳ không được để trống")
    @Size(max = 50, message = "Học kỳ tối đa 50 ký tự")
    private String semester;

    @NotBlank(message = "Niên khóa không được để trống")
    @Size(max = 20, message = "Niên khóa tối đa 20 ký tự")
    private String academicYear;

    private LocalDate startDate;

    private LocalDate endDate;
}