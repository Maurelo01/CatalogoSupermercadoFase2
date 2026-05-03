package com.mycompany.catalogosupermercado.estructuras;
import com.mycompany.catalogosupermercado.nodos.NodoSimple;
import java.util.List;

public class Cola<T>
{
    private NodoSimple<T> frente;
    private NodoSimple<T> finalCola;
    private int tamaño;

    public Cola()
    {
        this.frente = null;
        this.finalCola = null;
        this.tamaño = 0;
    }

    public void encolar(T dato)
    {
        NodoSimple<T> nuevoNodo = new NodoSimple<>(dato);
        if (estaVacia())
        {
            frente = nuevoNodo;
            finalCola = nuevoNodo;
        }
        else
        {
            finalCola.setSiguiente(nuevoNodo);
            finalCola = nuevoNodo;
        }
        tamaño++;
    }
    
    public T desencolar()
    {
        if (estaVacia())
        {
            return null;
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null)
        {
            finalCola = null;
        }
        tamaño--;
        return dato;
    }
    
    public List<T> obtenerElementos()
    {
        List<T> lista = new java.util.ArrayList<>();
        NodoSimple<T> actual = frente;
        while (actual != null)
        {
            lista.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        return lista;
    }
    
    public T peek()
    {
        if (estaVacia()) return null;
        return frente.getDato();
    }

    public boolean estaVacia()
    {
        return frente == null;
    }
    
    public int getTamaño()
    {
        return tamaño;
    }
}