package com.mycompany.catalogosupermercado;
import com.mycompany.catalogosupermercado.Nodos.NodoLista;

public class ListaEnlazada 
{
    private NodoLista cabeza;
    private boolean esOrdenada;

    public ListaEnlazada(boolean esOrdenada)
    {
        this.cabeza = null;
        this.esOrdenada = esOrdenada;
    }

    public void insertar(Producto producto)
    {
        NodoLista nuevoNodo = new NodoLista(producto);
        if (cabeza == null)
        {
            cabeza = nuevoNodo;
            return;
        }

        if (esOrdenada)
        {
            if (producto.getNombre().compareToIgnoreCase(cabeza.getProducto().getNombre()) < 0)
            {
                nuevoNodo.setSiguiente(cabeza);
                cabeza = nuevoNodo;
            }
            else
            {
                NodoLista actual = cabeza;
                while (actual.getSiguiente() != null && actual.getSiguiente().getProducto().getNombre().compareToIgnoreCase(producto.getNombre()) < 0)
                {
                    actual = actual.getSiguiente();
                }
                nuevoNodo.setSiguiente(actual.getSiguiente());
                actual.setSiguiente(nuevoNodo);
            }
        }
        else
        {
            NodoLista actual = cabeza;
            while (actual.getSiguiente() != null)
            {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
    }

    public Producto buscarPorCodigo(String codigo)
    {
        NodoLista actual = cabeza;
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

    public Producto buscarPorNombre(String nombre)
    {
        NodoLista actual = cabeza;
        while (actual != null)
        {
            if (actual.getProducto().getNombre().equalsIgnoreCase(nombre))
            {
                return actual.getProducto();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean eliminar(String codigo)
    {
        if (cabeza == null)
        {
            return false;
        }
        if (cabeza.getProducto().getCodigoBarra().equals(codigo))
        {
            cabeza = cabeza.getSiguiente();
            return true;
        }
        NodoLista actual = cabeza;
        while (actual.getSiguiente() != null)
        {
            if (actual.getSiguiente().getProducto().getCodigoBarra().equals(codigo))
            {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
}