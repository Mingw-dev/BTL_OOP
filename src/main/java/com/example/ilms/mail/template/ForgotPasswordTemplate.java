package com.example.ilms.mail.template;

public class ForgotPasswordTemplate {

    public static String buildTemplate(String resetUrl) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;">
                <h2 style="color: #1a73e8; text-align: center; margin-bottom: 20px;">Đặt lại mật khẩu ILMS</h2>
                
                <p style="color: #333333; font-size: 15px; line-height: 1.5;">Xin chào,</p>
                <p style="color: #333333; font-size: 15px; line-height: 1.5;">
                    Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản ILMS của bạn. Vui lòng bấm vào nút bên dưới để tiến hành tạo mật khẩu mới:
                </p>

                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" 
                       style="background-color: #1a73e8; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 15px; display: inline-block;">
                        Đặt lại mật khẩu
                    </a>
                </div>

                <p style="color: #666666; font-size: 13px; line-height: 1.5;">
                    * Lưu ý: Đường dẫn này chỉ có hiệu lực trong vòng <strong>15 phút</strong>. Nếu nút bấm không hoạt động, bạn có thể copy và dán liên kết sau vào trình duyệt:
                </p>
                <p style="word-break: break-all; font-size: 12px; color: #1a73e8;">
                    <a href="%s" style="color: #1a73e8;">%s</a>
                </p>

                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 24px 0;" />
                <p style="color: #999999; font-size: 12px; text-align: center; margin: 0;">
                    Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này để đảm bảo an toàn cho tài khoản.
                </p>
            </div>
            """.formatted(resetUrl, resetUrl, resetUrl);
    }
}