-- Migration: New competitive features
-- Gamification, Social, AI Chat, Photo Food Logging, Grocery Lists, Progressive Overload

-- ─── Streaks & Gamification ──────────────────────────────────────────────────

create table public.user_streaks (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  current_streak int not null default 0,
  longest_streak int not null default 0,
  last_activity_date text,
  total_xp int not null default 0,
  level int not null default 1,
  updated_at timestamptz not null default now(),
  constraint user_streaks_user_id_key unique (user_id)
);
create table public.user_badges (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  badge_id text not null,
  badge_name text not null,
  badge_description text not null default '',
  badge_icon text not null default 'star',
  earned_at timestamptz not null default now(),
  constraint user_badges_unique unique (user_id, badge_id)
);
create table public.personal_records (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  exercise_name text not null,
  record_type text not null default 'weight', -- weight, reps, volume
  value real not null,
  previous_value real,
  achieved_at text not null,
  created_at timestamptz not null default now()
);
-- ─── Social / Accountability ─────────────────────────────────────────────────

create table public.accountability_partners (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  partner_id uuid not null references auth.users(id) on delete cascade,
  status text not null default 'pending', -- pending, accepted, declined
  created_at timestamptz not null default now(),
  constraint accountability_unique unique (user_id, partner_id)
);
create table public.challenges (
  id text primary key default gen_random_uuid()::text,
  creator_id uuid not null references auth.users(id) on delete cascade,
  title text not null,
  description text not null default '',
  challenge_type text not null default 'volume', -- volume, workouts, streak
  target_value real not null default 0,
  start_date text not null,
  end_date text not null,
  created_at timestamptz not null default now()
);
create table public.challenge_participants (
  id text primary key default gen_random_uuid()::text,
  challenge_id text not null references public.challenges(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  current_value real not null default 0,
  joined_at timestamptz not null default now(),
  constraint challenge_participants_unique unique (challenge_id, user_id)
);
-- ─── AI Chat Messages ────────────────────────────────────────────────────────

create table public.ai_chat_messages (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  role text not null, -- user, assistant
  content text not null,
  created_at timestamptz not null default now()
);
-- ─── Photo Food Logs ─────────────────────────────────────────────────────────

-- Extend food_log_entries with photo support (nullable column)
alter table public.food_log_entries add column if not exists photo_url text;
-- ─── Grocery Lists ───────────────────────────────────────────────────────────

create table public.grocery_lists (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  meal_plan_id text references public.meal_plans(id) on delete set null,
  title text not null default 'Grocery List',
  items jsonb not null default '[]'::jsonb,
  created_at timestamptz not null default now()
);
-- ─── Supplement Tracking ─────────────────────────────────────────────────────

create table public.supplement_logs (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  name text not null,
  dosage text not null default '',
  time_of_day text not null default 'morning', -- morning, afternoon, evening, night
  taken boolean not null default false,
  date text not null,
  created_at timestamptz not null default now()
);
-- ─── Progress Photos ─────────────────────────────────────────────────────────

create table public.progress_photos (
  id text primary key default gen_random_uuid()::text,
  user_id uuid not null references auth.users(id) on delete cascade,
  photo_url text not null,
  pose text not null default 'front', -- front, side, back
  date text not null,
  notes text,
  created_at timestamptz not null default now()
);
-- ─── Indexes ─────────────────────────────────────────────────────────────────

create index idx_user_streaks_user on public.user_streaks(user_id);
create index idx_user_badges_user on public.user_badges(user_id);
create index idx_personal_records_user on public.personal_records(user_id);
create index idx_personal_records_exercise on public.personal_records(user_id, exercise_name);
create index idx_accountability_user on public.accountability_partners(user_id);
create index idx_accountability_partner on public.accountability_partners(partner_id);
create index idx_challenges_creator on public.challenges(creator_id);
create index idx_challenge_participants_challenge on public.challenge_participants(challenge_id);
create index idx_ai_chat_user on public.ai_chat_messages(user_id);
create index idx_grocery_lists_user on public.grocery_lists(user_id);
create index idx_supplement_logs_user_date on public.supplement_logs(user_id, date);
create index idx_progress_photos_user on public.progress_photos(user_id);
-- ─── Row Level Security ──────────────────────────────────────────────────────

alter table public.user_streaks enable row level security;
alter table public.user_badges enable row level security;
alter table public.personal_records enable row level security;
alter table public.accountability_partners enable row level security;
alter table public.challenges enable row level security;
alter table public.challenge_participants enable row level security;
alter table public.ai_chat_messages enable row level security;
alter table public.grocery_lists enable row level security;
alter table public.supplement_logs enable row level security;
alter table public.progress_photos enable row level security;
create policy "Users manage own data" on public.user_streaks
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.user_badges
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.personal_records
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own partners" on public.accountability_partners
  for all using (auth.uid() = user_id or auth.uid() = partner_id)
  with check (auth.uid() = user_id);
create policy "Users read challenges they participate in" on public.challenges
  for select using (
    auth.uid() = creator_id or
    exists (select 1 from public.challenge_participants cp where cp.challenge_id = id and cp.user_id = auth.uid())
  );
create policy "Users create own challenges" on public.challenges
  for insert with check (auth.uid() = creator_id);
create policy "Users manage own participation" on public.challenge_participants
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.ai_chat_messages
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.grocery_lists
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.supplement_logs
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users manage own data" on public.progress_photos
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
