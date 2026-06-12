# LogísTEC — Segundo Proyecto CE1103

Proyecto desarrollado para el curso **Algoritmos y Estructuras de Datos I (CE1103)** del Tecnológico de Costa Rica.

**Nombre del proyecto:** LogísTEC
**Lenguaje:** Java 17 o superior
**Formato de entrada:** JSON
**Librería externa:** Gson para lectura de JSON

---

## 1. Descripción general

LogísTEC es una aplicación en Java que simula un sistema básico de planificación logística para una empresa de distribución.

La ciudad se modela como un **grafo no dirigido y ponderado**, donde:

* Los vértices representan intersecciones, puntos de entrega o el depósito.
* Las aristas representan calles bidireccionales.
* Los pesos representan distancias en metros.
* Los paquetes deben ser asignados a camiones según su capacidad.
* Cada camión debe recibir una ruta de entrega calculada con heurísticas.

El sistema carga la información desde un archivo JSON, valida la conectividad del grafo, calcula caminos mínimos, genera árboles de expansión mínima, asigna paquetes a camiones y planifica rutas de entrega.

---

## 2. Funcionalidades implementadas

El proyecto implementa las siguientes funcionalidades principales:

### Carga de datos

* Carga de ciudad, vértices, aristas, paquetes y camiones desde un archivo JSON.
* Validación básica del archivo de configuración.
* Identificación del depósito principal.

### Grafo

* Grafo no dirigido y ponderado.
* Representación mediante lista de adyacencia.
* Vértices con identificador, tipo y coordenadas.
* Aristas con distancia en metros.

### Algoritmos de grafos

* BFS.
* DFS.
* Warshall para cierre transitivo.
* Dijkstra para caminos mínimos desde un origen.
* Floyd-Warshall para caminos mínimos entre todos los pares.
* Prim para árbol de expansión mínima.
* Kruskal para árbol de expansión mínima.

### Estructuras de datos propias

* Lista doblemente enlazada.
* Cola.
* Pila.
* Cola de prioridad basada en heap mínimo.
* Union-Find para Kruskal.

### Planificación logística

* Validación de paquetes inalcanzables desde el depósito.
* Rechazo de paquetes con destino inalcanzable.
* Asignación de paquetes a camiones según prioridad, peso y capacidad.
* Heurística de asignación tipo best-fit.
* Ruteo mediante Vecino Más Cercano.
* Ruteo mediante heurística basada en MST.
* Comparación entre Nearest Neighbor y MST-based.
* Selección de la mejor ruta por camión.
* Cálculo de distancia total por ruta.
* Cálculo de carga total y porcentaje de ocupación del camión.
* Cálculo del ahorro porcentual de MST-based respecto a Nearest Neighbor.

### Interfaz gráfica

* Visualización básica del grafo mediante Swing.
* Dibujo de vértices y aristas.
* Identificación visual del depósito.
* Identificación de puntos de entrega.
* Dibujo de rutas de camiones con colores diferentes.
* Leyenda visual para interpretar la ventana.

---

## 3. Requisitos del sistema

Para compilar y ejecutar el proyecto se necesita:

* Java JDK 17 o superior.
* Sistema operativo Windows, Linux o macOS.
* En Windows se recomienda usar los archivos `.bat` incluidos.
* Librería Gson ubicada en la carpeta `lib`.

La estructura esperada para Gson es:

```text
lib/gson-2.10.1.jar
```

---

## 4. Estructura del proyecto

```text
LogisTEC
├── data
│   └── caso_prueba.json
├── docs
│   └── esquema_json.md
├── lib
│   └── gson-2.10.1.jar
├── src
│   ├── algorithms
│   ├── ds
│   ├── graph
│   ├── io
│   ├── model
│   ├── planner
│   ├── ui
│   └── Main.java
├── compilar.bat
├── ejecutar.bat
├── compilar_y_ejecutar.bat
└── README.md
```

---

## 5. Descripción de paquetes

### `graph`

Contiene las clases relacionadas con el grafo:

* `Graph`
* `Vertex`
* `Edge`
* `VertexType`

Este paquete modela la ciudad como un grafo no dirigido y ponderado.

---

### `algorithms`

Contiene los algoritmos principales del proyecto:

* `Traversals`
* `Warshall`
* `Dijkstra`
* `FloydWarshall`
* `Prim`
* `Kruskal`

Este paquete se encarga de los recorridos, caminos mínimos, cierre transitivo y árboles de expansión mínima.

---

### `ds`

Contiene estructuras de datos implementadas por el equipo:

* `DLinkedList`
* `Queue`
* `Stack`
* `MinHeap`
* `UnionFind`

Estas estructuras son utilizadas por los algoritmos del proyecto.

---

### `io`

Contiene las clases de carga y validación del JSON:

* `JsonLoader`
* `LogisticsConfig`
* `ConfigValidationException`

Este paquete se encarga de leer el archivo de entrada y transformar los datos en objetos utilizables por el sistema.

---

### `model`

Contiene las clases del modelo logístico:

* `ProblemCase`
* `Package`
* `Truck`

Este paquete representa el caso completo del problema, los paquetes y la flota de camiones.

---

### `planner`

Contiene las clases relacionadas con la planificación logística:

* `PackageAssigner`
* `AssignmentResult`
* `TruckAssignment`
* `NearestNeighborRouter`
* `MSTBasedRouter`
* `RouteResult`
* `RouteComparison`

Este paquete asigna paquetes a camiones y calcula rutas usando heurísticas.

---

### `ui`

Contiene la interfaz gráfica:

* `LogisTecFrame`

Este paquete dibuja el grafo y las mejores rutas calculadas para los camiones.

---

## 6. Formato del JSON de entrada

El archivo JSON debe contener:

* Ciudad.
* Lista de vértices.
* Lista de aristas.
* Lista de paquetes.
* Lista de camiones.

Ejemplo general:

```json
{
  "ciudad": {
    "vertices": [
      {
        "id": "V00",
        "tipo": "DEPOT",
        "x": 100,
        "y": 100
      },
      {
        "id": "V01",
        "tipo": "INTERSECCION",
        "x": 200,
        "y": 100
      },
      {
        "id": "V02",
        "tipo": "ENTREGA",
        "x": 300,
        "y": 150
      }
    ],
    "aristas": [
      {
        "u": "V00",
        "v": "V01",
        "distancia": 250
      },
      {
        "u": "V01",
        "v": "V02",
        "distancia": 300
      }
    ]
  },
  "paquetes": [
    {
      "id": "P01",
      "destino": "V02",
      "peso": 5,
      "prioridad": 1
    }
  ],
  "camiones": [
    {
      "id": "C01",
      "capacidad": 50
    }
  ]
}
```

---

## 7. Tipos de vértices aceptados

El sistema acepta los siguientes tipos:

```text
DEPOT
DEPOSITO
DEPÓSITO
DELIVERY
ENTREGA
INTERSECCION
INTERSECCIÓN
INTERSECTION
```

Internamente se manejan como:

```text
DEPOT
DELIVERY
INTERSECTION
```

---

## 8. Cómo compilar en Windows

Desde la raíz del proyecto, ejecutar:

```bat
compilar.bat
```

Este archivo:

1. Verifica que exista la carpeta `src`.
2. Verifica que exista `lib/gson-2.10.1.jar`.
3. Elimina la carpeta `out` si ya existía.
4. Compila todos los archivos `.java`.
5. Guarda los `.class` en la carpeta `out`.

---

## 9. Cómo ejecutar en Windows

Después de compilar, ejecutar:

```bat
ejecutar.bat
```

Este archivo ejecuta el programa usando:

```text
data/caso_prueba.json
```

como caso de prueba principal.

---

## 10. Compilar y ejecutar en un solo paso

También se puede usar:

```bat
compilar_y_ejecutar.bat
```

Este archivo compila el proyecto y, si no hay errores, lo ejecuta automáticamente.

---

## 11. Cómo compilar manualmente

Si no se desea usar `.bat`, se puede compilar manualmente con:

```bat
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\gson-2.10.1.jar" -d out @sources.txt
```

---

## 12. Cómo ejecutar manualmente

Después de compilar:

```bat
java -cp "out;lib\gson-2.10.1.jar" Main data\caso_prueba.json
```

---

## 13. Salida esperada en consola

Al ejecutar el programa, se muestra información como:

```text
=== LogísTEC (LGTC) ===

ProblemCase{vertices=34, aristas=56, paquetes=15, camiones=3, depot=V00}

Componentes conexas: 1 (grafo conexo)

--- Validación (Warshall) ---
Paquetes con destino inalcanzable: 0

--- Dijkstra ---
Distancia mínima

--- Floyd-Warshall ---
Distancia calculada en la matriz D

--- MST: Prim vs Kruskal ---
Costo Prim
Costo Kruskal
¿Mismo costo total?

--- Asignación de paquetes a camiones ---
Camiones con sus paquetes asignados

--- Rutas por camión: Nearest Neighbor vs MST-based ---
Ruta Nearest Neighbor
Ruta MST-based
Mejor ruta elegida
Distancia mejor ruta
Carga total
Ocupación
Ahorro MST sobre NN
```

Además, se abre una ventana gráfica donde se visualiza el grafo y las rutas.

---

## 14. Algoritmos implementados

### Warshall

Se utiliza para calcular la matriz de cierre transitivo del grafo.

Permite saber si un destino es alcanzable desde el depósito.

Si un paquete tiene destino inalcanzable, se marca como rechazado y no se asigna a ningún camión.

---

### Dijkstra

Se utiliza para calcular el camino mínimo desde un origen hacia los demás vértices.

En el programa se muestra un ejemplo de camino mínimo desde el depósito hacia el primer destino entregable.

Usa una cola de prioridad implementada por el equipo.

---

### Floyd-Warshall

Se utiliza para calcular las distancias mínimas entre todos los pares de vértices.

La matriz generada por Floyd-Warshall se usa como base para las heurísticas de ruteo.

---

### Prim

Construye un árbol de expansión mínima del grafo.

El programa muestra el costo total del MST obtenido con Prim.

---

### Kruskal

Construye otro árbol de expansión mínima del grafo.

Usa Union-Find para evitar ciclos.

El programa compara el costo obtenido por Kruskal con el costo obtenido por Prim.

---

### Nearest Neighbor

Heurística de ruteo donde el camión siempre visita la parada no visitada más cercana desde su posición actual.

La ruta empieza en el depósito y termina regresando al depósito.

---

### MST-based

Heurística de ruteo basada en construir un MST sobre el conjunto formado por:

```text
depósito + paradas del camión
```

Luego se hace un DFS preorden sobre ese MST para obtener el orden de visita.

La ruta también inicia y termina en el depósito.

---

## 15. Asignación de paquetes a camiones

La asignación sigue estos pasos:

1. Se ordenan los paquetes por prioridad ascendente.
2. Si dos paquetes tienen la misma prioridad, se ordenan por peso descendente.
3. Cada paquete se intenta asignar a un camión con capacidad suficiente.
4. Si ningún camión puede llevar el paquete, se rechaza por capacidad.
5. Los paquetes con destino inalcanzable ya vienen rechazados desde la validación con Warshall.

---

## 16. Interfaz gráfica

La interfaz gráfica se implementó usando Swing.

La ventana muestra:

* Calles del grafo en gris.
* Vértices del grafo.
* Depósito.
* Puntos de entrega.
* Intersecciones.
* Mejores rutas de los camiones.
* Leyenda de colores.

La interfaz tiene como objetivo ser clara durante la defensa del proyecto, no necesariamente ser una aplicación visual avanzada.

---

## 17. Caso de prueba incluido

El archivo principal de prueba es:

```text
data/caso_prueba.json
```

Este caso contiene:

* Al menos 30 vértices.
* Al menos 50 aristas.
* 15 paquetes.
* 3 camiones.
* Un depósito.

---

## 18. Problemas conocidos y limitaciones

* La interfaz gráfica es básica.
* El sistema no permite editar el grafo desde la interfaz.
* La ruta visual dibuja líneas directas entre vértices de la ruta, no necesariamente el camino físico completo reconstruido por Dijkstra entre cada par.
* Las heurísticas no garantizan siempre la ruta óptima.
* El sistema depende de que el JSON esté correctamente formado.
* La comparación empírica con múltiples instancias debe completarse con más archivos de prueba si se desea documentar tiempos sobre casos adicionales.

---

## 19. Recomendaciones de uso

Para la defensa se recomienda:

1. Ejecutar primero `compilar_y_ejecutar.bat`.
2. Mostrar la salida en consola.
3. Explicar la validación con Warshall.
4. Mostrar la comparación Prim vs Kruskal.
5. Explicar la asignación de paquetes.
6. Explicar las dos rutas por camión.
7. Mostrar la ventana gráfica.
8. Indicar las limitaciones conocidas.
