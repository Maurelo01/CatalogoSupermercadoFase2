package com.mycompany.catalogosupermercado;
import com.mycompany.catalogosupermercado.Nodos.NodoB;
import java.util.ArrayList;
import java.util.List;

public class ArbolB
{
    private NodoB raiz;
    private int d;

    public ArbolB()
    {
        this.d = 3;
        this.raiz = new NodoB(d, true);
    }

    public void mostrarEnRango(String fechaInicio, String fechaFin)
    {
        System.out.println("--- Productos con caducidad entre " + fechaInicio + " y " + fechaFin + " ---");
        mostrarEnRangoRecursivo(raiz, fechaInicio, fechaFin);
        System.out.println("------------------------------------------------------");
    }

    private void mostrarEnRangoRecursivo(NodoB nodo, String fechaInicio, String fechaFin)
    {
        if (nodo == null) return;
        int i = 0;
        for (i = 0; i < nodo.getFechas().size(); i++)
        {
            String fechaActual = nodo.getFechas().get(i);
            if (!nodo.isHoja() && fechaActual.compareToIgnoreCase(fechaInicio) > 0)
            {
                mostrarEnRangoRecursivo(nodo.getHijos().get(i), fechaInicio, fechaFin);
            }
            if (fechaActual.compareToIgnoreCase(fechaInicio) >= 0 && fechaActual.compareToIgnoreCase(fechaFin) <= 0)
            {
                for (Producto p : nodo.getListasProductos().get(i))
                {
                    System.out.println("- " + p.getNombre() + " | Expira: " + p.getFechaCaducidad() + " | Stock: " + p.getStock());
                }
            }
        }
        if (!nodo.isHoja() && nodo.getFechas().get(i - 1).compareToIgnoreCase(fechaFin) < 0)
        {
            mostrarEnRangoRecursivo(nodo.getHijos().get(i), fechaInicio, fechaFin);
        }
    }
    
    public void insertar(Producto producto)
    {
        String fecha = producto.getFechaCaducidad();
        NodoB existente = buscarPorFecha(raiz, fecha);
        if (existente != null)
        {
            int indice = existente.getFechas().indexOf(fecha);
            existente.getListasProductos().get(indice).add(producto);
            return;
        }
        if (raiz == null)
        {
            raiz = new NodoB(d, true);
            raiz.getFechas().add(fecha);
            List<Producto> nuevaLista = new ArrayList<>();
            nuevaLista.add(producto);
            raiz.getListasProductos().add(nuevaLista);
        }
        else
        {
            if (raiz.getFechas().size() == 2 * d - 1)
            {
                NodoB nuevaRaiz = new NodoB(d, false);
                nuevaRaiz.getHijos().add(raiz);
                dividirHijo(nuevaRaiz, 0, raiz);
                int i = 0;
                if (nuevaRaiz.getFechas().get(0).compareToIgnoreCase(fecha) < 0)
                {
                    i++;
                }
                insertarNoLleno(nuevaRaiz.getHijos().get(i), producto, fecha);
                raiz = nuevaRaiz;
            }
            else
            {
                insertarNoLleno(raiz, producto, fecha);
            }
        }
    }
    
    private void insertarNoLleno(NodoB nodo, Producto producto, String fecha)
    {
        int i = nodo.getFechas().size() - 1;
        if (nodo.isHoja())
        {
            while (i >= 0 && nodo.getFechas().get(i).compareToIgnoreCase(fecha) > 0)
            {
                i--;
            }
            nodo.getFechas().add(i + 1, fecha);
            List<Producto> nuevaLista = new ArrayList<>();
            nuevaLista.add(producto);
            nodo.getListasProductos().add(i + 1, nuevaLista);
        }
        else
        {
            while (i >= 0 && nodo.getFechas().get(i).compareToIgnoreCase(fecha) > 0)
            {
                i--;
            }
            i++;
            if (nodo.getHijos().get(i).getFechas().size() == 2 * d - 1)
            {
                dividirHijo(nodo, i, nodo.getHijos().get(i));
                if (nodo.getFechas().get(i).compareToIgnoreCase(fecha) < 0)
                {
                    i++;
                }
            }
            insertarNoLleno(nodo.getHijos().get(i), producto, fecha);
        }
    }

    private void dividirHijo(NodoB padre, int i, NodoB m)
    {
        NodoB n = new NodoB(d, m.isHoja());
        n.getFechas().addAll(m.getFechas().subList(d, m.getFechas().size()));
        n.getListasProductos().addAll(m.getListasProductos().subList(d, m.getListasProductos().size()));
        if (!m.isHoja())
        {
            n.getHijos().addAll(m.getHijos().subList(d, m.getHijos().size()));
        }
        padre.getHijos().add(i + 1, n);
        padre.getFechas().add(i, m.getFechas().get(d - 1));
        padre.getListasProductos().add(i, m.getListasProductos().get(d - 1));
        m.getFechas().subList(d - 1, m.getFechas().size()).clear();
        m.getListasProductos().subList(d - 1, m.getListasProductos().size()).clear();
        if (!m.isHoja())
        {
            m.getHijos().subList(d, m.getHijos().size()).clear();
        }
    }
    
    private NodoB buscarPorFecha(NodoB nodo, String fecha)
    {
        if (nodo == null)
        {
            return null;
        }
        
        int i = 0;
        while (i < nodo.getFechas().size() && fecha.compareToIgnoreCase(nodo.getFechas().get(i)) > 0)
        {
            i++;
        }
        if (i < nodo.getFechas().size() && fecha.equalsIgnoreCase(nodo.getFechas().get(i)))
        {
            return nodo;
        }
        if (nodo.isHoja())
        {
            return null;
        }
        return buscarPorFecha(nodo.getHijos().get(i), fecha);
    }

    public void eliminar(Producto producto)
    {
        if (raiz == null) return;
        String fecha = producto.getFechaCaducidad();
        NodoB nodo = buscarPorFecha(raiz, fecha);
        if (nodo != null)
        {
            int indice = nodo.getFechas().indexOf(fecha);
            if (indice != -1)
            {
                nodo.getListasProductos().get(indice).removeIf(p -> p.getCodigoBarra().equals(producto.getCodigoBarra()));
            }
        }
    }
}