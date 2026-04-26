package com.mycompany.catalogosupermercado;

public class Arista
{
    private double tiempo;
    private double costo;
    private Sucursal destino;
    
    public Arista(double tiempo, double costo, Sucursal destino)
    {
        this.tiempo = tiempo;
        this.costo = costo;
        this.destino = destino;
    }
    
    public Sucursal getDestino()
    {
        return destino;
    }
   
    public void setDestino(Sucursal destino)
    {
        this.destino = destino;
    }
    
    public double getTiempo()
    {
        return tiempo;
    }
    
    public void setTiempo(double tiempo)
    {
       this.tiempo = tiempo;
    }
    
    public double getCosto()
    {
        return costo;
    }
    
    public void setCosto(double costo)
    {
        this.costo = costo;
    }
}
