package com.example.ilms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {


    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotNull(message = "Mật khẩu không được để trống")
    String password;

    @NotNull(message = "First name khong duoc de trong")
    String firstName;

    @NotNull(message = "Tên không duoc de trong")
    String lastName;

    @NotNull(message = "Tên đệm khong duoc de trong")
    String middleName;



}
