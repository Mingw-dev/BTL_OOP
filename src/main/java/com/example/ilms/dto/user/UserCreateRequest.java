package com.example.ilms.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor // Sinh constructor không có tham số

@AllArgsConstructor //sinh constructor chứa tất cả các thuộc tính của class

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {

//    @NotBlank(message = "Họ và tên không được để trống")
//    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
//    private String fullName;

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
