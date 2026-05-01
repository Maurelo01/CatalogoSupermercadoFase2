package com.mycompany.catalogosupermercado.estructuras;
import com.mycompany.catalogosupermercado.nodos.NodoSimple;

public class Pila<T>
{
    private NodoSimple<T> cima;
    private int tamaño;

    public Pila()
    {
        this.cima = null;
        this.tamaño = 0;
    }

    public void push(T dato)
    {
        NodoSimple<T> nuevoNodo = new NodoSimple<>(dato);
        nuevoNodo.setSiguiente(cima);
        cima = nuevoNodo;
        tamaño++;
    }

    public T pop()
    {
        if (estaVacia())
        {
            return null;
        }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamaño--;
        return dato;
    }

    public T peek()
    {
        if (estaVacia()) return null;
        return cima.getDato();
    }

    public boolean estaVacia()
    {
        return cima == null;
    }

    public int getTamaño()
    {
        return tamaño;
    }
}