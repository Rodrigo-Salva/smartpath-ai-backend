# 🌟 SmartPath AI – Backend

Plataforma de desarrollo profesional impulsada por IA para optimizar CVs, detectar brechas de habilidades, recomendar recursos y practicar entrevistas técnicas y conductuales.

---

## 📌 Descripción General

**SmartPath AI Backend** es un servicio REST construido con Spring Boot que proporciona toda la lógica de negocio y APIs necesarias para un asistente de carrera profesional basado en IA.

Permite gestionar:
- Usuarios y perfiles profesionales
- CVs y análisis automatizados
- Skills y brechas de habilidades
- Recursos de aprendizaje personalizados
- Entrevistas simuladas con feedback de IA
- Notificaciones internas y progreso del usuario

---

## 🧱 Arquitectura y Stack

### 🏗️ Arquitectura

- **Monolito modular** organizado por dominios: `auth`, `cv`, `skill`, `interview`, `resource`, `notification`, `progress`, `ai`, etc.
- **Patrón de capas**: `Controller` → `Service` → `Repository` → `Entity`
- **Integración con IA** encapsulada en un servicio especializado, responsable de construir prompts y consumir la API externa

### ⚙️ Tecnologías Principales

| Componente | Tecnología |
|------------|------------|
| **Lenguaje** | Java 17+ |
| **Framework** | Spring Boot 3.x |
| **Persistencia** | Spring Data JPA + Hibernate + PostgreSQL |
| **Seguridad** | Spring Security + JWT |
| **IA** | Cliente HTTP hacia modelo de lenguaje tipo Gemini |
| **PDF** | Apache PDFBox para extraer texto de CVs |
| **Documentación** | OpenAPI / Swagger UI |

---

## 🗂️ Modelo de Datos

### Relaciones Principales entre Entidades

```
User
├─ CV (1:N)
│   ├─ CVAnalysis (1:1)
│   └─ CVSkill (1:N) ──> Skill
├─ SkillGap (1:N) ──> Skill
├─ UserSkill (1:N) ──> Skill
├─ InterviewPractice (1:N)
│   └─ InterviewQuestion (1:N)
├─ UserProgress (1:N) ──> LearningResource
├─ AIRecommendation (1:N) ──> LearningResource
└─ Notification (1:N)
```

### Entidades Destacadas

#### 👤 User
Credenciales, perfil extendido y preferencias profesionales.

#### 📄 CV
Archivo PDF, metadatos y texto extraído.

#### 🧠 CVAnalysis
Puntuación global, fortalezas, debilidades y mejoras sugeridas por IA.

#### 🎯 Skill, CVSkill, UserSkill
Catálogo de habilidades y su relación con CV y usuario.

#### 🧩 SkillGap
Brechas de habilidades por rol objetivo, importancia y horas estimadas.

#### 📚 LearningResource
Cursos, libros, vídeos, certificaciones y otros recursos.

#### 💡 AIRecommendation
Recursos recomendados con score y motivo generado por IA.

#### 🎤 InterviewPractice y InterviewQuestion
Simulaciones de entrevista y preguntas concretas.

#### 📈 UserProgress
Progreso del usuario sobre recursos de aprendizaje.

#### 🔔 Notification
Sistema de notificaciones internas del usuario.

---

## 🧩 Módulos Funcionales

### 🔐 Autenticación y Usuarios

#### Funciones Clave

- Registro e inicio de sesión con emisión de JWT
- Protección de endpoints mediante filtros de seguridad
- Gestión de perfil extendido y cambio de contraseña

#### Perfil Extendido

Incluye:
- Nombre completo, ubicación, teléfono, bio
- Enlaces profesionales (LinkedIn, GitHub, portfolio)
- Rol objetivo
- Nivel de experiencia y años de experiencia
- Expectativa salarial
- Modo de trabajo preferido (`REMOTE`, `HYBRID`, `ONSITE`)
- Disponibilidad para nuevas oportunidades

#### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register` | Registro de usuario |
| `POST` | `/api/v1/auth/login` | Login y obtención de token |
| `GET` | `/api/v1/users/profile` | Obtener perfil del usuario autenticado |
| `PUT` | `/api/v1/users/profile` | Actualizar perfil |
| `PUT` | `/api/v1/users/change-password` | Cambiar contraseña |
| `GET` | `/api/v1/users/dashboard` | Dashboard con métricas y siguiente acción recomendada |

---

### 📄 CV: Subida y Análisis con IA

#### Flujo Funcional

1. El usuario sube un CV en PDF
2. El sistema extrae el texto del PDF
3. Se envía el contenido a IA para obtener:
    - Puntuación global del CV
    - Lista de fortalezas, debilidades y mejoras sugeridas
4. Se persiste el objeto `CVAnalysis` asociado al CV
5. Se genera una notificación indicando que el análisis está disponible

#### Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/cvs/upload` | Subida de CV (multipart) |
| `GET` | `/api/v1/cvs` | Listado de CVs del usuario |
| `POST` | `/api/v1/cvs/{id}/analyze` | Lanzar análisis de CV con IA |
| `GET` | `/api/v1/cvs/{id}/analysis` | Recuperar análisis almacenado |

---

### 🎯 Skills y Análisis de Brechas

#### 🔍 Detección de Skills desde el CV

- A partir del texto extraído, se invoca IA para detectar habilidades
- La IA devuelve un JSON con skills (nombre, nivel, años, confianza)
- Se crean entidades `CVSkill` enlazadas a `Skill` (se crean nuevas si no existen)

#### 🧩 Gap Analysis (Brechas de Habilidades)

El sistema compara el rol objetivo del usuario con las skills detectadas en su CV.

**Respuesta de la IA incluye:**
- `missing_skills` con:
    - Nombre de skill
    - Importancia (`CRITICAL` / `HIGH` / `MEDIUM` / `LOW`)
    - Horas estimadas de estudio
    - Descripción de por qué es relevante

**Resultado del análisis:**
- Total de brechas
- Número de brechas críticas
- Horas totales estimadas
- Mensaje de recomendación de alto nivel

#### Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/skills` | Catálogo de skills activas (con filtro por categoría opcional) |
| `POST` | `/api/v1/skills/detect/{cvId}` | Detectar skills en un CV con IA |
| `GET` | `/api/v1/skills/cv/{cvId}` | Listar skills detectadas de un CV |
| `POST` | `/api/v1/skills/gap-analysis/{cvId}` | Ejecutar análisis de brechas |
| `GET` | `/api/v1/skills/my-gaps` | Obtener análisis de brechas actual del usuario |
| `PUT` | `/api/v1/skills/gaps/{gapId}/close` | Cerrar una brecha manualmente |
| `GET` | `/api/v1/skills/my-skills` | Skills objetivo del usuario |
| `POST` | `/api/v1/skills/my-skills` | Añadir skill manualmente al perfil |

---

### 💡 Recomendaciones Personalizadas con IA

#### Objetivo

Ofrecer al usuario una ruta de aprendizaje recomendada en función de:
- Su perfil y rol objetivo
- Sus brechas de habilidades
- Los recursos de aprendizaje disponibles

#### Comportamiento

1. Se construye un contexto con los datos del usuario
2. IA devuelve orientación textual que se utiliza para valorar recursos
3. Se generan entidades `AIRecommendation` con:
    - Recurso recomendado
    - Score de recomendación
    - Motivo (texto)
    - Modelo de IA utilizado
4. Se notifica al usuario cuando se crean nuevas recomendaciones

---

### 🎤 Entrevistas con IA

#### Características

**Generación de preguntas** de entrevista según:
- Rol objetivo
- Nivel de dificultad (`JUNIOR`, `INTERMEDIATE`, `SENIOR`)
- Tipo (`TECHNICAL`, `BEHAVIORAL`, `SITUATIONAL`, `MIXED`)

**Evaluación automática** de respuestas con IA:
- Puntuación por pregunta (0–10)
- Feedback detallado y sugerencias de mejora
- Cálculo de score global y feedback resumen al completar

**Historial** de entrevistas por usuario

#### Flujo

1. **startInterview**: se crea `InterviewPractice` y se generan preguntas con IA
2. **answerQuestion**: el usuario responde, IA evalúa y se guarda score y feedback
3. **completeInterview**: se calcula el promedio, se genera feedback global y se dispara una notificación

#### Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/interviews/start` | Iniciar una nueva práctica de entrevista |
| `GET` | `/api/v1/interviews/{id}/questions` | Obtener todas las preguntas de la sesión |
| `GET` | `/api/v1/interviews/{id}/next-question` | Obtener la siguiente pregunta sin responder |
| `POST` | `/api/v1/interviews/{id}/questions/{qId}/answer` | Responder y recibir feedback IA |
| `POST` | `/api/v1/interviews/{id}/complete` | Completar entrevista y obtener resultado final |
| `GET` | `/api/v1/interviews/{id}/result` | Consultar el resultado de una sesión completada |
| `GET` | `/api/v1/interviews/history` | Historial de prácticas del usuario |

---

### 📈 Progreso de Aprendizaje

#### Responsabilidad

Representar qué recursos está trabajando el usuario y en qué estado.

**Estados típicos** mediante un enum `ProgressStatus`:
- `IN_PROGRESS`
- `COMPLETED`
- Otros estados ampliables

**Se utiliza para:**
- Métricas del dashboard
- Seguimiento de la ruta de aprendizaje

---

### 📚 Recursos de Aprendizaje

#### Modelo

Cada `LearningResource` describe un recurso formativo:

- Título, descripción y URL
- **Tipo**: `COURSE`, `BOOK`, `VIDEO`, `ARTICLE`, `CERTIFICATION`, `PLATFORM`, etc.
- **Nivel de dificultad**: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`
- Proveedor, horas estimadas, si es gratuito, precio, rating y tags
- Campo `isActive` para distinguir entre activo e inactivo (soft delete)

#### Consultas y Filtros

**Listado general con filtros:**
- Tipo
- Dificultad
- Proveedor
- Gratuito o de pago
- Tag
- Rango de horas estimadas
- Búsqueda por texto en título o descripción

**Cálculo de estadísticas agregadas:**
- Total de recursos
- Recursos gratuitos vs de pago
- Conteo por tipo (curso, libro, vídeo)
- Media de horas estimadas
- Top proveedores distintos

#### Endpoints de Usuario

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/resources` | Listado con filtros opcionales y paginación |
| `GET` | `/api/v1/resources/{id}` | Detalle de un recurso |
| `GET` | `/api/v1/resources/type/{type}` | Filtrado por tipo |
| `GET` | `/api/v1/resources/difficulty/{level}` | Filtrado por dificultad |
| `GET` | `/api/v1/resources/free` | Solo recursos gratuitos |
| `GET` | `/api/v1/resources/search?query=...` | Búsqueda simple por texto |
| `GET` | `/api/v1/resources/stats` | Estadísticas generales |

#### Endpoints de Administración

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/resources` | Crear recurso (`ROLE_ADMIN`) |
| `PUT` | `/api/v1/resources/{id}` | Actualizar |
| `DELETE` | `/api/v1/resources/{id}` | Desactivar (soft delete) |
| `DELETE` | `/api/v1/resources/{id}/permanent` | Eliminar definitivamente |

---

### 🔔 Sistema de Notificaciones

#### Propósito

Mantener al usuario informado de eventos clave dentro de la plataforma:
- CV analizado
- Skills detectadas
- Brechas generadas o actualizadas
- Entrevistas completadas
- Nuevas recomendaciones de IA disponibles

#### Estructura

**Notification:**
- `title`, `message`
- `notificationType`: `INFO`, `SUCCESS`, `WARNING`, `ERROR`
- `category`: `CV_ANALYSIS`, `SKILL_GAP`, `INTERVIEW`, `RECOMMENDATION`, `PROGRESS`, etc.
- `priority`: `LOW`, `MEDIUM`, `HIGH`, `URGENT`
- `isRead`, `readAt`, `actionUrl`

#### Eventos Automáticos

- Cuando se completa un análisis de CV
- Cuando se detectan skills por primera vez en un CV
- Cuando el usuario termina una entrevista práctica
- Cuando se generan nuevas recomendaciones

#### Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/notifications` | Todas las notificaciones del usuario |
| `GET` | `/api/v1/notifications/unread` | No leídas |
| `GET` | `/api/v1/notifications/summary` | Resumen (contadores + últimas) |
| `PUT` | `/api/v1/notifications/{id}/read` | Marcar como leída |
| `PUT` | `/api/v1/notifications/mark-all-read` | Marcar todas como leídas |
| `DELETE` | `/api/v1/notifications/{id}` | Eliminar una |
| `DELETE` | `/api/v1/notifications/delete-read` | Eliminar todas las leídas |

---

### 📊 Dashboard de Usuario

El endpoint `/api/v1/users/dashboard` expone un resumen ejecutivo para el usuario autenticado:

- **Perfil** (UserProfileDTO)
- **Número total de CVs** subidos
- **Número de brechas abiertas** (SkillGap no cerradas)
- **Entrevistas completadas** y puntuación media
- **Recursos de aprendizaje** en progreso
- **Notificaciones no leídas**
- **Mensaje de "siguiente acción recomendada"**, en función de:
    - Si no tiene CVs: se sugiere subir el primero
    - Si tiene brechas: se recomienda cerrarlas
    - Si no tiene entrevistas: se propone practicar la primera
    - En caso contrario: se refuerza el buen progreso

---

## 🔄 Flujos de Uso Recomendados

### 1. Primer Uso (Onboarding)

1. Registro y login
2. Completar datos principales del perfil
3. Subir CV y lanzar análisis con IA

### 2. Descubrimiento de Brechas y Recursos

1. Detectar skills desde el CV
2. Ejecutar análisis de brechas contra el rol objetivo
3. Consultar recursos de aprendizaje recomendados para cada brecha

### 3. Entrenamiento Guiado

1. Revisar el dashboard para ver la siguiente acción recomendada
2. Consumir recursos de aprendizaje y registrar progreso
3. Practicar entrevistas según el rol objetivo

### 4. Iteración y Mejora Continua

1. Actualizar CV y volver a analizarlo
2. Cerrar brechas completadas
3. Revisar recomendaciones nuevas que vayan apareciendo

---

## ⚙️ Instalación y Ejecución

### Requisitos Previos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (local o en Docker)
- Clave de API para el proveedor de IA

### Pasos Básicos

```bash
# 1. Clonar repo
git clone <url-del-repo>
cd smartpath-ai-backend

# 2. Configurar variables de entorno (ejemplos)
export GEMINI_API_KEY=tu_api_key
export DB_PASSWORD=tu_password_db
export JWT_SECRET=un_secret_seguro

# 3. (Opcional) Levantar PostgreSQL con Docker
docker run -d \
  --name smartpath-postgres \
  -e POSTGRES_DB=smartpath \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:14

# 4. Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

### Swagger / OpenAPI

Una vez en marcha:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🛡️ Seguridad

- **Autenticación basada en JWT**
- Endpoints protegidos por filtros de seguridad; se espera `Authorization: Bearer <token>`
- **Roles soportados**: `ROLE_USER`, `ROLE_ADMIN`
- Endpoints de administración protegidos mediante anotaciones como `@PreAuthorize("hasRole('ADMIN')")`

---

## 📏 Convenciones y Buenas Prácticas

- **Organización por módulos de dominio**: `auth`, `cv`, `skill`, `interview`, `resource`, `notification`, `progress`, `ai`, etc.
- Uso extensivo de **Lombok** para reducir boilerplate
- **DTOs separados** del modelo de persistencia para no exponer entidades directamente
- Métodos de servicio anotados con `@Transactional` donde corresponde
- Manejo de errores mediante excepciones controladas y respuestas coherentes de API

---

## 🚀 Roadmap Sugerido

- Panel de administración web para gestión avanzada de recursos, skills y usuarios
- Integración de notificaciones por email o push externo
- Internacionalización (i18n) de mensajes y feedback generado
- Métricas y observabilidad (Prometheus / Grafana)
- Integración con un frontend React / Angular / Vue y futura app móvil

---

## 📄 Licencia

Incluye un archivo `LICENSE` en la raíz del proyecto con la licencia elegida (por ejemplo, MIT) y referencia a ella en este README.

---

## 📞 Contacto y Contribuciones

Para reportar problemas, solicitar funcionalidades o contribuir al proyecto, visita el repositorio en GitHub o contacta al equipo de desarrollo.

**¡Gracias por usar SmartPath AI!** 🚀
