package com.example.ilms.mail.template;

public class InvitationTemplate {

    public static String buildTemplate(String className, String invitationUrl) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;">
                <h2 style="color: #1a73e8; text-align: center;">
                    Lời mời tham gia lớp học
                </h2>

                <p>
                    Bạn đã nhận được lời mời tham gia lớp học:
                </p>

                <p style="font-size: 18px; font-weight: bold;">
                    %s
                </p>

                <p>
                    Nhấn vào nút bên dưới để chấp nhận lời mời.
                </p>

                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s"
                       style="background-color: #1a73e8;
                              color: white;
                              padding: 12px 24px;
                              text-decoration: none;
                              border-radius: 6px;">
                        Tham gia lớp học
                    </a>
                </div>

                <p style="font-size: 13px; color: #666666;">
                    Nếu bạn không yêu cầu hoặc không mong muốn lời mời này,
                    bạn có thể bỏ qua email.
                </p>
            </div>
            """.formatted(className, invitationUrl);
    }
}
