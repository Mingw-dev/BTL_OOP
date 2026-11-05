package com.example.ilms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequestDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotNull(message = "Mật khẩu không được để trống")
    String password;

    @NotNull(message = "Họ không được để trống")
    String firstName;

    @NotNull(message = "Tên không được để trống")
    String lastName;

    @NotNull(message = "Tên đệm không được để trống")
    String middleName;

    @NotNull(message = "Mã vai trò không được để trống")
    UUID roleId;

}
