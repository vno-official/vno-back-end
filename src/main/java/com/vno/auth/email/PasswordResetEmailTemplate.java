package com.vno.auth.email;

/**
 * Email template for password reset links.
 * Follows the same professional design pattern as MagicLinkEmailTemplate.
 */
public class PasswordResetEmailTemplate implements EmailTemplate {

    private final String resetLink;
    
    public PasswordResetEmailTemplate(String resetLink) {
        this.resetLink = resetLink;
    }
    
    @Override
    public String getSubject() {
        return "Reset Your Password - VNO";
    }
    
    @Override
    public String getHtmlBody() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reset Your Password</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f7;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f5f5f7;">
                    <tr>
                        <td align="center" style="padding: 40px 20px;">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 48px 40px 32px; text-align: center; border-bottom: 1px solid #e5e5e7;">
                                        <h1 style="margin: 0; font-size: 28px; font-weight: 600; color: #1d1d1f; letter-spacing: -0.5px;">
                                            🔐 Reset Your Password
                                        </h1>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px;">
                                        <p style="margin: 0 0 24px; font-size: 16px; line-height: 1.6; color: #1d1d1f;">
                                            Hello,
                                        </p>
                                        
                                        <p style="margin: 0 0 24px; font-size: 16px; line-height: 1.6; color: #1d1d1f;">
                                            We received a request to reset your password. Click the button below to create a new password:
                                        </p>
                                        
                                        <!-- CTA Button -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; margin: 32px 0;">
                                            <tr>
                                                <td align="center">
                                                    <a href="%s" 
                                                       style="display: inline-block; padding: 16px 48px; background-color: #0071e3; color: #ffffff; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 500; letter-spacing: 0.3px; transition: background-color 0.2s;">
                                                        Reset Password
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Security Notice -->
                                        <div style="margin: 32px 0; padding: 20px; background-color: #fff3cd; border-left: 4px solid #ffc107; border-radius: 6px;">
                                            <p style="margin: 0 0 12px; font-size: 14px; font-weight: 600; color: #856404;">
                                                ⚠️ Security Notice
                                            </p>
                                            <p style="margin: 0; font-size: 14px; line-height: 1.5; color: #856404;">
                                                This link will expire in <strong>1 hour</strong> and can only be used once. If you didn't request this password reset, please ignore this email.
                                            </p>
                                        </div>
                                        
                                        <p style="margin: 24px 0 0; font-size: 14px; line-height: 1.6; color: #86868b;">
                                            Or copy and paste this link into your browser:
                                        </p>
                                        <p style="margin: 8px 0 0; font-size: 13px; line-height: 1.4; color: #0071e3; word-break: break-all;">
                                            <a href="%s" style="color: #0071e3; text-decoration: none;">%s</a>
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="padding: 32px 40px; background-color: #f5f5f7; border-top: 1px solid #e5e5e7; border-radius: 0 0 12px 12px;">
                                        <p style="margin: 0 0 12px; font-size: 13px; line-height: 1.5; color: #86868b; text-align: center;">
                                            Need help? Contact us at support@vno.com
                                        </p>
                                        <p style="margin: 0; font-size: 12px; line-height: 1.4; color: #86868b; text-align: center;">
                                            © 2024 VNO. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(resetLink, resetLink, resetLink);
    }
    
    @Override
    public String getTextBody() {
        return """
            Reset Your Password - VNO
            
            We received a request to reset your password.
            
            Click the link below to create a new password:
            %s
            
            SECURITY NOTICE:
            This link will expire in 1 hour and can only be used once.
            If you didn't request this password reset, please ignore this email.
            
            Need help? Contact us at support@vno.com
            
            © 2024 VNO. All rights reserved.
            """.formatted(resetLink);
    }
}
