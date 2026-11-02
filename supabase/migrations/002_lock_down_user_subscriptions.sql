-- ─── Lock down user_subscriptions ───────────────────────────────────────────
--
-- 001 created "Service role manages subscriptions" as `for all using (true)
-- with check (true)` without a `to` clause, so it applied to every role:
-- any signed-in user could insert or update their own row and set
-- status = 'active'. The service role bypasses RLS anyway, so the Edge
-- Functions don't need a policy at all. Users keep read access to their own
-- row via "Users read own subscription".

drop policy if exists "Service role manages subscriptions" on public.user_subscriptions;

-- Belt and braces: clients only ever read this table.
revoke insert, update, delete on public.user_subscriptions from anon, authenticated;

-- One Play purchase token may back only one account. verify-purchase checks
-- this too; the index closes the race between two concurrent verifies.
-- If tokens are already shared between accounts the index can't be built, so
-- report them instead of failing the migration. Resolve them, then re-run.
do $$
begin
  if exists (
    select purchase_token from public.user_subscriptions
    where purchase_token is not null
    group by purchase_token having count(*) > 1
  ) then
    raise warning 'user_subscriptions has purchase tokens shared by several accounts; '
      'unique index user_subscriptions_purchase_token_key not created. Find them with: '
      'select purchase_token, array_agg(user_id) from public.user_subscriptions '
      'where purchase_token is not null group by 1 having count(*) > 1;';
  else
    create unique index if not exists user_subscriptions_purchase_token_key
      on public.user_subscriptions (purchase_token)
      where purchase_token is not null;
  end if;
end $$;
