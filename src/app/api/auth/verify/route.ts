import { NextRequest, NextResponse } from 'next/server';
import prisma from '@/lib/prisma';

export const dynamic = 'force-dynamic';

function renderPage(title: string, heading: string, message: string, success: boolean) {
  const color = success ? '#10b981' : '#ef4444';
  const icon = success
    ? '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9 12l2 2 4-4"/></svg>'
    : '<svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>';

  return new NextResponse(
    `<!DOCTYPE html>
    <html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <title>${title} — FitMate</title>
    <style>
      *{margin:0;padding:0;box-sizing:border-box}
      body{font-family:system-ui,-apple-system,sans-serif;min-height:100vh;display:flex;align-items:center;justify-content:center;background:#f8fafc;padding:24px}
      .card{background:#fff;border-radius:20px;padding:48px 32px;max-width:400px;width:100%;text-align:center;box-shadow:0 4px 24px rgba(0,0,0,.06);border:1px solid #e2e8f0}
      .icon{margin-bottom:24px}
      h1{font-size:22px;color:#0f172a;margin-bottom:8px}
      p{font-size:15px;color:#64748b;line-height:1.6;margin-bottom:24px}
      .badge{display:inline-block;padding:8px 20px;border-radius:10px;background:${color}12;color:${color};font-weight:600;font-size:14px}
      .brand{margin-top:32px;font-size:13px;color:#cbd5e1}
    </style></head>
    <body><div class="card">
      <div class="icon">${icon}</div>
      <h1>${heading}</h1>
      <p>${message}</p>
      <span class="badge">${success ? '✓ Verified' : '✗ Failed'}</span>
      <p class="brand">FitMate</p>
    </div></body></html>`,
    { status: 200, headers: { 'Content-Type': 'text/html; charset=utf-8' } }
  );
}

export async function GET(request: NextRequest) {
  try {
    const token = request.nextUrl.searchParams.get('token');

    if (!token) {
      return renderPage('Invalid Link', 'Invalid Verification Link', 'The verification link is invalid or missing. Please try registering again.', false);
    }

    // Find the verification token
    const verificationToken = await prisma.verificationToken.findUnique({
      where: { token },
    });

    if (!verificationToken) {
      return renderPage('Invalid Token', 'Link Already Used or Invalid', 'This verification link has already been used or is invalid. You can log in to your account if already verified.', false);
    }

    // Check if expired
    if (new Date() > verificationToken.expires) {
      await prisma.verificationToken.delete({
        where: { token },
      });
      return renderPage('Link Expired', 'Verification Link Expired', 'This link has expired. Please register again to receive a new verification email.', false);
    }

    // Mark user email as verified
    await prisma.user.update({
      where: { email: verificationToken.identifier },
      data: { emailVerified: new Date() },
    });

    // Delete the used token
    await prisma.verificationToken.delete({
      where: { token },
    });

    return renderPage('Email Verified', 'Email Verified Successfully!', 'Your email has been verified. You can now open the FitMate app and sign in to your account.', true);
  } catch (error) {
    console.error('Email verification error:', error);
    return renderPage('Error', 'Verification Failed', 'Something went wrong verifying your email. Please try again or contact support.', false);
  }
}
