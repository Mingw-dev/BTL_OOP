package com.example.ilms.dto.classDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinByCodeRequestDTO {

    @NotBlank(message = "Mã lớp không được để trống")
    @JsonProperty("class_code")
    private String classCode;
}