# TuGymBro — Proyecto Android (v0.2.0 — conectado a Supabase)

App Android nativa en Kotlin + Jetpack Compose, siguiendo la propuesta técnica
y el diseño visual v3 ("Placa de fierro") ya aprobados. Esta versión está
conectada a un proyecto real de Supabase.

## Qué incluye esta versión

- Las 4 pantallas principales del diseño aprobado, navegables de punta a punta.
- Arquitectura MVVM + capas domain / data / presentation.
- Inyección de dependencias con Hilt.
- Design system propio (`ui/theme`) con la paleta y los componentes de marca.
- **Backend real conectado**: Supabase (Postgres + Auth anónima), con:
  - Perfil de usuario (`users`)
  - Descubrimiento de personas cerca (`getNearbyMatches`, hoy sin filtro
    geográfico real todavía — ver "Qué falta" abajo)
  - Solicitudes de match (`match_requests`)
  - Chat (`messages`) — funciona con backend real, pero sin push instantáneo
    todavía (ver nota de Realtime más abajo)
- `supabase/schema.sql` con las 6 tablas del documento técnico y Row Level
  Security (RLS) activado en todas, más 4 perfiles de ejemplo para probar
  apenas conectás.

## ⚠️ Antes de compilar: 2 pasos en el dashboard de Supabase

1. **Correr el esquema**: Dashboard → SQL Editor → pegar el contenido de
   `supabase/schema.sql` → Run.
2. **Habilitar sesiones anónimas**: Authentication → Settings → activar
   "Allow anonymous sign-ins". La app usa una sesión anónima por instalación
   para poder identificar de quién es cada fila (así funcionan las políticas
   de RLS) mientras no hay pantalla de login. Sin este paso, la app va a
   fallar al intentar crear o leer el perfil.

Las credenciales (`SUPABASE_URL` y `SUPABASE_ANON_KEY`) ya están cargadas en
`app/build.gradle.kts`.

### Nota de seguridad sobre la anon key

La anon key **está pensada para ir embebida en apps cliente** (móvil o web);
no es un secreto que haya que esconder como una contraseña de servidor. Lo
que protege los datos de verdad es Row Level Security (que ya está activado
en las 6 tablas de `schema.sql`), no que la key esté oculta. Aun así, dos
recomendaciones:

- Antes de publicar en Play Store, revisar cada política de `schema.sql` —
  la de `messages` quedó marcada como "MVP" (permisiva) porque todavía no
  existe una forma de saber desde la base qué dos usuarios pertenecen a cada
  match; hay que endurecerla antes de producción.
- Si en algún momento este proyecto de Supabase se usó también para otra
  cosa o se compartió por otro canal antes de tener RLS activado, conviene
  rotar la anon key desde el dashboard (Settings → API) para partir de cero.

## Qué falta para producción

- **Geolocalización real**: `distance_meters` hoy es un campo fijo en la
  tabla (0 para todos). Falta calcular la distancia real con
  `FusedLocationProviderClient` + la posición del otro usuario (la
  dependencia de ubicación ya está en `build.gradle.kts`).
- **Realtime en el chat**: hoy el chat trae los mensajes al entrar a la
  pantalla y después de cada envío (`refreshMessages`), no en push
  instantáneo. El siguiente paso es sumar el módulo `realtime-kt` de
  Supabase y suscribirse a cambios de la tabla `messages` filtrados por
  `match_id`.
- **Reporte y bloqueo desde la UI**: las tablas `reports` y `blocks` ya
  existen en el esquema, pero todavía no hay pantalla para usarlas
  (obligatorio antes de publicar, ver documento técnico sección 5 y 6).
- **Política de `messages` más estricta** (ver nota de seguridad arriba).
- **Fuentes de marca reales** (Allerta Stencil / Barlow Condensed) — ver
  nota en `ui/theme/Type.kt`.
- **Login real** (hoy es sesión anónima por instalación).

### Aclaración honesta sobre este código

No tengo forma de compilar proyectos Android en el entorno donde armo estos
archivos (no hay SDK de Android ni acceso a los repositorios de Google desde
acá), así que no pude correr un build real de punta a punta contra el SDK de
Supabase. Revisé la sintaxis contra la documentación oficial más reciente del
SDK de Kotlin, pero es posible que al sincronizar en Android Studio aparezca
algún ajuste menor de API (nombres de funciones del DSL de `update`/`select`,
por ejemplo). Si te tira un error de compilación puntual, pegámelo acá y lo
corregimos.

## Cómo abrir el proyecto

1. Instalar [Android Studio](https://developer.android.com/studio) (versión
   reciente, con soporte para Kotlin 2.0 y Compose).
2. Abrir la carpeta `TuGymBro/` completa con **File > Open**.
3. Al abrirlo por primera vez, Android Studio va a ofrecer generar el Gradle
   Wrapper automáticamente si falta algún archivo binario del wrapper — aceptar
   esa opción (o instalar Gradle 8.9 localmente y correr `gradle wrapper`).
4. Dejar que sincronice las dependencias (necesita conexión a internet).
5. Correr los 2 pasos de Supabase de la sección de arriba.
6. Correr en un emulador (API 26 o superior) o en un celular con
   depuración USB habilitada.

## Probarla en tu celular sin instalar Android Studio

La forma más simple es dejar que **GitHub compile el APK por vos**, gratis,
y después descargarlo al celular. Paso a paso:

1. **Crear una cuenta gratis en [github.com](https://github.com)** (si no
   tenés una).
2. Crear un repositorio nuevo (botón verde "New"). Puede ser privado o
   público — la anon key de Supabase está pensada para estar embebida en
   apps cliente, así que no hay problema de seguridad en que quede en el
   código (ver la nota de seguridad más abajo si igual preferís privado).
3. Subir el contenido de esta carpeta `TuGymBro/` a ese repositorio. La
   forma más fácil sin usar la terminal: en la página del repo vacío, click
   en "uploading an existing file" y arrastrar todos los archivos y carpetas
   (incluyendo la carpeta oculta `.github/`, que es la que hace la magia).
4. Ir a la pestaña **Actions** del repositorio. Va a aparecer un flujo
   llamado "Compilar APK de TuGymBro" corriendo automáticamente (tarda
   unos 3-5 minutos la primera vez).
5. Cuando termine (ícono verde ✓), entrar a esa ejecución y bajar a la
   sección **Artifacts**: ahí va a estar `TuGymBro-debug` para descargar
   (es un .zip que adentro tiene el `app-debug.apk`).
6. Pasar ese `.apk` al celular (por WhatsApp a vos mismo, Google Drive,
   cable USB, lo que te resulte más cómodo).
7. En el celular, abrir el archivo `.apk` desde el explorador de archivos.
   Android va a pedir permiso para "instalar apps de fuentes desconocidas"
   la primera vez — aceptar solo para esa app/origen.
8. Listo, se instala como cualquier app y ya podés abrirla.

Cada vez que subas un cambio de código al repositorio, este proceso se
repite solo y te genera un APK nuevo en la pestaña Actions.

### Alternativa sin GitHub

Si preferís no usar GitHub: cualquier persona que tenga Android Studio
instalado puede abrir esta misma carpeta, conectar tu celular por USB con la
depuración habilitada, y darle "Run". No hace falta que seas vos quien tenga
Android Studio instalado, solo alguien que compile una vez y te pase el
`.apk` generado en `app/build/outputs/apk/debug/`.



Si en algún momento querés probar la UI sin depender de la conexión a
Supabase (por ejemplo, sin internet), cambiá los 4 `@Binds` de
`di/AppModule.kt` de `Supabase*Repository` a `Mock*Repository`
(`data/repository/MockRepositories.kt`). Ningún ViewModel ni pantalla
necesita tocarse en ese cambio.

## Estructura de carpetas

```
app/src/main/java/com/tugymbro/app/
├── ui/theme/          -> paleta, tipografía, componentes de marca
├── domain/            -> modelos, interfaces de repositorio, casos de uso
├── data/
│   ├── remote/        -> cliente de Supabase + DTOs de las tablas
│   └── repository/    -> SupabaseRepositories.kt (real) y MockRepositories.kt (offline)
├── di/                -> módulo de Hilt
├── navigation/        -> grafo de navegación (Navigation Compose)
└── feature/
    ├── onboarding/
    ├── home/
    ├── match/
    └── chat/
supabase/
└── schema.sql         -> tablas + RLS + datos de ejemplo
.github/workflows/
└── build-apk.yml      -> compila el APK automáticamente en GitHub (sin Android Studio)
```

## Próximo paso sugerido

Geolocalización real y Realtime en el chat son los dos pendientes con más
impacto en que la app se sienta "viva". Después de eso: pantalla de
reporte/bloqueo (obligatoria para Play Store) y las fuentes de marca reales.

