package com.mycompany.catalogosupermercado.Nodos;
import com.mycompany.catalogosupermercado.Producto;
import java.util.ArrayList;
import java.util.List;

public class NodoBMas
{
    private boolean esHoja;
    private List<String> categorias;
    private List<List<Producto>> listasProductos;
    private List<NodoBMas> hijos;
    private NodoBMas siguiente;
    private NodoBMas padre;
    private int d;

    public NodoBMas(int d, boolean esHoja)
    {
        this.d = d;
        this.esHoja = esHoja;
        this.categorias = new ArrayList<>();
        this.listasProductos = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.siguiente = null;
        this.padre = null;
    }

    public boolean isEsHoja()
    {
        return esHoja;
    }
    public List<String> getCategorias()
    {
        return categorias;
    }
    public List<List<Producto>> getListasProductos()
    {
        return listasProductos;
    }
    public List<NodoBMas> getHijos()
    {
        return hijos;
    }
    public NodoBMas getSiguiente()
    {
        return siguiente;
    }
    public void setSiguiente(NodoBMas siguiente)
    {
        this.siguiente = siguiente;
    }
    public NodoBMas getPadre()
    {
        return padre;
    }
    public void setPadre(NodoBMas padre)
    {
        this.padre = padre;
    }
}