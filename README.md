# CatalogoSupermercadoFase2
# Descripción:
Aplicación de escritorio en Java con interfaz gráfica hecha con Swing que gestiona un catálogo de productos distribuido en múltiples sucursales, usando seis estructuras de datos implementadas desde cero: Lista Enlazada (no ordenada y ordenada), Árbol AVL, Árbol B, Árbol B+ y Tabla Hash. El sistema soporta inserción, búsqueda y eliminación en todas las estructuras, carga desde CSV, transferencia de productos entre sucursales mediante un grafo con el algoritmo de Dijkstra, comparación de tiempos de búsqueda y generación de gráficos en formato Graphviz.

# Requisitos
Para compilar y ejecutar este proyecto, asegurarse de contar con lo siguiente en el sistema:
- **Java (JDK)** versión 21 o superior.
- **Apache Maven** versión 3.6 o superior.
- **Graphviz** instalado (`dot`) para generar las imágenes PNG y SVG de los árboles y grafos.
- **NetBeans IDE** (recomendado, versión 17+) o cualquier IDE compatible con proyectos Maven.

Para instalar Graphviz en Linux (Ubuntu/Debian):
```
sudo apt install graphviz
```

Para instalar Graphviz en Windows, descarga el instalador desde:
```
https://graphviz.org/download/
```
> **Importante:** Asegurarse de agregar Graphviz al PATH del sistema para que el programa pueda generar imágenes automáticamente.

# Instrucciones de Compilación

Este proyecto usa **Maven** como sistema de construcción, ofrece dos métodos para compilar: desde línea de comandos o desde un IDE.

# Opción 1: Compilar desde línea de comandos
1. Abre una terminal y navega hasta la carpeta del proyecto (donde se encuentra el archivo `pom.xml`):
   ```
   cd CatalogoSupermercado
   ```
2. Para compilar el proyecto y generar el JAR ejecutable:
   ```
   mvn clean package
   ```
3. Una vez finalizado, ejecuta el programa con:
   ```
   java -jar target/CatalogoSupermercado-1.0-SNAPSHOT.jar
   ```

# Opción 2: Compilar y ejecutar desde NetBeans IDE
1. Abre NetBeans y selecciona **File -> Open Project**.
2. Navega hasta la carpeta `CatalogoSupermercado` y ábrela como proyecto Maven.
3. Haz clic derecho sobre el proyecto en el panel de proyectos y selecciona **Clean and Build**.
4. Para ejecutar, haz clic en el botón **Run** o presiona `F6`.

# Uso del Programa
Al ejecutar el programa se abre una ventana con cuatro pestañas principales:
Gestión - Transferir - Visualizar - Sucursales

### Pestaña Gestión
Permite administrar el inventario de cada sucursal. Sus funciones principales son:
- **Cargar Sucursales / Rutas / Productos:** Carga datos desde archivos CSV.
- **Agregar / Eliminar / Editar Producto:** Gestión manual de productos en el inventario de una sucursal.
- **Mostrar Productos:** Visualiza el inventario ordenado por Nombre (AVL), Fecha de Expiración (Árbol B) o Categoría (Árbol B+).
- **Buscar por Nombre Secuencial (Lista Enlazada):** Búsqueda exacta y por coincidencias parciales.
- **Buscar por Rango de Fechas (Árbol B):** Consulta productos que caducan en un rango dado.
- **Buscar por Categorías (Árbol B+):** Agrupa y muestra productos de una categoría.
- **Buscar por Código (Hash):** Búsqueda O(1) por código de barras.
- **Listar por Nombre (AVL):** Recorrido in-order del árbol AVL.
- **Comparar Búsqueda (Lista Enlazada - AVL):** Mide y compara tiempos en microsegundos.
- **Devolver (Pila):** Registra una devolución de producto usando una pila.

### Pestaña Transferir
Permite transferir productos entre sucursales aplicando el algoritmo de Dijkstra sobre el grafo de conexiones. Sus modos son:
- **Modo Simulación (Hilos):** Simula la transferencia en tiempo real usando hilos, mostrando cada cola (Ingreso, Traspaso, Salida) de forma asíncrona.
- **Modo Paso a Paso:** Avanza manualmente la transferencia de cola en cola con el botón **Avanzar Siguiente Paso**.
El criterio de transferencia puede ser por **Tiempo** o por **Costo** de ruta.

### Pestaña Visualizar
Genera y muestra gráficos internos del sistema usando Graphviz:
- **Ver Grafo Sucursales:** Muestra la red de sucursales y sus conexiones (aristas con tiempo y costo).
- **Ver Árbol AVL / Árbol B / Árbol B+ / Tabla Hash:** Genera y despliega el gráfico de la estructura correspondiente para una sucursal específica.
- **Ver Colas (Sucursal):** Muestra el estado actual de las tres colas de una sucursal.
- **Exportar a SVG:** Exporta el último gráfico generado en formato vectorial `.svg`.

### Pestaña Sucursales
Permite la gestión completa de sucursales y sus rutas/conexiones (aristas):
- **Actualizar / Nueva / Editar / Eliminar Sucursal:** CRUD completo de sucursales.
- **Actualizar / Nueva / Editar / Eliminar Ruta:** CRUD completo de aristas del grafo.

### Formato de CSV — Sucursales
```
ID,Nombre,Ubicacion,TiempoIngreso,TiempoTraspaso,TiempoEntrega
1,Sucursal Central,Zona 1,3,2,4
```

### Formato de CSV — Rutas/Aristas
```
IDOrigen,IDDestino,Tiempo,Costo
1,2,5.0,15.0
```

### Formato de CSV — Productos
```
IDSucursal,Nombre,CodigoBarra,Categoria,FechaCaducidad,Marca,Precio,Stock
1,Leche Entera,7500001,Lacteos,2025-10-02,SuperMax,104.48,309
```

El programa valida automáticamente cada línea, omite duplicados y líneas malformadas, y registra todos los errores en el archivo `errors.log` normalmente la primera linea de encabezados va ahí.

### Comparación de búsquedas
El sistema ejecuta automáticamente `m = 25` búsquedas por repetición en `j = 5` repeticiones, midiendo y comparando los tiempos en microsegundos entre la búsqueda secuencial en lista enlazada O(n) y la búsqueda binaria en el árbol AVL O(log n).

### Visualización de estructuras
Genera archivos `.dot` y los convierte automáticamente a imágenes `.png` usando Graphviz. Los archivos se crean en el directorio de ejecución del proyecto, por ejemplo:

- `AvlSucursal_1.dot` / `AvlSucursal_1.png`
- `ArbolbSucursal_1.dot` / `ArbolbSucursal_1.png`
- `ArbolBMasSucursal_1.dot` / `ArbolBMasSucursal_1.png`
- `TablaHashSucursal_1.dot` / `TablaHashSucursal_1.png`
- `GrafoSucursales.dot` / `GrafoSucursales.png`
- `ColasSucursal_1.dot` / `ColasSucursal_1.png`

# Estructuras de Datos Implementadas
- Lista Enlazada (no ordenada) **Clave: Código de barra. **Propósito: Registro maestro, búsqueda secuencial y por coincidencias parciales.
- Lista Enlazada (ordenada) **Clave: Nombre. **Propósito: Pruebas comparativas de búsqueda.
- Árbol AVL **Clave: Nombre **Propósito: Búsqueda binaria por nombre, listado alfabético, comparación de rendimiento
- Árbol B **Clave: Fecha de caducidad **Propósito: Consultas por rango de fechas de expiración.
- Árbol B+ **Clave: Categoría **Propósito: Agrupación y recorrido eficiente por categoría con listas enlazadas en hojas.
- Tabla Hash **Clave: Código de barra **Propósito: Búsqueda O(1) por código, validación de duplicados, rehashing dinámico.

# Algoritmos Adicionales
- Dijkstra **Estructura: Grafo dirigido (Lista de adyacencia) **Propósito: Búsqueda de ruta más corta entre sucursales por tiempo o costo.
- Pila LIFO **Estructura: Pila enlazada genérica **Propósito: Registro y control de devoluciones de productos.
- Cola FIFO **Estructura: Cola enlazada genérica **Propósito: Simulación de colas de ingreso, traspaso y salida en transferencias.

# Autor
Mauricio Joel Gómez Barrios — 202031478
Estructura de Datos — USAC CUNOC — 2026
