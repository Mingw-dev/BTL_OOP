package com.example.ilms.dto.classDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassCreateRequestDTO{
        @NotBlank(message = "Tên không được để trống")
       private String name;
       private String title;
       private String description;
       @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "Visibility chỉ được phép là PUBLIC hoặc PRIVATE")
       private String visibility;
       LocalDateTime startAt;
       LocalDateTime endAt;
}
