import { serve } from "https://deno.land/std@0.208.0/http/server.ts";

serve((_req) => {
  const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Email Verified - Nexal</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: #0a0a0a;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 24px;
    }
    .container {
      text-align: center;
      max-width: 400px;
    }
    .check-circle {
      width: 96px;
      height: 96px;
      border-radius: 50%;
      background: linear-gradient(135deg, #10B981, #06B6D4);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 32px;
    }
    .check-circle svg {
      width: 48px;
      height: 48px;
      fill: none;
      stroke: #fff;
      stroke-width: 3;
      stroke-linecap: round;
      stroke-linejoin: round;
    }
    h1 {
      font-size: 28px;
      font-weight: 700;
      margin-bottom: 12px;
    }
    p {
      color: #9ca3af;
      font-size: 16px;
      line-height: 1.5;
      margin-bottom: 32px;
    }
    .btn {
      display: inline-block;
      padding: 14px 40px;
      background: linear-gradient(135deg, #10B981, #06B6D4);
      color: #fff;
      text-decoration: none;
      border-radius: 12px;
      font-size: 16px;
      font-weight: 600;
      transition: opacity 0.2s;
    }
    .btn:hover { opacity: 0.9; }
  </style>
</head>
<body>
  <div class="container">
    <div class="check-circle">
      <svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"></polyline></svg>
    </div>
    <h1>Email Verified</h1>
    <p>Your Nexal account has been confirmed. Go back to the app and sign in.</p>
    <a href="nexal://app/login" class="btn">Back to Sign In</a>
  </div>
</body>
</html>`;

  return new Response(html, {
    headers: { "Content-Type": "text/html; charset=utf-8" },
  });
});
