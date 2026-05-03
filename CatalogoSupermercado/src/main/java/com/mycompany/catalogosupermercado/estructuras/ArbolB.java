package com.mycompany.catalogosupermercado.estructuras;
import com.mycompany.catalogosupermercado.modelos.Producto;
import com.mycompany.catalogosupermercado.nodos.NodoB;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    public String mostrarEnRango(String fechaInicio, String fechaFin)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Productos con caducidad entre ").append(fechaInicio).append(" y ").append(fechaFin).append(" ---\n");
        mostrarEnRangoRecursivo(raiz, fechaInicio, fechaFin, sb);
        sb.append("------------------------------------------------------\n");
        return sb.toString();
    }

    private void mostrarEnRangoRecursivo(NodoB nodo, String fechaInicio, String fechaFin, StringBuilder sb)
    {
        if (nodo == null) return;
        int i = 0;
        for (i = 0; i < nodo.getFechas().size(); i++)
        {
            String fechaActual = nodo.getFechas().get(i);
            if (!nodo.isHoja() && fechaActual.compareToIgnoreCase(fechaInicio) > 0)
            {
                mostrarEnRangoRecursivo(nodo.getHijos().get(i), fechaInicio, fechaFin, sb);
            }
            if (fechaActual.compareToIgnoreCase(fechaInicio) >= 0 && fechaActual.compareToIgnoreCase(fechaFin) <= 0)
            {
                for (Producto producto : nodo.getListasProductos().get(i))
                {
                    sb.append(i+1).append(") ").append(producto.getNombre()).append(" | Expira: ").append(producto.getFechaCaducidad()).append(" | Stock: ").append(producto.getStock()).append("\n");
                }
            }
        }
        if (!nodo.isHoja() && nodo.getFechas().get(i - 1).compareToIgnoreCase(fechaFin) < 0)
        {
            mostrarEnRangoRecursivo(nodo.getHijos().get(i), fechaInicio, fechaFin, sb);
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
                if (nodo.getListasProductos().get(indice).isEmpty())
                {
                    eliminarClave(raiz, fecha);
                    if (raiz != null && raiz.getFechas().isEmpty())
                    {
                        if (raiz.isHoja())
                        {
                            raiz = null;
                        }
                        else
                        {
                            raiz = raiz.getHijos().get(0);
                        }
                    }
                }
            }
        }
    }
    
    private void eliminarClave(NodoB nodo, String fecha)
    {
        int i = 0;
        while (i < nodo.getFechas().size() && fecha.compareToIgnoreCase(nodo.getFechas().get(i)) > 0)
        {
            i++;
        }
        boolean existeEnNodo = (i < nodo.getFechas().size() && fecha.equalsIgnoreCase(nodo.getFechas().get(i)));
        if (existeEnNodo)
        {
            if (nodo.isHoja())
            {
                nodo.getFechas().remove(i);
                nodo.getListasProductos().remove(i);
            }
            else
            {
                NodoB hijoIzq = nodo.getHijos().get(i);
                NodoB hijoDer = nodo.getHijos().get(i + 1);
                if (hijoIzq.getFechas().size() >= d)
                {
                    NodoB pred = obtenerPredecesor(hijoIzq);
                    String fPred = pred.getFechas().get(pred.getFechas().size() - 1);
                    List<Producto> lPred = new ArrayList<>(pred.getListasProductos().get(pred.getListasProductos().size() - 1));
                    nodo.getFechas().set(i, fPred);
                    nodo.getListasProductos().set(i, lPred);
                    eliminarClave(hijoIzq, fPred);
                }
                else if (hijoDer.getFechas().size() >= d)
                {
                    NodoB suc = obtenerSucesor(hijoDer);
                    String fSuc = suc.getFechas().get(0);
                    List<Producto> lSuc = new ArrayList<>(suc.getListasProductos().get(0));
                    nodo.getFechas().set(i, fSuc);
                    nodo.getListasProductos().set(i, lSuc);
                    eliminarClave(hijoDer, fSuc);
                }
                else
                {
                    unir(nodo, i);
                    eliminarClave(hijoIzq, fecha);
                }
            }
        }
        else
        {
            if (nodo.isHoja())
            {
                return;
            }
            boolean esUltimoHijo = (i == nodo.getFechas().size());
            NodoB hijo = nodo.getHijos().get(i);
            if (hijo.getFechas().size() < d)
            {
                llenarHijo(nodo, i);
            }
            if (esUltimoHijo && i > nodo.getFechas().size())
            {
                eliminarClave(nodo.getHijos().get(i - 1), fecha);
            }
            else
            {
                eliminarClave(nodo.getHijos().get(i), fecha);
            }
        }
    }
    
    private NodoB obtenerPredecesor(NodoB nodo)
    {
        while (!nodo.isHoja())
        {
            nodo = nodo.getHijos().get(nodo.getFechas().size());
        }
        return nodo;
    }

    private NodoB obtenerSucesor(NodoB nodo)
    {
        while (!nodo.isHoja())
        {
            nodo = nodo.getHijos().get(0);
        }
        return nodo;
    }
    
    private void llenarHijo(NodoB padre, int indiceHijo)
    {
        if (indiceHijo != 0 && padre.getHijos().get(indiceHijo - 1).getFechas().size() >= d)
        {
            tomarDeAnterior(padre, indiceHijo);
        }
        else if (indiceHijo != padre.getFechas().size() && padre.getHijos().get(indiceHijo + 1).getFechas().size() >= d)
        {
            tomarDeSiguiente(padre, indiceHijo);
        }
        else
        {
            if (indiceHijo != padre.getFechas().size())
            {
                unir(padre, indiceHijo);
            }
            else
            {
                unir(padre, indiceHijo - 1);
            }
        }
    }
    
    private void tomarDeAnterior(NodoB padre, int indiceHijo)
    {
        NodoB hijo = padre.getHijos().get(indiceHijo);
        NodoB hermano = padre.getHijos().get(indiceHijo - 1);
        hijo.getFechas().add(0, padre.getFechas().get(indiceHijo - 1));
        hijo.getListasProductos().add(0, padre.getListasProductos().get(indiceHijo - 1));
        if (!hijo.isHoja())
        {
            hijo.getHijos().add(0, hermano.getHijos().remove(hermano.getHijos().size() - 1));
        }
        padre.getFechas().set(indiceHijo - 1, hermano.getFechas().remove(hermano.getFechas().size() - 1));
        padre.getListasProductos().set(indiceHijo - 1, hermano.getListasProductos().remove(hermano.getListasProductos().size() - 1));
    }
    
    private void tomarDeSiguiente(NodoB padre, int indiceHijo)
    {
        NodoB hijo = padre.getHijos().get(indiceHijo);
        NodoB hermano = padre.getHijos().get(indiceHijo + 1);
        hijo.getFechas().add(padre.getFechas().get(indiceHijo));
        hijo.getListasProductos().add(padre.getListasProductos().get(indiceHijo));
        if (!hijo.isHoja())
        {
            hijo.getHijos().add(hermano.getHijos().remove(0));
        }
        padre.getFechas().set(indiceHijo, hermano.getFechas().remove(0));
        padre.getListasProductos().set(indiceHijo, hermano.getListasProductos().remove(0));
    }
    
    private void unir(NodoB padre, int indiceHijo)
    {
        NodoB hijo = padre.getHijos().get(indiceHijo);
        NodoB hermano = padre.getHijos().get(indiceHijo + 1);
        hijo.getFechas().add(padre.getFechas().remove(indiceHijo));
        hijo.getListasProductos().add(padre.getListasProductos().remove(indiceHijo));
        hijo.getFechas().addAll(hermano.getFechas());
        hijo.getListasProductos().addAll(hermano.getListasProductos());
        if (!hijo.isHoja())
        {
            hijo.getHijos().addAll(hermano.getHijos());
        }
        padre.getHijos().remove(indiceHijo + 1);
    }
    
    
    public void crearGrafico(String nombreArchivo)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo)))
        {
            bw.write("digraph ArbolB {\n");
            bw.write("  node [shape=record];\n");
            if (raiz != null)
            {
                int[] contador = {0};
                generarDot(raiz, bw, contador, 0);
            }
            bw.write("}\n");
        }
        catch (IOException e)
        {
            System.err.println("Error al escribir el .dot del Árbol B: " + e.getMessage());
        }
    }
    
    private void generarDot(NodoB nodo, BufferedWriter bw, int[] contador, int idActual) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodo.getFechas().size(); i++)
        {
            String fecha = nodo.getFechas().get(i);
            List<Producto> productos = nodo.getListasProductos().get(i);
            StringBuilder nombres = new StringBuilder();
            for (int j = 0; j < productos.size(); j++) 
            {
                nombres.append(productos.get(j).getNombre());
                if (j < productos.size() - 1) nombres.append(", ");
            }
            sb.append(fecha).append("\\n(").append(nombres).append(")");
            if (i < nodo.getFechas().size() - 1)
            {
                sb.append(" | ");
            }
        }
        bw.write("  node" + idActual + " [label=\"" + sb + "\"];\n");
        if (!nodo.isHoja())
        {
            for (int i = 0; i <= nodo.getFechas().size(); i++)
            {
                int idHijo = ++contador[0];
                bw.write("  node" + idActual + " -> node" + idHijo + ";\n");
                generarDot(nodo.getHijos().get(i), bw, contador, idHijo);
            }
        }
    }
}