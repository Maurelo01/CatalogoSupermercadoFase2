package com.mycompany.catalogosupermercado;

import java.io.*;
import java.util.Scanner;

public class CatalogoSupermercado
{
    private static void mostrarMenu()
    {
        System.out.println("-----------------------------------------");
        System.out.println("   SISTEMA DE CATALOGO DE SUPERMERCADO   ");
        System.out.println("-----------------------------------------");
        System.out.println(" 1) Cargar catalogo desde CSV");
        System.out.println(" 2) Agregar producto manualmente");
        System.out.println(" 3) Buscar por nombre - Lista Enlazada");
        System.out.println(" 4) Buscar por nombre - Arbol AVL");
        System.out.println(" 5) Buscar por codigo - Lista Enlazada");
        System.out.println(" 6) Buscar por rango de fechas - Arbol B");
        System.out.println(" 7) Buscar por categoria - Arbol B+");
        System.out.println(" 8) Eliminar producto");
        System.out.println(" 9) Listar productos en orden alfabetico (AVL in-order)");
        System.out.println("10) Comparar busquedas: Lista - AVL");
        System.out.println("11) Ver errores de carga");
        System.out.println("12) Generar grafico del Arbol B  (.dot / .png)");
        System.out.println("13) Generar grafico del Arbol B+ (.dot / .png)");
        System.out.println("14) Generar grafico del Arbol AVL (.dot / .png)");
        System.out.println("15) Salir");
        System.out.print("Seleccione una opcion: ");
    }

    public static void main(String[] args)
    {
        GestionInventario inventario = new GestionInventario();
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do
        {
            mostrarMenu();

            try
            {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Opcion no valida.");
                continue;
            }

            switch (opcion)
            {
                case 1:
                {
                    System.out.print("Ingrese la ruta del archivo CSV: ");
                    String ruta = scanner.nextLine().trim();
                    System.out.println("Cargando datos...");
                    long inicio = System.currentTimeMillis();
                    inventario.cargarDesdeCSV(ruta);
                    long duracion = System.currentTimeMillis() - inicio;
                    System.out.println("Carga completada en " + duracion + " ms.");
                    System.out.println("Revise errors.log para obtener detalles.");
                    break;
                }
                case 2:
                {
                    System.out.println(" NUEVO PRODUCTO ");
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine().trim();
                    System.out.print("Codigo de barras: ");
                    String codigo = scanner.nextLine().trim();
                    System.out.print("Categoria: ");
                    String categoria = scanner.nextLine().trim();
                    System.out.print("Fecha de caducidad (Año-Mes-Día): ");
                    String fecha = scanner.nextLine().trim();
                    System.out.print("Marca: ");
                    String marca = scanner.nextLine().trim();
                    double precio = 0;
                    int stock = 0;
                    try
                    {
                        System.out.print("Precio: Q");
                        precio = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Stock: ");
                        stock = Integer.parseInt(scanner.nextLine().trim());
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Error: precio o stock con formato incorrecto.");
                        break;
                    }
                    Producto nuevo = new Producto(nombre, codigo, categoria, fecha, marca, precio, stock);
                    long inicio = System.nanoTime();
                    boolean exito = inventario.agregarProducto(nuevo);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    System.out.println(exito ? "Producto agregado correctamente a todas las estructuras." : "Error: El producto con codigo " + codigo + " ya existe.");
                    System.out.println("Tiempo de insercion: " + duracion + " microsegundos.");
                    break;
                }
                case 3:
                {
                    System.out.print("Ingrese el nombre exacto del producto: ");
                    String nombre = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    Producto producto = inventario.buscarPorNombreSecuencial(nombre);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    if (producto != null)
                    {
                        System.out.println(" - Lista Enlazada -");
                        System.out.println("PRODUCTO ENCONTRADO:");
                        System.out.println(" - Codigo: "  + producto.getCodigoBarra());
                        System.out.println(" - Precio: Q" + producto.getPrecio());
                        System.out.println(" - Stock: "   + producto.getStock());
                    }
                    else System.out.println("Producto no encontrado.");
                    System.out.println("Tiempo de busqueda secuencial: " + duracion + " us.");
                    break;
                }
                case 4:
                {
                    System.out.print("Ingrese el nombre exacto del producto: ");
                    String nombre = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    Producto producto = inventario.buscarPorNombreAVL(nombre);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    if (producto != null)
                    {
                        System.out.println(" - Arbol AVL -");
                        System.out.println("PRODUCTO ENCONTRADO:");
                        System.out.println(" - Codigo: "  + producto.getCodigoBarra());
                        System.out.println(" - Precio: Q" + producto.getPrecio());
                        System.out.println(" - Stock: "   + producto.getStock());
                    }
                    else System.out.println("Producto no encontrado.");
                    System.out.println("Tiempo de busqueda en AVL: " + duracion + " us.");
                    break;
                }
                case 5:
                {
                    System.out.print("Ingrese el codigo de barras: ");
                    String codigo = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    Producto producto = inventario.buscarPorCodigo(codigo);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    if (producto != null)
                    {
                        System.out.println(" - Lista Enlazada -");
                        System.out.println("PRODUCTO ENCONTRADO:");
                        System.out.println(" - Nombre: "    + producto.getNombre());
                        System.out.println(" - Categoria: " + producto.getCategoria());
                        System.out.println(" - Precio: Q"   + producto.getPrecio());
                        System.out.println(" - Stock: "     + producto.getStock());
                    }
                    else System.out.println("Producto no encontrado en la Lista Enlazada.");
                    System.out.println("Tiempo de busqueda por codigo: " + duracion + " us.");
                    break;
                }
                case 6:
                {
                    System.out.print("Ingrese la fecha de inicio (Año-Mes-Día): ");
                    String fechaInicio = scanner.nextLine().trim();
                    System.out.print("Ingrese la fecha de fin   (Año-Mes-Día): ");
                    String fechaFin = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    inventario.buscarPorRangoFechas(fechaInicio, fechaFin);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    System.out.println("Tiempo de busqueda en Arbol B: " + duracion + " us.");
                    break;
                }
                case 7:
                {
                    System.out.print("Ingrese la categoria a buscar: ");
                    String categoria = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    inventario.buscarPorCategoria(categoria);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    System.out.println("Tiempo de busqueda en Arbol B+: " + duracion + " us.");
                    break;
                }
                case 8:
                {
                    System.out.print("Ingrese el codigo de barras a eliminar: ");
                    String codigo = scanner.nextLine().trim();
                    long inicio = System.nanoTime();
                    boolean eliminado = inventario.eliminarProducto(codigo);
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    System.out.println(eliminado ? "Producto eliminado de todas las estructuras." : "No se encontro el producto con ese codigo.");
                    System.out.println("Tiempo de eliminacion: " + duracion + " us.");
                    break;
                }
                case 9:
                {
                    long inicio = System.nanoTime();
                    inventario.listarPorNombre();
                    long duracion = (System.nanoTime() - inicio) / 1000;
                    System.out.println("Tiempo de recorrido in-order: " + duracion + " us.");
                    break;
                }
                case 10:
                {
                    int m = 0, j = 0;
                    try
                    {
                        System.out.print("Numero de busquedas por repeticion (m): ");
                        m = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Numero de repeticiones (j): ");
                        j = Integer.parseInt(scanner.nextLine().trim());
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Error: ingrese numeros enteros validos.");
                        break;
                    }
                    if (m <= 0 || j <= 0) { System.out.println("m y j deben ser positivos."); break; }
                    inventario.compararBusquedas(m, j);
                    break;
                }
                case 11:
                {
                    System.out.println("ULTIMOS ERRORES REGISTRADOS");
                    File logFile = new File("errors.log");
                    if (!logFile.exists())
                    {
                        System.out.println("El archivo errors.log no existe o esta vacio.");
                        break;
                    }
                    try (BufferedReader br = new BufferedReader(new FileReader(logFile)))
                    {
                        java.util.Deque<String> ultimas = new java.util.ArrayDeque<>();
                        String linea;
                        while ((linea = br.readLine()) != null)
                        {
                            if (ultimas.size() == 15) ultimas.pollFirst();
                            ultimas.addLast(linea);
                        }
                        for (String l : ultimas) System.out.println(l);
                    }
                    catch (IOException e)
                    {
                        System.out.println("Error al leer errors.log: " + e.getMessage());
                    }
                    break;
                }
                case 12:
                    inventario.generarGraficoB();
                    break;
                case 13:
                    inventario.generarGraficoBMas();
                    break;
                case 14:
                    inventario.generarGraficoAVL();
                    break;
                case 15:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
        }
        while (opcion != 15);

        scanner.close();
    }
}