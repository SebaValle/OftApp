# 👁️ OftApp — Plataforma Móvil Clínica de Gestión Oftalmológica

**OftApp** es una aplicación Android nativa desarrollada en **Kotlin** y **Jetpack Compose** orientada a la gestión, trazabilidad e interoperabilidad de exámenes oftalmológicos. Está diseñada bajo arquitectura **MVVM**, operación **Offline-First**, estándares internacionales de salud digital (**HL7 FHIR R4** y **LOINC**) y criterios de accesibilidad universal **WCAG 2.2 Nivel AA** para pacientes con baja visión.

---

## 🎯 Características Principales

### 1. Control de Acceso Basado en Roles (RBAC)
* **Técnico / Alumno:** Registro de nuevos exámenes oftalmológicos (OCT Macular, Campimetría, Retinografía, Fondo de Ojo, entre otros), asociación de paciente por RUT y carga de documentos/informes clínicos.
* **Médico Oftalmólogo:** Panel de métricas clínicas en tiempo real, revisión de exámenes pendientes, validación médica y acceso al historial clínico completo.
* **Paciente:** Portal personalizado que filtra y muestra exclusivamente los exámenes asociados a su RUT, con descarga/visualización de informes y herramientas de asistencia visual.

### 2. Interoperabilidad Clínica y Nube (APIs REST)
* **InsForge BaaS (PostgreSQL + REST):** Autenticación remota de usuarios con contraseñas protegidas mediante hash **SHA-256** y sincronización bidireccional de exámenes clínicos pendientes.
* **HAPI FHIR R4 (`https://hapi.fhir.org/baseR4/`):** Consulta e interoperabilidad con recursos clínicos estandarizados (`DiagnosticReport` y `Patient`).
* **NLM LOINC API (`https://clinicaltables.nlm.nih.gov/`):** Resolución y validación en tiempo real de terminología clínica internacional (ej. `LOINC 70936-0 - Visual acuity`).

### 3. Persistencia Local y Modo Offline-First
* **SQLite Nativo (`OftAppDbHelper`):** Almacenamiento local de exámenes clínicos para operar sin conexión a internet y sincronizar automáticamente al recuperar conectividad.
* **Jetpack DataStore (`AccesibilidadPreferences`):** Persistencia reactiva de preferencias visuales del usuario (escala de fuente y modo alto contraste).

### 4. Accesibilidad Universal (WCAG 2.2 AA)
* **Escalado Dinámico de Tipografía (`A+`):** Ajuste progresivo de tamaño de texto (`1.0x` a `1.5x`) adaptado a pacientes con baja visión.
* **Modo Alto Contraste:** Paleta de visibilidad reforzada con relación de contraste superior a 7:1.
* **Compatibilidad con Lectores de Pantalla:** Etiquetas semánticas (`contentDescription`) optimizadas para **TalkBack** y soporte de síntesis de voz (**TextToSpeech**).

---

## 🏗️ Arquitectura del Proyecto (MVVM)

```text
com.example.oftapp
├── data/
│   ├── local/
│   │   ├── entity/          # Entidades SQLite (ExamenEntity)
│   │   ├── AccesibilidadPreferences.kt
│   │   └── OftAppDbHelper.kt
│   ├── remote/
│   │   ├── HapiFhirApiService.kt
│   │   ├── InsForgeApiService.kt
│   │   ├── InsForgeModels.kt
│   │   ├── LoincApiService.kt
│   │   └── RetrofitClient.kt
│   └── repository/
│       └── ExamenRepository.kt
├── ui/
│   ├── components/          # Scaffold accesible y barras de navegación
│   ├── screens/             # Login, Dashboard, Formulario, Historial, Detalle, Portal Paciente
│   ├── theme/               # Paletas estándar y Alto Contraste WCAG 2.2 AA
│   └── viewmodel/           # ExamenViewModel + ExamenUiState (StateFlow)
└── util/
    └── SecurityUtils.kt     # Hashing criptográfico SHA-256
```

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose + Material Design 3
* **Asincronía:** Kotlin Coroutines + `StateFlow`
* **Red & Serialización:** Retrofit 2 + OkHttp Logging Interceptor + Gson
* **Persistencia:** SQLite (`SQLiteOpenHelper`) + Jetpack Preferences DataStore
* **Testing & Calidad:** JUnit 4, MockK, Kotlin Coroutines Test, Compose UI Test y JaCoCo Coverage

---

## 🧪 Pruebas Automatizadas y Cobertura (> 90%)

El proyecto cuenta con pruebas unitarias e instrumentadas que validan la lógica de negocio, seguridad, consumo de APIs y accesibilidad de interfaz:

* **`SecurityUtilsTest`:** Verifica el determinismo y longitud (64 caracteres hexadecimales) del cifrado SHA-256.
* **`ExamenRepositoryTest`:** Valida operaciones CRUD en SQLite, autenticación/registro en InsForge, sincronización offline y parseo de respuestas reales de **HAPI FHIR R4** y **NLM LOINC**.
* **`ExamenViewModelTest`:** Comprueba gestión de sesiones, validación de formularios, cálculo de métricas clínicas y persistencia de accesibilidad.
* **`OftAppAccesibilidadUiTest` (`androidTest`):** Valida componentes de Compose, controles WCAG (`A+`, TalkBack) y filtrado clínico en dispositivo/emulador.

### Ejecución de pruebas desde Android Studio
* **Pruebas Unitarias con Cobertura:** Clic derecho en `com.example.oftapp (test)` → *Run 'Tests in 'com.example.oftapp'' with Coverage*.
* **Pruebas Instrumentadas UI:** Clic derecho en `com.example.oftapp (androidTest)` → *Run 'Tests in 'com.example.oftapp''*.

---

## ⚙️ Configuración e Instalación Local

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/SebaValle/OftApp.git
   ```
2. Abrir el proyecto en **Android Studio** (`compileSdk 37`, `minSdk 24`).
3. Configurar las credenciales de InsForge en el archivo `local.properties` en la raíz del proyecto (este archivo está excluido de Git):
   ```properties
   INSFORGE_BASE_URL=https://tu-instancia.insforge.app/
   INSFORGE_ANON_KEY=tu_anon_key_aqui
   ```
4. Sincronizar Gradle (*Sync Project with Gradle Files*) y ejecutar en emulador o dispositivo físico Android.
