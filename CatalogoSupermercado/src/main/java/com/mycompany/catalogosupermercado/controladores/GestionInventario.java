package com.mycompany.catalogosupermercado.controladores;

import com.mycompany.catalogosupermercado.estructuras.ArbolB;
import com.mycompany.catalogosupermercado.estructuras.TablaHash;
import com.mycompany.catalogosupermercado.estructuras.ArbolAVL;
import com.mycompany.catalogosupermercado.estructuras.ArbolBMas;
import com.mycompany.catalogosupermercado.estructuras.ListaEnlazada;
import com.mycompany.catalogosupermercado.modelos.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestionInventario
{
    private ListaEnlazada listaNoOrdenada;
    private ListaEnlazada listaOrdenada;
    private ArbolAVL avlNombres;
    private ArbolB arbolFechas;
    private ArbolBMas arbolCategoria;
    private TablaHash tablaHash;

    public GestionInventario()
    {
        listaNoOrdenada = new ListaEnlazada(false);
        listaOrdenada = new ListaEnlazada(true);
        avlNombres = new ArbolAVL();
        arbolFechas = new ArbolB();
        arbolCategoria = new ArbolBMas();
        tablaHash = new TablaHash(503);
    }
    
    public void cargarDesdeCSV(String ruta)
    {
        File archivo = new File(ruta);
        int cargados = 0;
        int omitidos = 0;

        try (BufferedWriter log = new BufferedWriter(new FileWriter("errors.log", false)))
        {
            if (!archivo.exists() || !archivo.canRead())
            {
                System.err.println("Error: No se pudo abrir el archivo de datos.");
                log.write("Error: La ruta no existe o el archivo no se puede leer: " + ruta + "\n");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(archivo)))
            {
                String linea = br.readLine();
                if (linea == null) return;

                while ((linea = br.readLine()) != null)
                {
                    if (linea.trim().isEmpty()) continue;
                    String[] partes = linea.split(",", -1);
                    if (partes.length < 7)
                    {
                        log.write("Linea mal definida (faltan columnas): " + linea + "\n");
                        omitidos++;
                        continue;
                    }

                    try
                    {
                        String nombre = partes[0].trim();
                        String codigo = partes[1].trim();
                        String categoria = partes[2].trim();
                        String fecha = partes[3].trim();
                        String marca = partes[4].trim();
                        double precio = Double.parseDouble(partes[5].trim());
                        int stock = Integer.parseInt(partes[6].trim());
                        Producto producto = new Producto(nombre, codigo, categoria, fecha, marca, precio, stock);
                        if (agregarProducto(producto))
                        {
                            cargados++;
                        }
                        else
                        {
                            log.write("Linea ignorada, Codigo duplicado: " + linea + "\n");
                            omitidos++;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        log.write("Linea malformada (formato numerico): " + linea + " > " + e.getMessage() + "\n");
                        omitidos++;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Error de E/S: " + e.getMessage());
        }

        System.out.println("Carga finalizada: " + cargados + " insertados, " + omitidos + " omitidos.");
    }
    
    public boolean agregarProducto(Producto nuevo)
    {
        if (tablaHash.buscarPorCodigo(nuevo.getCodigoBarra()) != null)
        {
            return false;
        }
        boolean hashInsertado = false;
        boolean listaNoOrdInsertada = false;
        boolean listaOrdInsertada = false;
        boolean avlInsertado = false;
        boolean bInsertado = false;
        boolean bMasInsertado = false;
        try 
        {
            tablaHash.insertar(nuevo);
            hashInsertado = true;
            listaNoOrdenada.insertar(nuevo);
            listaNoOrdInsertada = true;
            listaOrdenada.insertar(nuevo);
            listaOrdInsertada = true;
            avlNombres.insertar(nuevo);
            avlInsertado = true;
            arbolFechas.insertar(nuevo);
            bInsertado = true;
            arbolCategoria.insertar(nuevo);
            bMasInsertado = true;
            return true;
        } 
        catch (Exception e) 
        {
            System.err.println("Error durante la inserción, rollback en proceso");
            if (bMasInsertado) arbolCategoria.eliminarProducto(nuevo.getCategoria(), nuevo.getCodigoBarra());
            if (bInsertado) arbolFechas.eliminar(nuevo);
            if (avlInsertado) avlNombres.eliminar(nuevo.getNombre());
            if (listaOrdInsertada) listaOrdenada.eliminar(nuevo.getCodigoBarra());
            if (listaNoOrdInsertada) listaNoOrdenada.eliminar(nuevo.getCodigoBarra());
            if (hashInsertado) tablaHash.eliminar(nuevo.getCodigoBarra());
            System.err.println("Rollback completo. Detalle del error: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(String codigo)
    {
        Producto producto = tablaHash.buscarPorCodigo(codigo);
        if (producto == null) return false;
        boolean avlEliminado = false;
        boolean bEliminado = false;
        boolean bMasEliminado = false;
        boolean hashEliminado = false;
        boolean listaNoOrdEliminada = false;
        boolean listaOrdEliminada = false;
        try 
        {
            avlNombres.eliminar(producto.getNombre());
            avlEliminado = true;
            arbolFechas.eliminar(producto);
            bEliminado = true;
            arbolCategoria.eliminarProducto(producto.getCategoria(), codigo);
            bMasEliminado = true;
            tablaHash.eliminar(codigo);
            hashEliminado = true;
            boolean e1 = listaNoOrdenada.eliminar(codigo);
            listaNoOrdEliminada = e1;
            boolean e2 = listaOrdenada.eliminar(codigo);
            listaOrdEliminada = e2;
            return e1 && e2;
            
        } 
        catch (Exception e) 
        {
            System.err.println("Error durante la eliminación, rollback en proceso");
            if (listaOrdEliminada) listaOrdenada.insertar(producto);
            if (listaNoOrdEliminada) listaNoOrdenada.insertar(producto);
            if (hashEliminado) tablaHash.insertar(producto);
            if (bMasEliminado) arbolCategoria.insertar(producto);
            if (bEliminado) arbolFechas.insertar(producto);
            if (avlEliminado) avlNombres.insertar(producto);
            System.err.println("Rollback completado, producto restaurado en memoria. Detalle: " + e.getMessage());
            return false;
        }
    }

    public Producto buscarPorNombreSecuencial(String nombre)
    {
        return listaNoOrdenada.buscarPorNombre(nombre);
    }

    public Producto buscarPorNombreAVL(String nombre)
    {
        return avlNombres.buscarPorNombre(nombre);
    }

    public Producto buscarPorCodigo(String codigo)
    {
        return tablaHash.buscarPorCodigo(codigo);
    }

    public String buscarPorCategoria(String categoria)
    {
        List<Producto> lista = arbolCategoria.buscarPorCategoria(categoria);
        if (lista.isEmpty())
        {
            return "No se encontraron productos en la categoría " + categoria + ".";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- PRODUCTOS EN LA CATEGORÍA: ").append(categoria).append(" ---\n\n");
        for (int i = 0; i < lista.size(); i++)
        {
            Producto producto = lista.get(i);
            sb.append((i + 1)).append(") ").append(producto.getNombre()).append(" (Código: ").append(producto.getCodigoBarra()).append(", Precio: Q").append(producto.getPrecio()).append(")\n");
        }
        
        return sb.toString();
    }

    public void buscarPorRangoFechas(String fechaInicio, String fechaFin)
    {
        arbolFechas.mostrarEnRango(fechaInicio, fechaFin);
    }
    
    public void listarPorNombre()
    {
        avlNombres.listarEnOrden();
    }
    
    public String compararBusquedas(int m, int j)
    {
        StringBuilder sb = new StringBuilder();
        int totalProductos = avlNombres.getContadorNodos();
        if (totalProductos == 0)
        {
            return "No hay productos cargados para realizar la comparación.\n";
        }
        if (m > totalProductos) m = totalProductos;
        List<String> nombres = new ArrayList<>();
        avlNombres.obtenerNombres(nombres, m);
        final String INEXISTENTE = "PRODUCTO_NO_EXISTE";
        long tiempoListaExitosa  = 0;
        long tiempoListaFallida  = 0;
        long tiempoAVLExitosa    = 0;
        long tiempoAVLFallida    = 0;
        for (int rep = 0; rep < j; rep++)
        {
            long t0 = System.nanoTime();
            for (String nombre : nombres)
            {
                listaNoOrdenada.buscarPorNombre(nombre);
            }
            tiempoListaExitosa += (System.nanoTime() - t0) / 1000.0;
            t0 = System.nanoTime();
            listaNoOrdenada.buscarPorNombre(INEXISTENTE);
            tiempoListaFallida += (System.nanoTime() - t0) / 1000.0;
            t0 = System.nanoTime();
            for (String nombre : nombres)
            {
                avlNombres.buscarPorNombre(nombre);
            }
            tiempoAVLExitosa += (System.nanoTime() - t0) / 1000.0;
            t0 = System.nanoTime();
            avlNombres.buscarPorNombre(INEXISTENTE);
            tiempoAVLFallida += (System.nanoTime() - t0) / 1000.0;
        }
        double denominador = (double) j * m;
        double promListaExito = tiempoListaExitosa / denominador;
        double promAVLExito = tiempoAVLExitosa / denominador;
        double promListaFallido = tiempoListaFallida / (double) j;
        double promAVLFallido = tiempoAVLFallida / (double) j;
        long totalLista = tiempoListaExitosa + tiempoListaFallida;
        long totalAVL = tiempoAVLExitosa + tiempoAVLFallida;
        String separador = "----------------------------------------------------------\n";
        
        sb.append("\n").append(separador);
        sb.append("--- Comparacion Lista Enlazada vs Arbol AVL ---\n");
        sb.append(String.format(" m = %d busquedas por repeticion | j = %d repeticiones%n%n", m, j));
        sb.append(separador);
        sb.append("--- Busquedas exitosas - ").append(m).append(" nombres reales ---\n");
        sb.append(separador);
        sb.append("  Tiempo total Lista (us): ").append(tiempoListaExitosa).append("\n");
        sb.append("  Tiempo total AVL   (us): ").append(tiempoAVLExitosa).append("\n");
        sb.append(String.format("  Promedio por busqueda Lista: %.4f us%n", promListaExito));
        sb.append(String.format("  Promedio por busqueda AVL:   %.4f us%n%n", promAVLExito));
        
        sb.append("--- Busquedas fallidas - nombre inexistente ---\n");
        sb.append(separador);
        sb.append("  Tiempo total Lista (us): ").append(tiempoListaFallida).append("\n");
        sb.append("  Tiempo total AVL   (us): ").append(tiempoAVLFallida).append("\n");
        sb.append(String.format("  Promedio por busqueda Lista: %.4f us%n", promListaFallido));
        sb.append(String.format("  Promedio por busqueda AVL:   %.4f us%n%n", promAVLFallido));
        
        sb.append("--- Totales ---\n");
        sb.append(separador);
        sb.append("  Lista Enlazada (us): ").append(totalLista).append("\n");
        sb.append("  Arbol AVL      (us): ").append(totalAVL).append("\n");
        sb.append("  Resultado: ").append(totalLista > totalAVL ? "AVL es mas rapido" : "Lista es mas rapida").append("\n");
        if (promAVLExito > 0.0)
        {
            sb.append(String.format("  Factor de mejora AVL/Lista: x%.2f%n%n", promListaExito / promAVLExito));
        }
        sb.append("--- Complejidades Teoricas (Big-O) ---\n");
        sb.append(separador);
        sb.append("  Lista Enlazada: O(n)\n");
        sb.append("  Arbol AVL:      O(log n)\n");
        sb.append(separador);
        return sb.toString();
    }
    
    private boolean generarPNG(String archivoDot, String archivoPng)
    {
        try
        {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", archivoDot, "-o", archivoPng);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proceso.getInputStream())))
            {
                String linea;
                while ((linea = br.readLine()) != null)
                {
                    System.out.println("[graphviz] " + linea);
                }
            }
            int resultado = proceso.waitFor();
            return resultado == 0;
        }
        catch (IOException e)
        {
            System.err.println("Error: Graphviz no está instalado o no se encuentra en el PATH.");
            System.err.println(" Detalle: " + e.getMessage());
            return false;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            System.err.println("La generación del PNG fue interrumpida.");
            return false;
        }
    }
 
    public void generarGraficoAVL()
    {
        final String dot = "arbol_avl.dot";
        final String png = "arbol_avl.png";
        avlNombres.crearGrafico(dot);
        System.out.println("Archivo '" + dot + "' generado correctamente.");
        if (generarPNG(dot, png))
        {
            System.out.println("Archivo '" + png + "' generado exitosamente.");
        }
        else
        {
            System.err.println("Error al generar el PNG.");
        }
    }
 
    public void generarGraficoB()
    {
        final String dot = "arbolB.dot";
        final String png = "arbolB.png";
        arbolFechas.crearGrafico(dot);
        System.out.println("Archivo '" + dot + "' generado correctamente.");
        if (generarPNG(dot, png))
        {
            System.out.println("Archivo '" + png + "' generado exitosamente.");
        }
        else
        {
            System.err.println("Error al generar el PNG.");
        }
    }
 
    public void generarGraficoBMas()
    {
        final String dot = "arbolBMas.dot";
        final String png = "arbolBMas.png";
        arbolCategoria.crearGrafico(dot);
        System.out.println("Archivo '" + dot + "' generado correctamente.");
        if (generarPNG(dot, png))
        {
            System.out.println("Archivo '" + png + "' generado exitosamente.");
        }
        else
        {
            System.err.println("Error al generar el PNG.");
        }
    }
    
    public void generarGraficoHash()
    {
        final String dot = "tabla_hash.dot";
        final String png = "tabla_hash.png";
        tablaHash.crearGrafico(dot);
        System.out.println("Archivo '" + dot + "' generado correctamente.");
        if (generarPNG(dot, png))
        {
            System.out.println("Archivo '" + png + "' generado exitosamente.");
        }
        else
        {
            System.err.println("Error al generar el PNG.");
        }
    }
    
    public ArbolAVL getArbolAVL()
    {
        return avlNombres;
    }

    public ArbolB getArbolB()
    {
        return arbolFechas;
    }

    public ArbolBMas getArbolBMas()
    {
        return arbolCategoria;
    }

    public TablaHash getTablaHash()
    {
        return tablaHash;
    }
}