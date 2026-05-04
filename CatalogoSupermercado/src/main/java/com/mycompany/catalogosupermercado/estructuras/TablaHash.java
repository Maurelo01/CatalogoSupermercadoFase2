package com.mycompany.catalogosupermercado.estructuras;
import com.mycompany.catalogosupermercado.modelos.Producto;
import com.mycompany.catalogosupermercado.nodos.NodoLista;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TablaHash
{
    private NodoLista[] tabla;
    private int capacidad;
    private int elementos;
    private final double CAPACIDAD_LIMITE = 0.8;
    
    public TablaHash(int capacidadInicial)
    {
        this.capacidad = capacidadInicial;
        this.tabla = new NodoLista[capacidad];
        this.elementos = 0;
    }
    
    private int funcionHash(String clave)
    {
        long hash = 0;
        for (int i = 0; i < clave.length(); i++)
        {
            hash = (hash * 128 + clave.charAt(i)) % capacidad;
        }
        return (int) hash;
    }
    
    public void insertar(Producto producto)
    {
        if ((double) elementos / capacidad >= CAPACIDAD_LIMITE)
        {
            aumentarTamañoTabla();
        }
        String codigo = producto.getCodigoBarra();
        int indice = funcionHash(codigo);
        NodoLista nuevoNodo = new NodoLista(producto);
        if (tabla[indice] == null)
        {
            tabla[indice]= nuevoNodo;
        }
        else
        {
            nuevoNodo.setSiguiente(tabla[indice]);
            tabla[indice] = nuevoNodo;
        }
        elementos++;
    }
    
    private void aumentarTamañoTabla()
    {
        int nuevaCapacidad = capacidad * 2 + 1;
        NodoLista[] nuevaTabla = new NodoLista[nuevaCapacidad];
        int viejaCapacidad = capacidad;
        capacidad = nuevaCapacidad;
        for (int i = 0; i < viejaCapacidad; i++)
        {
            NodoLista actual = tabla[i];
            while (actual != null)
            {
                NodoLista siguienteOriginal = actual.getSiguiente();
                int nuevoIndice = funcionHash(actual.getProducto().getCodigoBarra());
                actual.setSiguiente(nuevaTabla[nuevoIndice]);
                nuevaTabla[nuevoIndice] = actual;
                actual = siguienteOriginal;
            }
        }
        tabla = nuevaTabla;
    }
    
    public Producto buscarPorCodigo(String codigo)
    {
        int indice = funcionHash(codigo);
        NodoLista actual = tabla[indice];
        while (actual != null)
        {
            if (actual.getProducto().getCodigoBarra().equals(codigo))
            {
                return actual.getProducto();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }
    
    public boolean eliminar(String codigo)
    {
        int indice = funcionHash(codigo);
        NodoLista actual = tabla[indice];
        NodoLista anterior = null;
        while (actual != null)
        {
            if (actual.getProducto().getCodigoBarra().equals(codigo))
            {
                if (anterior == null)
                {
                    tabla[indice] = actual.getSiguiente();
                }
                else
                {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                elementos--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    public void crearGrafico(String nombreArchivo)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo)))
        {
            bw.write("digraph TablaHash {\n");
            bw.write(" rankdir=LR;\n");
            bw.write(" node [shape=record, style=filled, fillcolor=grey];\n");
            for (int i = 0; i < capacidad; i++)
            {
                if (tabla[i] != null)
                {
                    bw.write("  slot_" + i + " [label=\"[" + i + "]\"];\n");
                    NodoLista actual = tabla[i];
                    int cont = 0;
                    String prevNodo = "slot_" + i;
                    while (actual != null)
                    {
                        String idNodo = "nodo_" + i + "_" + cont;
                        String codigo = actual.getProducto().getCodigoBarra();
                        String nombre = actual.getProducto().getNombre().replace("\"", "\\\"").replace("<", "\\<").replace(">", "\\>").replace("|", "\\|").replace("{", "\\{").replace("}", "\\}");
                        bw.write("  " + idNodo + " [label=\"{ " + codigo + " | " + nombre + " }\", fillcolor=white];\n");
                        bw.write("  " + prevNodo + " -> " + idNodo + ";\n");
                        prevNodo = idNodo;
                        actual = actual.getSiguiente();
                        cont++;
                    }
                }
            }
            bw.write("}\n");
        }
        catch (IOException e)
        {
            System.err.println("Error al escribir el .dot de la Tabla Hash: " + e.getMessage());
        }
    }
}
