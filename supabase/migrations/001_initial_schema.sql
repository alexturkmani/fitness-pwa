-- Supabase Migration: Initial Schema
-- Run this in Supabase SQL Editor or via `supabase db push`

-- ─── User Profiles ───────────────────────────────────────────────────────────

create table public.user_profiles (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  name text not null default '',
  weight real not null default 0,
  height real not null default 0,
  age int not null default 0,
  gender text not null default 'male',
  activity_level text not null default 'moderately_active',
  fitness_goals jsonb not null default '[]'::jsonb,
  target_weight real not null default 0,
  interval_weeks int not null default 6,
  gym_days_per_week int not null default 5,
  workout_style text not null default 'muscle_group',
  lifting_experience text not null default 'beginner',
  training_location text not null default 'gym',
  unit_system text not null default 'metric',
  allergies jsonb not null default '[]'::jsonb,
  onboarding_done boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint user_profiles_user_id_key unique (user_id)
);

-- ─── Workout Plans ───────────────────────────────────────────────────────────

create table public.workout_plans (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  interval_number int not null default 1,
  start_date text not null,
  end_date text not null,
  weeks int not null default 6,
  days jsonb not null default '[]'::jsonb,
  ai_notes text,
  assessment_summary text,
  created_at timestamptz not null default now()
);

-- ─── Workout Logs ────────────────────────────────────────────────────────────

create table public.workout_logs (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  plan_id text,
  day_id text,
  day_label text,
  exercises jsonb not null default '[]'::jsonb,
  duration int,
  notes text,
  created_at timestamptz not null default now()
);

-- ─── Custom Workout Logs ─────────────────────────────────────────────────────

create table public.custom_workout_logs (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  name text not null,
  date text not null,
  exercises jsonb not null default '[]'::jsonb,
  duration int,
  notes text,
  created_at timestamptz not null default now()
);

-- ─── Meal Plans ──────────────────────────────────────────────────────────────

create table public.meal_plans (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  meals jsonb not null default '[]'::jsonb,
  daily_totals jsonb not null default '{}'::jsonb,
  daily_targets jsonb not null default '{}'::jsonb,
  daily_water_intake_ml int,
  ai_notes text,
  created_at timestamptz not null default now()
);

-- ─── Food Log Entries ────────────────────────────────────────────────────────

create table public.food_log_entries (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  food_name text not null,
  serving_size text not null default '1 serving',
  quantity int not null default 1,
  macros jsonb not null default '{}'::jsonb,
  source text not null default 'manual',
  barcode text,
  created_at timestamptz not null default now()
);

-- ─── Weight Entries ──────────────────────────────────────────────────────────

create table public.weight_entries (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  weight real not null,
  created_at timestamptz not null default now(),
  constraint weight_entries_user_date_key unique (user_id, date)
);

-- ─── Water Log Entries ───────────────────────────────────────────────────────

create table public.water_log_entries (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  amount int not null,
  created_at timestamptz not null default now()
);

-- ─── Cardio Log Entries ──────────────────────────────────────────────────────

create table public.cardio_log_entries (
  id text primary key,
  user_id uuid not null references auth.users(id) on delete cascade,
  date text not null,
  type text not null,
  duration_minutes int not null,
  estimated_calories_burnt int not null default 0,
  notes text,
  created_at timestamptz not null default now()
);

-- ─── User Subscriptions ──────────────────────────────────────────────────────

create table public.user_subscriptions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  product_id text not null,
  purchase_token text,
  status text not null default 'inactive',
  expiry_time timestamptz,
  auto_renewing boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint user_subscriptions_user_id_key unique (user_id)
);

-- ─── Indexes ─────────────────────────────────────────────────────────────────

create index idx_workout_plans_user on public.workout_plans(user_id);
create index idx_workout_logs_user on public.workout_logs(user_id);
create index idx_workout_logs_date on public.workout_logs(user_id, date);
create index idx_custom_workout_logs_user on public.custom_workout_logs(user_id);
create index idx_meal_plans_user on public.meal_plans(user_id);
create index idx_food_log_entries_user_date on public.food_log_entries(user_id, date);
create index idx_weight_entries_user on public.weight_entries(user_id);
create index idx_water_log_entries_user_date on public.water_log_entries(user_id, date);
create index idx_cardio_log_entries_user_date on public.cardio_log_entries(user_id, date);
create index idx_user_subscriptions_token on public.user_subscriptions(purchase_token);

-- ─── Row Level Security ──────────────────────────────────────────────────────

alter table public.user_profiles enable row level security;
alter table public.workout_plans enable row level security;
alter table public.workout_logs enable row level security;
alter table public.custom_workout_logs enable row level security;
alter table public.meal_plans enable row level security;
alter table public.food_log_entries enable row level security;
alter table public.weight_entries enable row level security;
alter table public.water_log_entries enable row level security;
alter table public.cardio_log_entries enable row level security;
alter table public.user_subscriptions enable row level security;

-- Users can read/write their own data
create policy "Users manage own data" on public.user_profiles
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.workout_plans
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.workout_logs
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.custom_workout_logs
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.meal_plans
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.food_log_entries
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.weight_entries
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.water_log_entries
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users manage own data" on public.cardio_log_entries
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy "Users read own subscription" on public.user_subscriptions
  for select using (auth.uid() = user_id);

-- Service role can manage subscriptions (used by Edge Functions)
create policy "Service role manages subscriptions" on public.user_subscriptions
  for all using (true) with check (true);

-- ─── Updated-at trigger ──────────────────────────────────────────────────────

create or replace function public.set_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

create trigger set_user_profiles_updated_at
  before update on public.user_profiles
  for each row execute function public.set_updated_at();

create trigger set_user_subscriptions_updated_at
  before update on public.user_subscriptions
  for each row execute function public.set_updated_at();
