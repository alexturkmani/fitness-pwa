import { Resend } from 'resend';

let resend: Resend | null = null;

function getResend() {
  if (!resend) {
    if (!process.env.RESEND_API_KEY) {
      throw new Error('RESEND_API_KEY is not set');
    }
    resend = new Resend(process.env.RESEND_API_KEY);
  }
  return resend;
}

const FROM_EMAIL = process.env.FROM_EMAIL || 'Nexal <onboarding@resend.dev>';
const APP_URL = process.env.NEXTAUTH_URL || 'http://localhost:3000';

export async function sendVerificationEmail(email: string, token: string) {
  const verifyUrl = `${APP_URL}/api/auth/verify?token=${token}`;

  try {
    await getResend().emails.send({
      from: FROM_EMAIL,
      to: email,
      subject: 'Verify your Nexal account',
      html: `
        <div style="font-family: system-ui, -apple-system, sans-serif; max-width: 480px; margin: 0 auto; padding: 40px 20px;">
          <div style="text-align: center; margin-bottom: 32px;">
            <h1 style="color: #10b981; font-size: 28px; margin: 0;">Nexal</h1>
          </div>
          <div style="background: #f8fafc; border-radius: 16px; padding: 32px; border: 1px solid #e2e8f0;">
            <h2 style="color: #0f172a; font-size: 20px; margin: 0 0 12px;">Verify your email</h2>
            <p style="color: #64748b; font-size: 15px; line-height: 1.6; margin: 0 0 24px;">
              Thanks for signing up! Click the button below to verify your email address and activate your 7-day free trial.
            </p>
            <a href="${verifyUrl}" style="display: inline-block; background: linear-gradient(135deg, #10b981, #06b6d4); color: white; text-decoration: none; padding: 14px 32px; border-radius: 12px; font-weight: 600; font-size: 15px;">
              Verify Email
            </a>
            <p style="color: #94a3b8; font-size: 13px; margin: 24px 0 0; line-height: 1.5;">
              This link expires in 24 hours. If you didn't create an account, you can safely ignore this email.
            </p>
          </div>
          <p style="color: #cbd5e1; font-size: 12px; text-align: center; margin-top: 24px;">
            &copy; ${new Date().getFullYear()} Nexal. All rights reserved.
          </p>
        </div>
      `,
    });
    return { success: true };
  } catch (error) {
    console.error('Failed to send verification email:', error);
    return { success: false, error };
  }
}

export async function sendPasswordResetEmail(email: string, token: string) {
  const resetUrl = `${APP_URL}/reset-password?token=${token}`;

  try {
    await getResend().emails.send({
      from: FROM_EMAIL,
      to: email,
      subject: 'Reset your Nexal password',
      html: `
        <div style="font-family: system-ui, -apple-system, sans-serif; max-width: 480px; margin: 0 auto; padding: 40px 20px;">
          <div style="text-align: center; margin-bottom: 32px;">
            <h1 style="color: #10b981; font-size: 28px; margin: 0;">Nexal</h1>
          </div>
          <div style="background: #f8fafc; border-radius: 16px; padding: 32px; border: 1px solid #e2e8f0;">
            <h2 style="color: #0f172a; font-size: 20px; margin: 0 0 12px;">Reset your password</h2>
            <p style="color: #64748b; font-size: 15px; line-height: 1.6; margin: 0 0 24px;">
              We received a request to reset your password. Click the button below to choose a new password.
            </p>
            <a href="${resetUrl}" style="display: inline-block; background: linear-gradient(135deg, #10b981, #06b6d4); color: white; text-decoration: none; padding: 14px 32px; border-radius: 12px; font-weight: 600; font-size: 15px;">
              Reset Password
            </a>
            <p style="color: #94a3b8; font-size: 13px; margin: 24px 0 0; line-height: 1.5;">
              This link expires in 1 hour. If you didn't request this, you can safely ignore this email.
            </p>
          </div>
          <p style="color: #cbd5e1; font-size: 12px; text-align: center; margin-top: 24px;">
            &copy; ${new Date().getFullYear()} Nexal. All rights reserved.
          </p>
        </div>
      `,
    });
    return { success: true };
  } catch (error) {
    console.error('Failed to send password reset email:', error);
    return { success: false, error };
  }
}

export async function sendEmailChangeVerification(email: string, token: string) {
  const verifyUrl = `${APP_URL}/api/auth/verify-email-change?token=${token}`;

  try {
    await getResend().emails.send({
      from: FROM_EMAIL,
      to: email,
      subject: 'Verify your new email - Nexal',
      html: `
        <div style="font-family: system-ui, -apple-system, sans-serif; max-width: 480px; margin: 0 auto; padding: 40px 20px;">
          <div style="text-align: center; margin-bottom: 32px;">
            <h1 style="color: #10b981; font-size: 28px; margin: 0;">Nexal</h1>
          </div>
          <div style="background: #f8fafc; border-radius: 16px; padding: 32px; border: 1px solid #e2e8f0;">
            <h2 style="color: #0f172a; font-size: 20px; margin: 0 0 12px;">Verify your new email</h2>
            <p style="color: #64748b; font-size: 15px; line-height: 1.6; margin: 0 0 24px;">
              You requested to change your email address. Click the button below to verify this new email.
            </p>
            <a href="${verifyUrl}" style="display: inline-block; background: linear-gradient(135deg, #10b981, #06b6d4); color: white; text-decoration: none; padding: 14px 32px; border-radius: 12px; font-weight: 600; font-size: 15px;">
              Verify New Email
            </a>
            <p style="color: #94a3b8; font-size: 13px; margin: 24px 0 0; line-height: 1.5;">
              This link expires in 24 hours. If you didn't request this change, you can safely ignore this email.
            </p>
          </div>
          <p style="color: #cbd5e1; font-size: 12px; text-align: center; margin-top: 24px;">
            &copy; ${new Date().getFullYear()} Nexal. All rights reserved.
          </p>
        </div>
      `,
    });
    return { success: true };
  } catch (error) {
    console.error('Failed to send email change verification:', error);
    return { success: false, error };
  }
}
