import { serve } from "https://deno.land/std@0.208.0/http/server.ts";

const PAGE = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Delete your Nexal account — Alex Industries</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
      background: #0E1512;
      color: #e5e7eb;
      line-height: 1.7;
      padding: 24px;
    }
    .container { max-width: 720px; margin: 0 auto; padding: 40px 0; }
    h1 { font-size: 32px; font-weight: 700; color: #fff; margin-bottom: 8px; }
    .sub { color: #10B981; font-weight: 600; margin-bottom: 32px; }
    h2 { font-size: 20px; font-weight: 600; color: #10B981; margin: 28px 0 12px; }
    p, li { font-size: 15px; color: #d1d5db; margin-bottom: 12px; }
    ul { padding-left: 24px; margin-bottom: 16px; }
    a { color: #10B981; }
    .card {
      background: #161A21;
      border-radius: 18px;
      padding: 20px;
      margin: 20px 0;
    }
    label { display: block; font-size: 13px; font-weight: 600; margin: 12px 0 6px; }
    input, textarea {
      width: 100%;
      background: #0E1512;
      border: 1px solid #3A4454;
      border-radius: 12px;
      color: #fff;
      padding: 12px 14px;
      font-size: 15px;
    }
    button, .btn {
      display: inline-block;
      margin-top: 16px;
      background: #059669;
      color: #fff;
      border: 0;
      border-radius: 14px;
      padding: 14px 20px;
      font-weight: 700;
      font-size: 15px;
      cursor: pointer;
      text-decoration: none;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Delete your Nexal account</h1>
    <p class="sub">Nexal by Alex Industries</p>
    <p>Use this page to request deletion of your <strong>Nexal</strong> account and the personal data associated with it. Nexal is the AI workout, meal plan, and calorie tracker app published on Google Play by <strong>Alex Industries</strong>.</p>
    <h2>What we delete</h2>
    <ul>
      <li>Your Nexal account (email / Google Sign-In)</li>
      <li>Profile and health data (weight, goals, preferences)</li>
      <li>Workout logs, meal plans, food diary, and progress history</li>
    </ul>
    <p>Deletion is completed within 30 days. Google Play subscriptions must be cancelled separately in the Play Store.</p>
    <div class="card">
      <h2 style="margin-top:0">Request deletion</h2>
      <p>Email us from the address on your Nexal account. We will confirm and delete the account.</p>
      <form action="mailto:support@nexalapp.com?subject=Nexal%20account%20deletion%20request%20%E2%80%94%20Alex%20Industries" method="post" enctype="text/plain">
        <label for="email">Nexal account email</label>
        <input id="email" name="Email" type="email" required placeholder="you@example.com" />
        <label for="notes">Optional notes</label>
        <textarea id="notes" name="Notes" rows="3" placeholder="Anything we should know"></textarea>
        <button type="submit">Email deletion request</button>
      </form>
      <p style="margin-top:16px">Or write directly to <a href="mailto:support@nexalapp.com?subject=Nexal%20account%20deletion%20request">support@nexalapp.com</a> with the subject <strong>Nexal account deletion request</strong>.</p>
    </div>
    <p style="color:#6b7280;font-size:13px;text-align:center;margin-top:40px;">© 2026 Nexal · Alex Industries</p>
  </div>
</body>
</html>`;

serve((_req) =>
  new Response(PAGE, {
    headers: { "Content-Type": "text/html; charset=utf-8" },
  })
);
