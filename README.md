# Dungeon Seekers

Juego web de rol (estilo dungeon crawler) desarrollado como **trabajo práctico universitario**.  
Fuimos **2 personas**: yo implementé casi toda la aplicación (dominio del juego, pantallas, persistencia, tests y pulido del proyecto); mi compañero se encargó de la **integración con Mercado Pago**.

Este fue mi **primer proyecto serio en Java**.

---

## ¿Qué es?

En *Dungeon Seekers* el jugador gestiona héroes, arma un carruaje, compra ítems, cura en el santuario y avanza por **expediciones** con **mazmorras** y combates por turnos. También hay ranking entre jugadores y compra de oro con Mercado Pago.

### Funcionalidades principales

- Registro e inicio de sesión (contraseñas hasheadas con BCrypt)
- Reclutamiento de héroes y gestión del carruaje
- Tienda de ítems y economía con oro
- Santuario de curación
- Combate por turnos (atacar / defender) en mazmorras
- Progresión por expediciones y niveles de mazmorra
- Ranking de jugadores
- Compra de oro con Mercado Pago (aporte del compañero)

---

## Stack técnico

| Área | Tecnología |
|------|------------|
| Lenguaje | Java 11 |
| Framework | Spring MVC 5 |
| Vistas | Thymeleaf + Bootstrap |
| Persistencia | Hibernate + JDBC (JdbcTemplate) |
| Base de datos | HSQLDB (embebida) |
| Build / servidor | Maven + Jetty embebido |
| Seguridad de claves | BCrypt (`spring-security-crypto`) |
| Pagos | Mercado Pago SDK |
| Tests | JUnit 5, Mockito, Spring Test, Playwright (E2E) |

---

## Cómo correrlo

### Requisitos

- JDK 11 o superior
- Apache Maven 3.8+

### Ejecutar

```bash
mvn clean jetty:run
```

Abrí en el navegador:

**http://localhost:8080/spring**

### Tests

```bash
mvn clean test
```

### Mercado Pago (opcional)

Para probar compras de oro necesitás configurar credenciales locales (no se suben al repo):

1. Copiá el ejemplo:
   ```bash
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```
2. Completá `mp.access-token` y, si usás ngrok u otra URL pública, `app.base-url`.

También podés usar las variables de entorno `MP_ACCESS_TOKEN` y `APP_BASE_URL`.

---

## Estructura del proyecto

```text
src/main/java/com/tallerwebi/
├── config/            # Spring, Hibernate, hashing, Mercado Pago settings
├── dominio/           # Entidades, servicios y excepciones
├── infraestructura/   # Repositorios (persistencia)
└── presentacion/      # Controladores MVC

src/main/webapp/       # Vistas Thymeleaf, CSS, JS e imágenes
src/test/java/         # Tests unitarios, de integración y E2E
```

---

## Lo que aprendí

Este fue mi primer proyecto serio en Java. Más allá de “hacer que ande”, me obligó a pensar la app en capas y a resolver problemas reales de un sistema web:

- **Arquitectura en capas** con Spring MVC: separar presentación, servicios y persistencia (controladores, lógica de negocio y repositorios).
- **Modelar un dominio de juego** no trivial: expediciones, mazmorras, combates, héroes, carruaje, tienda, santuario y ranking, con reglas que se cruzan entre módulos.
- **Persistencia con Hibernate/JDBC** y una base embebida (HSQLDB), incluyendo relaciones entre entidades y el ciclo de una partida.
- **Autenticación básica** con sesión HTTP y **contraseñas hasheadas con BCrypt** (no guardar texto plano).
- **Tests** unitarios y de integración con JUnit/Mockito/Spring Test, y por qué el contexto de Spring en tests tiene que declarar los beans que usa la app.
- **Trabajo en equipo en un TP real**: coordinar un repo compartido y sumar una integración externa (Mercado Pago) hecha por mi compañero, sin romper el resto del sistema.
- **Pulir un proyecto académico para portfolio**: limpiar secretos, basura de entrega, nombres inconsistentes y dejarlo documentado y ejecutable.

---

## Créditos

- **Guido Fink** — lógica del juego, capas de la aplicación, UI, persistencia, tests y preparación del repo para portfolio  
- **Compañero de TP** — integración con Mercado Pago  

Proyecto académico universitario (Taller Web).
