package com.mycompany.catalogosupermercado;

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

    public GestionInventario()
    {
        listaNoOrdenada = new ListaEnlazada(false);
        listaOrdenada   = new ListaEnlazada(true);
        avlNombres      = new ArbolAVL();
        arbolFechas     = new ArbolB();
        arbolCategoria  = new ArbolBMas();
    }
    
    public void cargarDesdeCSV(String ruta)
    {
        File archivo = new File(ruta);
        int cargados = 0, omitidos = 0;

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
        if (listaNoOrdenada.buscarPorCodigo(nuevo.getCodigoBarra()) != null)
        {
            return false;
        }
        listaNoOrdenada.insertar(nuevo);
        listaOrdenada.insertar(nuevo);
        avlNombres.insertar(nuevo);
        arbolFechas.insertar(nuevo);
        arbolCategoria.insertar(nuevo);
        return true;
    }

    public boolean eliminarProducto(String codigo)
    {
        Producto prod = listaNoOrdenada.buscarPorCodigo(codigo);
        if (prod == null) return false;
        arbolFechas.eliminar(prod);
        arbolCategoria.eliminarProducto(prod.getCategoria(), codigo);
        boolean e1 = listaNoOrdenada.eliminar(codigo);
        boolean e2 = listaOrdenada.eliminar(codigo);
        return e1 && e2;
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
        return listaNoOrdenada.buscarPorCodigo(codigo);
    }

    public void buscarPorCategoria(String categoria)
    {
        arbolCategoria.buscarPorCategoria(categoria);
    }

    public void buscarPorRangoFechas(String fechaInicio, String fechaFin)
    {
        arbolFechas.mostrarEnRango(fechaInicio, fechaFin);
    }
    
    public void listarPorNombre()
    {
        avlNombres.listarEnOrden();
    }
    
    public void compararBusquedas(int m, int j)
    {
        int totalProductos = avlNombres.getContadorNodos();
        if (totalProductos == 0)
        {
            System.out.println("No hay productos cargados para realizar la comparación.");
            return;
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
            tiempoListaExitosa += (System.nanoTime() - t0) / 1000;
            t0 = System.nanoTime();
            listaNoOrdenada.buscarPorNombre(INEXISTENTE);
            tiempoListaFallida += (System.nanoTime() - t0) / 1000;
            t0 = System.nanoTime();
            for (String nombre : nombres)
            {
                avlNombres.buscarPorNombre(nombre);
            }
            tiempoAVLExitosa += (System.nanoTime() - t0) / 1000;
            t0 = System.nanoTime();
            avlNombres.buscarPorNombre(INEXISTENTE);
            tiempoAVLFallida += (System.nanoTime() - t0) / 1000;
        }

        double denominador = (double) j * m;
        double promListaExito = tiempoListaExitosa / denominador;
        double promAVLExito = tiempoAVLExitosa / denominador;
        double promListaFallido = tiempoListaFallida / (double) j;
        double promAVLFallido = tiempoAVLFallida / (double) j;
        long totalLista = tiempoListaExitosa + tiempoListaFallida;
        long totalAVL = tiempoAVLExitosa + tiempoAVLFallida;
        String separador = "-".repeat(58);
        System.out.println();
        System.out.println(separador);
        System.out.println("--- Comparacion Lista Enlazada - Arbol AVL ---");
        System.out.printf(" m = %d busquedas por repeticion | j = %d repeticiones%n%n", m, j);
        System.out.println(separador);
        System.out.println("--- Busquedas exitosas - " + m + " nombres reales ---");
        System.out.println(separador);
        System.out.println("  Tiempo total Lista (us): " + tiempoListaExitosa);
        System.out.println("  Tiempo total AVL   (us): " + tiempoAVLExitosa);
        System.out.printf ("  Promedio por busqueda Lista: %.4f us%n", promListaExito);
        System.out.printf ("  Promedio por busqueda AVL:   %.4f us%n%n", promAVLExito);
        System.out.println("--- Busquedas fallidas - nombre inexistente ---");
        System.out.println(separador);
        System.out.println("  Tiempo total Lista (us): " + tiempoListaFallida);
        System.out.println("  Tiempo total AVL   (us): " + tiempoAVLFallida);
        System.out.printf ("  Promedio por busqueda Lista: %.4f us%n", promListaFallido);
        System.out.printf ("  Promedio por busqueda AVL:   %.4f us%n%n", promAVLFallido);
        System.out.println("--- Totales ---");
        System.out.println(separador);
        System.out.println("  Lista Enlazada (us): " + totalLista);
        System.out.println("  Arbol AVL      (us): " + totalAVL);
        System.out.println("  Resultado: " + (totalLista > totalAVL ? "AVL es mas rapido" : "Lista es mas rapida"));
        if (promAVLExito > 0.0)
        {
            System.out.printf("  Factor de mejora AVL/Lista exitosa: x%.2f%n%n", promListaExito / promAVLExito);
        }
        System.out.println("--- Complejidades teoricas ---");
        System.out.println(separador);
        System.out.println("  Lista Enlazada: O(n)");
        System.out.println("  Arbol AVL: O(log n)");
        System.out.println(separador);
    }
}