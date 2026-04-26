package com.mycompany.catalogosupermercado;

import java.util.ArrayList;
import java.util.List;

public class Grafo
{
    private List<Sucursal> sucursales;
    public Grafo()
    {
        this.sucursales = new ArrayList<>();
    }
    
    public void agregarSucursal(Sucursal sucursal)
    {
        this.sucursales.add(sucursal);
    }
    
    public Sucursal buscarSucursal(int id)
    {
        for (Sucursal s : sucursales)
        {
            if (s.getId() == id)
            {
                return s;
            }
        }
        return null;
    }
    
    public void agregarArista(int idOrigen, int idDestino, double tiempo, double costo)
    {
        Sucursal origen = buscarSucursal(idOrigen);
        Sucursal destino = buscarSucursal(idDestino);
        if (origen != null && destino!= null)
        {
            origen.agregarArista(tiempo, costo, destino);
        }
    }
    
    public void encontrarRutaMasCorta(int idOrigen, int idDestino, boolean esPorTiempo)
    {
        Sucursal origen = buscarSucursal(idOrigen);
        Sucursal destino = buscarSucursal(idDestino);
        if (origen == null || destino == null)
        {
            System.out.println("Error: Sucursal de origen o destino no encontrados.");
            return;
        }
        int n = sucursales.size();
        double[] distanciasAcumuladas = new double[n];
        Sucursal[] predecesores = new Sucursal[n];
        boolean[] nodosPermanentes = new boolean[n];
        for (int i = 0; i < n; i++)
        {
            distanciasAcumuladas[i] = Double.MAX_VALUE;
            nodosPermanentes[i] = false;
            predecesores[i] = null;
        }
        int indiceOrigen = sucursales.indexOf(origen);
        distanciasAcumuladas[indiceOrigen] = 0.0;
        for (int i = 0; i < n; i++)
        {
            int m = -1;
            double minDistancia = Double.MAX_VALUE;
            for (int j = 0; j < n; j++)
            {
                if (!nodosPermanentes[j] && distanciasAcumuladas[j] < minDistancia)
                {
                    minDistancia = distanciasAcumuladas[j];
                    m = j;
                }
            }
            if (m == -1) break;
            nodosPermanentes[m] = true;
            Sucursal sucursalActual = sucursales.get(m);
            if (sucursalActual.getId() == idDestino) break;
            for (Arista aristas : sucursalActual.getAristas())
            {
                Sucursal cercano = aristas.getDestino();
                int cerca = sucursales.indexOf(cercano);
                if (!nodosPermanentes[cerca])
                {
                    double pesoArista = esPorTiempo ? aristas.getTiempo() : aristas.getCosto();
                    double nuevaDistancia = distanciasAcumuladas[m] + pesoArista;
                    if (nuevaDistancia < distanciasAcumuladas[cerca])
                    {
                        distanciasAcumuladas[cerca] = nuevaDistancia;
                        predecesores[cerca] = sucursalActual;
                    }
                }
            }
        }
        int indiceDestino = sucursales.indexOf(destino);
        if (distanciasAcumuladas[indiceDestino] == Double.MAX_VALUE)
        {
            System.out.println("No hay ruta posible entre " + origen.getNombre() + " y " + destino.getNombre());
            return;
        }
        Pila<Sucursal> ruta = new Pila<>();
        Sucursal actual = destino;
        while (actual != null)
        {
            ruta.push(actual);
            int idx = sucursales.indexOf(actual);
            actual = predecesores[idx];
        }
        System.out.println("--- RUTA ÓPTIMA POR " + (esPorTiempo ? "TIEMPO" : "COSTO") + " ---");
        System.out.println("De: " + origen.getNombre() + " ---> " + destino.getNombre());
        System.out.println("Total (" + (esPorTiempo ? "Tiempo" : "Costo") + "): " + distanciasAcumuladas[indiceDestino]);
        System.out.print("Camino a seguir: ");
        while (!ruta.estaVacia())
        {
            Sucursal s = ruta.pop();
            System.out.print(s.getNombre() + (ruta.estaVacia() ? "" : " ---> "));
        }
    }
}

