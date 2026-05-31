# Esquema del archivo de configuración JSON — LogísTEC (LGTC)

Documento del **protocolo de entrada** del sistema. Lo produce/consume el módulo `io`
(`JsonLoader` → `LogisticsConfig`). Responsable: **Persona 1**.

El parseo se realiza con **Gson** (única librería de terceros permitida, sección 5 del
enunciado). Gson no es objetivo del curso: solo convierte el texto JSON en objetos Java; la
validación del modelo la hace `JsonLoader`.

---

## 1. Estructura general

```json
{
  "ciudad":   { "vertices": [ ... ], "aristas": [ ... ] },
  "paquetes": [ ... ],
  "camiones": [ ... ]
}
```

| Clave        | Tipo            | Obligatorio | Descripción                                   |
|--------------|-----------------|:-----------:|-----------------------------------------------|
| `ciudad`     | objeto          | Sí          | El grafo de la ciudad.                         |
| `paquetes`   | arreglo         | No*         | Paquetes a repartir. Puede omitirse o ir vacío.|
| `camiones`   | arreglo         | Sí          | Flota. Debe tener al menos un camión.          |

\* Si no hay paquetes, el sistema carga igual pero no hay nada que planificar.

---

## 2. `ciudad.vertices[]`

Cada vértice representa una intersección o punto de interés.

```json
{ "id": "A", "tipo": "DEPOT", "x": 100, "y": 100 }
```

| Campo  | Tipo    | Reglas                                                                 |
|--------|---------|------------------------------------------------------------------------|
| `id`   | string  | No vacío. **Único** en todo el grafo.                                  |
| `tipo` | string  | Uno de `DEPOT`, `INTERSECCION`, `ENTREGA`. Si se omite → `INTERSECCION`.|
| `x`    | entero  | Coordenada horizontal (solo para dibujar en la UI).                    |
| `y`    | entero  | Coordenada vertical (solo para dibujar en la UI).                      |

**Regla del depósito:** debe existir **exactamente un** vértice con `tipo = "DEPOT"`.
Su `id` queda accesible como `LogisticsConfig.depotId()`.

---

## 3. `ciudad.aristas[]`

Cada arista es una calle **bidireccional** (grafo no dirigido) con su distancia en metros.

```json
{ "u": "A", "v": "B", "distancia": 320 }
```

| Campo       | Tipo   | Reglas                                                   |
|-------------|--------|----------------------------------------------------------|
| `u`         | string | `id` de un vértice **existente**.                        |
| `v`         | string | `id` de un vértice **existente**, distinto de `u`.       |
| `distancia` | entero | **> 0** (metros).                                        |

La arista `(u,v)` equivale a `(v,u)`; Persona 2 la inserta en ambos sentidos en la lista
de adyacencia. La lista `aristas` puede ir vacía (`[]`) pero la clave debe estar presente.

---

## 4. `paquetes[]`

```json
{ "id": "P01", "destino": "G", "peso": 5, "prioridad": 1 }
```

| Campo       | Tipo   | Reglas                                                         |
|-------------|--------|----------------------------------------------------------------|
| `id`        | string | No vacío. **Único** entre los paquetes.                        |
| `destino`   | string | `id` de un vértice **existente**.                              |
| `peso`      | entero | **> 0** (kilogramos).                                          |
| `prioridad` | entero | **∈ {1, 2, 3}**, donde 1 es la más alta.                       |

> Nota: que el `destino` exista **no** significa que sea alcanzable desde el depósito.
> La alcanzabilidad la decide **Warshall** (Persona 2) sobre el grafo ya construido; los
> destinos inalcanzables se marcan como rechazados en esa etapa, no aquí.

---

## 5. `camiones[]`

```json
{ "id": "C01", "capacidad": 50 }
```

| Campo       | Tipo   | Reglas                                          |
|-------------|--------|-------------------------------------------------|
| `id`        | string | No vacío. **Único** entre los camiones.         |
| `capacidad` | entero | **> 0** (kilogramos de carga máxima).           |

Todos los camiones inician y terminan su ruta en el depósito.

---

## 6. Ejemplo mínimo válido (anotado)

```json
{
  "ciudad": {
    "vertices": [
      {"id": "A", "tipo": "DEPOT",        "x": 100, "y": 100},
      {"id": "B", "tipo": "INTERSECCION", "x": 250, "y": 150},
      {"id": "G", "tipo": "ENTREGA",      "x": 300, "y": 400}
    ],
    "aristas": [
      {"u": "A", "v": "B", "distancia": 320},
      {"u": "B", "v": "G", "distancia": 410}
    ]
  },
  "paquetes": [
    {"id": "P01", "destino": "G", "peso": 5, "prioridad": 1}
  ],
  "camiones": [
    {"id": "C01", "capacidad": 50},
    {"id": "C02", "capacidad": 30}
  ]
}
```

---

## 7. Errores que `JsonLoader` reporta

Todos se lanzan como `ConfigValidationException` con un mensaje legible:

- JSON mal formado o vacío.
- Falta `ciudad`, `vertices` vacío, falta `aristas`, o flota vacía.
- `id` de vértice/paquete/camión vacío o duplicado.
- Cero o más de un vértice `DEPOT`.
- Arista con extremo inexistente, lazo (`u == v`) o `distancia <= 0`.
- Paquete con destino inexistente, `peso <= 0` o `prioridad` fuera de {1,2,3}.
- Camión con `capacidad <= 0`.

---

## 8. Contrato con el resto del equipo

`JsonLoader.load(path)` devuelve un `LogisticsConfig` con estas vistas (todas usan la
`DLinkedList` del equipo, no `java.util`):

| Método                  | Devuelve                                  | Consumidor          |
|-------------------------|-------------------------------------------|---------------------|
| `depotId()`             | `String`                                  | Persona 2 / 4       |
| `vertices()`            | `DLinkedList<VertexInfo>` (id, tipo, x, y)| Persona 2 (grafo)   |
| `edges()`               | `DLinkedList<EdgeInfo>` (u, v, distancia) | Persona 2 (grafo)   |
| `packages()`            | `DLinkedList<PackageInfo>`                | Persona 4 (modelo)  |
| `trucks()`              | `DLinkedList<TruckInfo>`                  | Persona 4 (modelo)  |

`VertexInfo.tipo()` se entrega como `String` (`"DEPOT"`, `"INTERSECCION"`, `"ENTREGA"`);
Persona 2 lo convierte a su enum `VertexType`.
