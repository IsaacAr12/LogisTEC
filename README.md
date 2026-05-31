# LogísTEC (LGTC)

Sistema de planificación logística sobre grafos no dirigidos ponderados — CE1103, TEC 2026-I.

## Integrantes

| Rol                                   | Nombre | Carné |
|---------------------------------------|--------|-------|
| P1 · Estructuras de datos & IO        |        |       |
| P2 · Grafo, recorridos & alcanzabilidad |      |       |
| P3 · Caminos mínimos & MST            |        |       |
| P4 · Planificación & visualización    |        |       |

## Requisitos

- **Java 17 LTS o superior** (probado con OpenJDK 21).
- **Gson 2.11** (parseo del JSON de entrada — única librería de terceros).
- **JUnit 5** (solo para pruebas; no forma parte del producto).

## Estructura

Layout **plano**: las fuentes van directamente en `src/<paquete>/` (paquetes cortos:
`ds`, `graph`, `algorithms`, `model`, `planner`, `io`, `ui`) y las pruebas en
`src/test/java/<paquete>/`.

## Cómo compilar y ejecutar

### Opción A — Maven (recomendada)

```bash
mvn clean test      # compila y corre las pruebas JUnit
mvn clean package   # genera el jar en target/
java -cp target/logistec-1.0.0.jar:<ruta-gson.jar> Main data/caso_prueba.json
```

### Opción B — javac directo (sin Maven)

Coloquen `gson-2.11.0.jar` en `lib/`. Desde la raíz del proyecto:

```bash
# Compilar todo el código fuente (sin las pruebas)
javac --release 17 -cp lib/gson-2.11.0.jar -d out -sourcepath src \
  $(find src -name '*.java' -not -path 'src/test/*')

# Ejecutar
java -cp out:lib/gson-2.11.0.jar Main data/caso_prueba.json
```

### Opción C — IDE (NetBeans / IntelliJ)

Marcar `src` como *Source Root* y `src/test/java` como *Test Source Root*; agregar Gson
y JUnit 5 como dependencias del proyecto.

## Documentación

- `docs/esquema_json.md` — protocolo del archivo de configuración de entrada.
- `docs/manual_usuario.pdf` — manual de usuario (LaTeX → PDF).
- `docs/diagrama_clases.png` — diagrama de clases.
- `docs/bitacora/` — bitácoras del equipo.
