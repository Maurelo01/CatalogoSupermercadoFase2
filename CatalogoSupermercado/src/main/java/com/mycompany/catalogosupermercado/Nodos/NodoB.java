package com.mycompany.catalogosupermercado.Nodos;

import com.mycompany.catalogosupermercado.Producto;
import java.util.ArrayList;
import java.util.List;

public class NodoB
{
    private int d;
    private boolean hoja;
    private List<String> fechas;
    private List<List<Producto>> listasProductos;
    private List<NodoB> hijos;

    public NodoB(int d, boolean hoja)
    {
        this.d = d;
        this.hoja = hoja;
        this.fechas = new ArrayList<>();
        this.listasProductos = new ArrayList<>();
        this.hijos = new ArrayList<>();
    }

    public int getD()
    {
        return d;
    }
    public boolean isHoja()
    {
        return hoja;
    }
    public void setHoja(boolean hoja)
    {
        this.hoja = hoja;
    }
    
    public List<String> getFechas()
    {
        return fechas;
    }
    public List<List<Producto>> getListasProductos()
    {
        return listasProductos;
    }
    public List<NodoB> getHijos()
    {
        return hijos;
    }
}