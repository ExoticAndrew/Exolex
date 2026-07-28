# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

"Exotic" (base package `exolex.exotic`) is a Spring Boot 4.0.7 / Java 21 REST API for managing legal
processes ("Exolex"-style case management). Domain and code (field names, validation messages, exception
text) are in Portuguese. Key entities:

- `Cliente` — a client (customer) of the firm.
- `Processo` — a legal case/process, owned by a `Cliente`, with a `StatusProcesso` (ATIVO/ARQUIVADO/ENCERRADO).
- `Usuario` — an application user (login/JWT subject is `email`).
- `ProcessoUsuario` — join entity linking a `Usuario` to a `Processo` with a `PapelProcesso` role
  (RESPONSAVEL/COLABORADOR/VISUALIZADOR). This is how per-process access control is modeled — there is no
  global user role system.

## Build, run, test

Use the Maven wrapper (no need for a global Maven install). On Windows use `mvnw.cmd`.

```
mvnw.cmd clean package        # build
mvnw.cmd spring-boot:run       # run locally (port 8080)
mvnw.cmd test                  # run all tests
mvnw.cmd test -Dtest=ExoticApplicationTests            # run a single test class
mvnw.cmd test -Dtest=ExoticApplicationTests#contextLoads  # run a single test method
```

The app boots against an in-memory H2 database (`jdbc:h2:mem:exotic_db`, ddl-auto: update), so no external
DB setup is needed for local dev/tests. H2 console is enabled at `/h2-console`. Swagger/OpenAPI UI is
available (springdoc) once the app is running.

Kafka is optional infrastructure: `docker-compose.yml` (in the parent `exotic/` directory, one level above
this Maven project) starts a single-node Kafka broker + kafka-ui. `application.yaml` currently points
`spring.kafka.bootstrap-servers` at `localhost:9092`, but the broker's host-reachable listener in
docker-compose is advertised as `localhost:29092` — if you're exercising the Kafka code path against the
dockerized broker from the host JVM, you'll need to adjust the bootstrap-servers port. The Kafka wiring
(`kafka/TesteProducerService`, `TesteConsumer`, `TesteController`) is scaffolding/smoke-test code, not part
of the core domain.

## Architecture

Standard layered structure: `controller` → `service` → `repository`/`model`, with a separate `map` package
for entity→DTO mapping and a shared `exception` package for domain exceptions + a `@RestControllerAdvice`
(`GlobalExceptionHandler`) that translates them into a consistent `ErroResponse` JSON body.

**Auth & security:**
- JWT-based, fully stateless (`SessionCreationPolicy.STATELESS`), CSRF disabled.
- `SecurityConfig` permits `/api/auth/**`, `/h2-console/**`, and swagger paths; everything else requires a
  valid bearer token.
- `JwtFilter` validates the token, extracts the email (JWT subject), loads the matching `Usuario`, and sets
  a `UsernamePasswordAuthenticationToken` with **no granted authorities** — Spring Security is only used to
  gate "is this request authenticated," not for role-based access.
- Per-resource authorization is done manually in `ProcessoService` (not via `@PreAuthorize` or Spring
  method security): `buscarProcessoComAcesso` checks the caller has *any* `ProcessoUsuario` link to the
  process; `exigirPapel` checks the caller's `PapelProcesso` on that process is an **exact match** to the
  role required (e.g. adding a collaborator or deleting a process both require the caller's role to be
  exactly `RESPONSAVEL`). When extending `ProcessoService`, follow this same manual-check pattern rather
  than introducing Spring Security roles/authorities.
- The authenticated user is always resolved via `SecurityContextHolder` → email → `UsuarioRepository`
  (see `ProcessoService.getUsuarioAutenticado()`); there is no `@AuthenticationPrincipal` custom type.

**DTOs:** all request/response DTOs are Java records under `dtos/`, validated with `jakarta.validation`
annotations (messages in Portuguese, surfaced verbatim through `GlobalExceptionHandler`).

**Lombok:** entities use `@Data` + `@NoArgsConstructor`; services/controllers use
`@RequiredArgsConstructor` for constructor injection — follow this convention for new classes rather than
writing explicit getters/setters/constructors.