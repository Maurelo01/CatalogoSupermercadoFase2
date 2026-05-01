package com.mycompany.catalogosupermercado.nodos;
import com.mycompany.catalogosupermercado.modelos.Producto;

public class NodoAVL
{
    private Producto producto;
    private NodoAVL izquierdo;
    private NodoAVL derecho;
    private int altura;

    public NodoAVL(Producto producto)
    {
        this.producto = producto;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1;
    }
    
    public Producto getProducto() 
    {
        return producto;
    }

    public void setProducto(Producto producto)
    {
        this.producto = producto;
    }

    public NodoAVL getIzquierdo()
    {
        return izquierdo;
    }

    public void setIzquierdo(NodoAVL izquierdo)
    {
        this.izquierdo = izquierdo;
    }

    public NodoAVL getDerecho()
    {
        return derecho;
    }

    public void setDerecho(NodoAVL derecho)
    {
        this.derecho = derecho;
    }

    public int getAltura()
    {
        return altura;
    }

    public void setAltura(int altura)
    {
        this.altura = altura;
    }
}