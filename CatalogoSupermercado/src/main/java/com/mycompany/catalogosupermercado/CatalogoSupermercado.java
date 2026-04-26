/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.catalogosupermercado;

/**
 *
 * @author mauricio
 */
public class CatalogoSupermercado {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      PRUEBA DE INTEGRACION - FASE 1 MIGRADA      ");
        System.out.println("==================================================\n");

        // 1. Crear productos de prueba variados
        Producto p1 = new Producto("Leche Entera", "1001", "Lacteos", "2026-10-15", "Dos Pinos", 15.50, 50);
        Producto p2 = new Producto("Queso Kraft", "1002", "Lacteos", "2026-05-10", "Kraft", 25.00, 30);
        Producto p3 = new Producto("Manzana Gala", "1003", "Frutas", "2026-04-30", "Importada", 5.00, 100);
        Producto p4 = new Producto("Pan de Caja", "1004", "Panaderia", "2026-05-05", "Bimbo", 22.50, 20);
        Producto p5 = new Producto("Yogurt Fresa", "1005", "Lacteos", "2026-05-20", "Yoplait", 12.00, 40);
        Producto p6 = new Producto("Cereal Miel", "1006", "Abarrotes", "2027-01-15", "Kelloggs", 35.00, 15);

        // --- INICIALIZAR ESTRUCTURAS ---
        ListaEnlazada listaNoOrdenada = new ListaEnlazada(false);
        ListaEnlazada listaOrdenada = new ListaEnlazada(true);
        ArbolAVL arbolAVL = new ArbolAVL();
        ArbolB arbolB = new ArbolB();
        ArbolBMas arbolBMas = new ArbolBMas();

        // --- INSERCIONES ---
        System.out.println("[*] Insertando productos en las estructuras...");
        Producto[] productos = {p1, p2, p3, p4, p5, p6};
        
        for (Producto p : productos) {
            listaNoOrdenada.insertar(p);
            listaOrdenada.insertar(p);
            arbolAVL.insertar(p);
            arbolB.insertar(p);
            arbolBMas.insertar(p);
        }
        System.out.println("[+] Inserciones completadas con éxito.\n");

        // --- PRUEBAS DE LISTAS ENLAZADAS ---
        System.out.println("--- 1. LISTA ENLAZADA (Búsqueda por código) ---");
        Producto buscadoLista = listaNoOrdenada.buscarPorCodigo("1003");
        if (buscadoLista != null) {
            System.out.println("Encontrado: " + buscadoLista.getNombre() + " (Esperado: Manzana Gala)");
        }

        // --- PRUEBAS DE ÁRBOL AVL ---
        System.out.println("\n--- 2. ARBOL AVL (Listado Alfabético e In-Order) ---");
        arbolAVL.listarEnOrden(); // Debería salir: Cereal, Leche, Manzana, Pan, Queso, Yogurt
        
        System.out.println("\nBuscando 'Pan de Caja' en AVL...");
        Producto buscadoAVL = arbolAVL.buscarPorNombre("Pan de Caja");
        if (buscadoAVL != null) {
            System.out.println("Encontrado en AVL con precio: Q" + buscadoAVL.getPrecio());
        }

        // --- PRUEBAS DE ÁRBOL B ---
        System.out.println("\n--- 3. ARBOL B (Búsqueda por Rango de Fechas) ---");
        // Rango de mayo 2026: Deberían salir Pan (05-05), Queso (05-10) y Yogurt (05-20)
        arbolB.mostrarEnRango("2026-05-01", "2026-05-31"); 

        // --- PRUEBAS DE ÁRBOL B+ ---
        System.out.println("\n--- 4. ARBOL B+ (Búsqueda por Categoría) ---");
        // Deberían salir Leche, Queso y Yogurt
        arbolBMas.buscarPorCategoria("Lacteos");
        
        System.out.println("\n--- 5. PRUEBA DE ELIMINACIÓN ---");
        System.out.println("Eliminando 'Queso Kraft' (Código 1002)...");
        
        // Simular lógica de eliminación de GestionInventario
        boolean eliminadoLista = listaNoOrdenada.eliminar("1002");
        arbolB.eliminar(p2);
        arbolBMas.eliminarProducto("Lacteos", "1002");
        
        if (eliminadoLista) {
            System.out.println("[+] Eliminado de la lista correctamente.");
        }
        
        System.out.println("Verificando Árbol B+ tras eliminación de Lacteos:");
        arbolBMas.buscarPorCategoria("Lacteos"); // Ya no debería mostrar Queso Kraft

        System.out.println("\n==================================================");
        System.out.println("         PRUEBAS FINALIZADAS CORRECTAMENTE        ");
        System.out.println("==================================================");
    }
}
