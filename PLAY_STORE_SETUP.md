# Play Store Subscription & Supabase Setup Guide

This guide walks you through setting up Google Play subscriptions, Supabase backend, and connecting everything together.

---

## Part 1: Google Play Console — Subscription Setup

### 1.1 Create the Subscription Product

1. Open [Google Play Console](https://play.google.com/console/)
2. Select your app **Nexal** (`com.nexal.app`)
3. Navigate to **Monetize → Products → Subscriptions**
4. Click **Create subscription**
5. Fill in:
   - **Product ID**: `nexal_premium` (this is referenced in `BillingRepository.kt`)
   - **Name**: Nexal Premium
   - **Description**: Unlock AI workout plans, meal plans, barcode scanner, progress analytics, and smart food substitutions.
6. Click **Create**

### 1.2 Add Base Plans

#### Monthly Plan
1. Inside the subscription you just created, click **Add base plan**
2. Fill in:
   - **Base plan ID**: `monthly-autorenewing`
   - **Auto-renewing**: Yes
   - **Billing period**: 1 Month
   - **Price**: Set to **$12.99 USD** (and equivalent in other currencies using "Auto-convert")
3. Click **Set price** → **Update**
4. **Activate** the base plan

#### Yearly Plan
1. Click **Add base plan** again
2. Fill in:
   - **Base plan ID**: `yearly-autorenewing`
   - **Auto-renewing**: Yes
   - **Billing period**: 1 Year
   - **Price**: Set to **$110.00 USD** (and equivalent in other currencies using "Auto-convert")
3. Click **Set price** → **Update**
4. **Activate** the base plan

### 1.3 Add a Free Trial Offer (Optional but Recommended)

1. Inside the base plan, click **Add offer**
2. Select **Free trial**
3. Fill in:
   - **Offer ID**: `free-trial-14d`
   - **Eligibility**: New customers only (Never purchased this subscription)
   - **Free trial duration**: 14 days
4. **Activate** the offer

### 1.4 Set Grace Period & Account Hold

1. Go to **Monetize → Monetization setup**
2. Under **Subscription settings**:
   - **Grace period**: Enable, 7 days (gives users time to fix payment issues)
   - **Account hold**: Enable, 30 days (subscription paused while payment is resolved)
   - **Resubscribe**: Enable (allow users to resubscribe from Play Store)

---

## Part 2: Google Cloud — Service Account for Server Verification

Your Supabase Edge Function needs to verify purchase tokens with the Google Play Developer API. This requires a service account.

### 2.1 Create a Service Account

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select the project linked to your Play Console (or create one)
3. Navigate to **IAM & Admin → Service Accounts**
4. Click **Create Service Account**
   - **Name**: `nexal-play-billing`
   - **Description**: Verifies Play Store subscription purchases
5. Click **Create and Continue**
6. Skip the role assignment on this screen (we'll grant access via Play Console)
7. Click **Done**

### 2.2 Create a Key

1. Click on the service account you just created
2. Go to the **Keys** tab
3. Click **Add Key → Create new key**
4. Select **JSON** → **Create**
5. Save the downloaded JSON file securely — you'll add it as a Supabase Edge Function secret

### 2.3 Enable the Android Publisher API

1. In Google Cloud Console, go to **APIs & Services → Library**
2. Search for **Google Play Android Developer API**
3. Click **Enable**

### 2.4 Grant Access in Play Console

1. Go to [Google Play Console](https://play.google.com/console/)
2. Navigate to **Settings → API access**
3. Click **Link** next to your Google Cloud project (if not already linked)
4. Under **Service accounts**, find `nexal-play-billing`
5. Click **Grant access**
6. Set permission: **View financial data, orders, and cancellation survey responses** and **Manage orders and subscriptions**
7. Under App permissions, add your app
8. Click **Invite user** → **Send invite**

> **Note**: It can take up to 24 hours for the service account to become active after granting access.

---

## Part 3: Real-Time Developer Notifications (RTDN)

RTDN sends subscription lifecycle events (renewals, cancellations, etc.) to your backend in real time via Google Cloud Pub/Sub.

### 3.1 Create a Pub/Sub Topic

1. Go to [Google Cloud Console → Pub/Sub](https://console.cloud.google.com/cloudpubsub)
2. Click **Create Topic**
   - **Topic ID**: `nexal-play-billing-events`
   - Uncheck "Add a default subscription"
3. Click **Create**

### 3.2 Grant Google Play Permission to Publish

1. Click on the topic you created
2. Go to the **Permissions** tab
3. Click **Grant Access**
4. **New principal**: `google-play-developer-notifications@system.gserviceaccount.com`
5. **Role**: Pub/Sub Publisher
6. Click **Save**

### 3.3 Create a Push Subscription

1. In the topic details, click **Create Subscription**
2. Fill in:
   - **Subscription ID**: `nexal-rtdn-push`
   - **Delivery type**: Push
   - **Endpoint URL**: `https://<YOUR_SUPABASE_PROJECT_REF>.supabase.co/functions/v1/rtdn-webhook`
   - **Enable authentication**: No (we verify using the Pub/Sub message structure)
3. Click **Create**

### 3.4 Configure RTDN in Play Console

1. Go to **Play Console → Settings → API access**
2. Scroll to **Real-time developer notifications**
3. Set **Topic name**: `projects/<YOUR_GCP_PROJECT_ID>/topics/nexal-play-billing-events`
4. Click **Send test notification** to verify the connection ✓
5. Click **Save**

---

## Part 4: Supabase Project Setup

### 4.1 Create Project

1. Go to [supabase.com](https://supabase.com) → **New Project**
2. Choose an organization, name it **nexal**
3. Set a strong database password (save it!)
4. Choose the region closest to your users
5. Click **Create new project**

### 4.2 Get Your Keys

After project creation, go to **Settings → API**:
- **Project URL**: `https://<ref>.supabase.co` → this is `SUPABASE_URL`
- **anon public key**: → this is `SUPABASE_ANON_KEY`
- **service_role secret key**: → this is for Edge Functions only (never in client code!)

### 4.3 Set Up Authentication

1. Go to **Authentication → Providers**
2. **Email**: Already enabled by default. Configure:
   - **Confirm email**: Enable (sends verification email on sign-up)
   - **Secure email change**: Enable
3. **Google**: Click to enable, fill in:
   - **Client ID**: Your Google OAuth web client ID (same one used for Google Sign-In)
   - **Client Secret**: Your Google OAuth client secret
4. Go to **Authentication → Email Templates** to customize email templates if desired

### 4.4 Create Database Tables

Go to **SQL Editor** and run the migration SQL from `supabase/migrations/001_initial_schema.sql` (included in this project).

### 4.5 Deploy Edge Functions

Install the Supabase CLI and deploy:

```bash
# Install Supabase CLI
npm install -g supabase

# Login
supabase login

# Link to your project
supabase link --project-ref <your-project-ref>

# Set secrets
supabase secrets set GEMINI_API_KEY=your_gemini_api_key
supabase secrets set GOOGLE_SERVICE_ACCOUNT_JSON='<contents of the JSON key file>'

# Deploy all functions
supabase functions deploy ai-workout
supabase functions deploy ai-meal
supabase functions deploy ai-assess
supabase functions deploy ai-food-lookup
supabase functions deploy ai-meal-substitute
supabase functions deploy ai-exercise-suggestions
supabase functions deploy nutrition-lookup
supabase functions deploy verify-purchase
supabase functions deploy rtdn-webhook
```

---

## Part 5: License Testing

### 5.1 Add Test Accounts

1. Go to **Play Console → Settings → License testing**
2. Add Gmail addresses of your test devices
3. Set **License response** to `RESPOND_NORMALLY`

### 5.2 Test the Flow

1. Install a **debug** or **internal testing** build on a device signed in with a test account
2. Navigate to the paywall → tap **Subscribe**
3. Google Play payment sheet should appear with a **test card** option
4. Complete the purchase
5. Verify:
   - `BillingRepository` receives the purchase callback
   - The app calls `verify-purchase` Edge Function
   - `user_subscriptions` table in Supabase shows `status = 'active'`
   - User gains access to premium features

### 5.3 Test Subscription Events

- **Cancel**: Go to Play Store → Subscriptions → Cancel Nexal
- **Renew**: Test subscriptions renew at an accelerated rate (monthly = ~5 minutes)
- Verify RTDN webhook updates the subscription status in Supabase

---

## Part 6: Android Configuration

### 6.1 Update `local.properties`

```properties
# Supabase
SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_ANON_KEY=eyJ...your_anon_key

# Google Sign-In
GOOGLE_WEB_CLIENT_ID=your_google_web_client_id

# Signing (keep existing)
RELEASE_STORE_FILE=C:/Users/alexa/nexal-release.jks
RELEASE_STORE_PASSWORD=nexal2026
RELEASE_KEY_ALIAS=nexal
RELEASE_KEY_PASSWORD=nexal2026
```

### 6.2 Subscription Product ID

The product ID `nexal_premium` is referenced in `BillingRepository.kt`. If you used a different product ID in Part 1, update it there.

---

## Checklist

- [ ] Subscription product created in Play Console with base plans ($12.99/mo + $110/yr)
- [ ] Free trial offer created (14 days)
- [ ] Grace period and account hold enabled
- [ ] Google Cloud service account created with JSON key
- [ ] Android Publisher API enabled
- [ ] Service account granted access in Play Console
- [ ] Pub/Sub topic created with Google Play publish permission
- [ ] Push subscription created pointing to Supabase Edge Function
- [ ] RTDN configured in Play Console
- [ ] Supabase project created
- [ ] Supabase Auth configured (Email + Google)
- [ ] Database tables created (run migration SQL)
- [ ] Edge Functions deployed with secrets set
- [ ] `local.properties` updated with Supabase keys
- [ ] License testing configured with test accounts
- [ ] End-to-end subscription flow tested
