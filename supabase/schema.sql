-- =====================================================================
-- TuGymBro — Esquema de base de datos (Supabase / PostgreSQL)
-- =====================================================================
-- Cómo usarlo: Dashboard de Supabase > SQL Editor > pegar todo este
-- archivo > Run. Se puede correr una sola vez; volver a correrlo no
-- duplica nada porque usa "if not exists" / "or replace" donde aplica.
--
-- Importante antes de compilar la app:
-- Authentication > Settings > habilitar "Allow anonymous sign-ins".
-- La app usa una sesión anónima por instalación para poder identificar
-- "de quién es" cada fila sin tener todavía una pantalla de login.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tabla: users (perfil de cada persona)
-- ---------------------------------------------------------------------
create table if not exists public.users (
  id text primary key,                 -- coincide con auth.uid() (sesión anónima o futura cuenta real)
  name text not null default '',
  age int4 not null default 0,
  bio text not null default '',
  gym_name text not null default '',
  training_types text not null default '',   -- lista separada por comas, ej: "Fuerza,Cardio"
  interaction_level text not null default 'SPOTTER', -- SILENCIOSO | SPOTTER | CHARLA | FULL_TRAINING
  distance_meters int4 not null default 0,   -- TODO: calcular con geolocalización real (ver doc técnico 4.3)
  photo_url text,
  created_at timestamptz not null default now()
);

alter table public.users enable row level security;

drop policy if exists "cualquiera autenticado puede ver perfiles" on public.users;
create policy "cualquiera autenticado puede ver perfiles"
on public.users for select
to authenticated, anon
using (true);

drop policy if exists "cada usuario crea su propio perfil" on public.users;
create policy "cada usuario crea su propio perfil"
on public.users for insert
to authenticated
with check (auth.uid()::text = id);

drop policy if exists "cada usuario edita su propio perfil" on public.users;
create policy "cada usuario edita su propio perfil"
on public.users for update
to authenticated
using (auth.uid()::text = id)
with check (auth.uid()::text = id);

-- ---------------------------------------------------------------------
-- Tabla: match_requests (solicitudes de conexión, doble opt-in)
-- ---------------------------------------------------------------------
create table if not exists public.match_requests (
  id uuid primary key default gen_random_uuid(),
  from_user_id text not null references public.users(id) on delete cascade,
  to_user_id text not null references public.users(id) on delete cascade,
  status text not null default 'pendiente', -- pendiente | ACEPTADO | RECHAZADO
  created_at timestamptz not null default now()
);

alter table public.match_requests enable row level security;

drop policy if exists "ver solo mis solicitudes" on public.match_requests;
create policy "ver solo mis solicitudes"
on public.match_requests for select
to authenticated
using (auth.uid()::text = from_user_id or auth.uid()::text = to_user_id);

drop policy if exists "crear solicitud como yo mismo" on public.match_requests;
create policy "crear solicitud como yo mismo"
on public.match_requests for insert
to authenticated
with check (auth.uid()::text = from_user_id);

drop policy if exists "responder solicitudes que me llegaron" on public.match_requests;
create policy "responder solicitudes que me llegaron"
on public.match_requests for update
to authenticated
using (auth.uid()::text = to_user_id)
with check (auth.uid()::text = to_user_id);

-- ---------------------------------------------------------------------
-- Tabla: matches (conexión ya aceptada entre dos personas)
-- ---------------------------------------------------------------------
create table if not exists public.matches (
  id uuid primary key default gen_random_uuid(),
  user_a text not null references public.users(id) on delete cascade,
  user_b text not null references public.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

alter table public.matches enable row level security;

drop policy if exists "ver solo mis matches" on public.matches;
create policy "ver solo mis matches"
on public.matches for select
to authenticated
using (auth.uid()::text = user_a or auth.uid()::text = user_b);

-- ---------------------------------------------------------------------
-- Tabla: messages (chat 1 a 1, ligado a un match)
-- ---------------------------------------------------------------------
create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  match_id text not null,
  sender_id text not null references public.users(id) on delete cascade,
  text text not null,
  created_at timestamptz not null default now()
);

alter table public.messages enable row level security;

-- Nota MVP: esta política es permisiva (cualquier autenticado puede leer
-- y escribir mensajes) porque todavía no hay una tabla que relacione
-- match_id con sus dos participantes de forma consultable desde acá.
-- Antes de publicar en Play Store, hay que endurecerla para que solo
-- puedan leer/escribir los dos usuarios de ese match específico
-- (ver documento técnico, sección 5).
drop policy if exists "MVP: autenticados pueden leer mensajes" on public.messages;
create policy "MVP: autenticados pueden leer mensajes"
on public.messages for select
to authenticated
using (true);

drop policy if exists "solo puedo enviar mensajes como yo mismo" on public.messages;
create policy "solo puedo enviar mensajes como yo mismo"
on public.messages for insert
to authenticated
with check (auth.uid()::text = sender_id);

-- ---------------------------------------------------------------------
-- Tabla: reports (reporte de usuarios — requisito de Play Store para UGC)
-- ---------------------------------------------------------------------
create table if not exists public.reports (
  id uuid primary key default gen_random_uuid(),
  reporter_id text not null references public.users(id) on delete cascade,
  reported_id text not null references public.users(id) on delete cascade,
  reason text not null,
  status text not null default 'pendiente',
  created_at timestamptz not null default now()
);

alter table public.reports enable row level security;

drop policy if exists "puedo crear reportes como yo mismo" on public.reports;
create policy "puedo crear reportes como yo mismo"
on public.reports for insert
to authenticated
with check (auth.uid()::text = reporter_id);

drop policy if exists "veo solo mis propios reportes" on public.reports;
create policy "veo solo mis propios reportes"
on public.reports for select
to authenticated
using (auth.uid()::text = reporter_id);

-- ---------------------------------------------------------------------
-- Tabla: blocks (bloqueo de usuarios)
-- ---------------------------------------------------------------------
create table if not exists public.blocks (
  id uuid primary key default gen_random_uuid(),
  blocker_id text not null references public.users(id) on delete cascade,
  blocked_id text not null references public.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

alter table public.blocks enable row level security;

drop policy if exists "manejo solo mis propios bloqueos" on public.blocks;
create policy "manejo solo mis propios bloqueos"
on public.blocks for all
to authenticated
using (auth.uid()::text = blocker_id)
with check (auth.uid()::text = blocker_id);

-- =====================================================================
-- Datos de ejemplo opcionales, para ver algo apenas conectás la app.
-- Se puede borrar esta sección sin afectar el esquema.
-- =====================================================================
insert into public.users (id, name, age, bio, gym_name, training_types, interaction_level, distance_meters)
values
  ('demo-fede', 'Fede', 27, 'Entreno de 19 a 21h en SportClub Once. Busco alguien para pierna, sin drama de charla.', 'SportClub Once', 'Fuerza', 'SPOTTER', 450),
  ('demo-cami', 'Cami', 24, 'Voy temprano, antes del laburo. Prefiero entrenar en silencio con música.', 'SportClub Once', 'Funcional,Cardio', 'SILENCIOSO', 300),
  ('demo-nico', 'Nico', 31, 'Rutina full body, tardes. Buena onda para charlar entre series.', 'Iron House', 'Fuerza,Hipertrofia', 'CHARLA', 900),
  ('demo-ari', 'Ari', 29, 'Busco compañero fijo para armar rutina de 3 meses juntos.', 'Iron House', 'Powerlifting', 'FULL_TRAINING', 1200)
on conflict (id) do nothing;
