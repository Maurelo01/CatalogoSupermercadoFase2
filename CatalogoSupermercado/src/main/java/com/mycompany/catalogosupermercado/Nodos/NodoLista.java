package com.mycompany.catalogosupermercado.Nodos;

import com.mycompany.catalogosupermercado.Producto;

public class NodoLista
{
    private Producto producto;
    private NodoLista siguiente;

    public NodoLista(Producto producto)
    {
        this.producto = producto;
        this.siguiente = null;
    }
    public Producto getProducto()
    {
        return producto;
    }
    public void setProducto(Producto producto)
    {
        this.producto = producto;
    }
    public NodoLista getSiguiente()
    {
        return siguiente;
    }
    public void setSiguiente(NodoLista siguiente)
    {
        this.siguiente = siguiente;
    }
}