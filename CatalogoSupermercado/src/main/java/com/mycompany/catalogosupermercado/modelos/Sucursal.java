package com.mycompany.catalogosupermercado.modelos;

import com.mycompany.catalogosupermercado.estructuras.Cola;
import com.mycompany.catalogosupermercado.controladores.GestionInventario;
import com.mycompany.catalogosupermercado.modelos.Producto;
import java.util.ArrayList;
import java.util.List;

public class Sucursal
{
    private int id;
    private String nombre;
    private String ubicacion;
    private GestionInventario inventarioSucursal;
    private Cola<Producto> colaIngreso;
    private Cola<Producto> colaTraspaso;
    private Cola<Producto> colaSalida;
    private List<Arista> aristas;
    private int tiempoIngreso;
    private int tiempoTraspaso;
    private int tiempoEntrega;
    
    public Sucursal(int id, String nombre, String ubicacion, int tiempoIngreso, int tiempoTraspaso, int tiempoEntrega)
    {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tiempoIngreso = tiempoIngreso;
        this.tiempoTraspaso = tiempoTraspaso;
        this.tiempoEntrega = tiempoEntrega;
        this.inventarioSucursal = new GestionInventario();
        this.colaIngreso = new Cola<>();
        this.colaTraspaso = new Cola<>();
        this.colaSalida = new Cola<>();
        this.aristas = new ArrayList<>();
    }
    
    public void agregarArista(double tiempo, double costo, Sucursal destino)
    {
        this.aristas.add(new Arista(tiempo, costo, destino));
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    
    public String getUbicacion()
    {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion)
    {
        this.ubicacion = ubicacion;
    }
    
    public int getTiempoIngreso()
    {
        return tiempoIngreso;
    }
    
    public void setTiempoIngreso(int tiempoIngreso)
    {
        this.tiempoIngreso = tiempoIngreso;
    }
    
    public int getTiempoTraspaso()
    {
        return tiempoTraspaso;
    }
    
    public void setTiempoTraspaso(int tiempoTraspaso)
    {
        this.tiempoTraspaso = tiempoTraspaso;
    }
    
    public int getTiempoEntrega()
    {
        return tiempoEntrega;
    }
    
    public void setTiempoDespacho(int tiempoDespacho)
    {
        this.tiempoEntrega = tiempoDespacho;
    }
    
    public GestionInventario getInventarioSucursal()
    {
        return inventarioSucursal;
    }
    
    public Cola<Producto> getColaIngreso()
    {
        return colaIngreso;
    }
    
    public Cola<Producto> getColaTraspaso()
    {
        return colaTraspaso;
    }
    
    public Cola<Producto> getColaSalida()
    {
        return colaSalida;
    }
    
    public List<Arista> getAristas()
    {
        return aristas;
    }
}
