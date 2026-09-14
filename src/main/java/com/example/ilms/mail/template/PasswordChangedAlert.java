package com.example.ilms.mail.template;

public class PasswordChangedAlert {

    public static String buildTemplate(String email) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;">
                <h2 style="color: #d93025; text-align: center; margin-bottom: 20px;">Cảnh báo bảo mật: Mật khẩu đã thay đổi</h2>
                
                <p style="color: #333333; font-size: 15px; line-height: 1.5;">Xin chào <strong>%s</strong>,</p>
                <p style="color: #333333; font-size: 15px; line-height: 1.5;">
                    Mật khẩu tài khoản ILMS của bạn vừa được cập nhật thành công.
                </p>

                <div style="background-color: #fce8e6; border-left: 4px solid #d93025; padding: 12px 16px; margin: 20px 0; border-radius: 4px;">
                    <p style="color: #c5221f; font-size: 14px; margin: 0; line-height: 1.4;">
                        <strong>Cảnh báo:</strong> Nếu bạn không thực hiện thay đổi này, tài khoản của bạn có thể đã bị xâm nhập. Vui lòng liên hệ ngay với ban quản trị hoặc đặt lại mật khẩu khẩn cấp.
                    </p>
                </div>

                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 24px 0;" />
                <p style="color: #999999; font-size: 12px; text-align: center; margin: 0;">
                    Đây là email tự động từ hệ thống ILMS, vui lòng không phản hồi trực tiếp email này.
                </p>
            </div>
            """.formatted(email);
    }
}