package com.mycompany.catalogosupermercado;
import com.mycompany.catalogosupermercado.Nodos.NodoSimple;

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