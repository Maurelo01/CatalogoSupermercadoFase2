package com.mycompany.catalogosupermercado.estructuras;

import com.mycompany.catalogosupermercado.modelos.Producto;
import com.mycompany.catalogosupermercado.nodos.NodoBMas;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArbolBMas
{
    private NodoBMas raiz;
    private int d;

    public ArbolBMas()
    {
        this.d = 3;
        this.raiz = null;
    }

    public void insertar(Producto producto)
    {
        String categoria = producto.getCategoria();
        if (raiz == null)
        {
            raiz = new NodoBMas(d, true);
            raiz.getCategorias().add(categoria);
            List<Producto> lista = new ArrayList<>();
            lista.add(producto);
            raiz.getListasProductos().add(lista);
            return;
        }
        NodoBMas hoja = buscarHoja(raiz, categoria);
        int indice = hoja.getCategorias().indexOf(categoria);
        if (indice != -1)
        {
            hoja.getListasProductos().get(indice).add(producto);
            return;
        }
        int i = hoja.getCategorias().size() - 1;
        while (i >= 0 && hoja.getCategorias().get(i).compareToIgnoreCase(categoria) > 0)
        {
            i--;
        }
        hoja.getCategorias().add(i + 1, categoria);
        List<Producto> nuevaLista = new ArrayList<>();
        nuevaLista.add(producto);
        hoja.getListasProductos().add(i + 1, nuevaLista);
        if (hoja.getCategorias().size() == 2 * d)
        {
            dividirNodo(hoja);
        }
    }

    private void dividirNodo(NodoBMas nodo)
    {
        int mitad = d;
        if (nodo.isEsHoja())
        {
            NodoBMas nuevoNodo = new NodoBMas(d, true);
            nuevoNodo.getCategorias().addAll(nodo.getCategorias().subList(mitad, nodo.getCategorias().size()));
            nuevoNodo.getListasProductos().addAll(nodo.getListasProductos().subList(mitad, nodo.getListasProductos().size()));
            nodo.getCategorias().subList(mitad, nodo.getCategorias().size()).clear();
            nodo.getListasProductos().subList(mitad, nodo.getListasProductos().size()).clear();
            nuevoNodo.setSiguiente(nodo.getSiguiente());
            nodo.setSiguiente(nuevoNodo);

            insertarEnPadre(nodo, nuevoNodo.getCategorias().get(0), nuevoNodo);
        }
        else
        {
            NodoBMas nuevoNodo = new NodoBMas(d, false);
            String clavePromedio = nodo.getCategorias().get(mitad);
            nuevoNodo.getCategorias().addAll(nodo.getCategorias().subList(mitad + 1, nodo.getCategorias().size()));
            nuevoNodo.getHijos().addAll(nodo.getHijos().subList(mitad + 1, nodo.getHijos().size()));
            for (NodoBMas hijo : nuevoNodo.getHijos())
            {
                hijo.setPadre(nuevoNodo);
            }
            nodo.getCategorias().subList(mitad, nodo.getCategorias().size()).clear();
            nodo.getHijos().subList(mitad + 1, nodo.getHijos().size()).clear();
            insertarEnPadre(nodo, clavePromedio, nuevoNodo);
        }
    }

    private void insertarEnPadre(NodoBMas izq, String clave, NodoBMas der)
    {
        if (izq == raiz)
        {
            NodoBMas nuevaRaiz = new NodoBMas(d, false);
            nuevaRaiz.getCategorias().add(clave);
            nuevaRaiz.getHijos().add(izq);
            nuevaRaiz.getHijos().add(der);
            izq.setPadre(nuevaRaiz);
            der.setPadre(nuevaRaiz);
            raiz = nuevaRaiz;
            return;
        }
        NodoBMas padre = izq.getPadre();
        int i = padre.getCategorias().size() - 1;
        while (i >= 0 && padre.getCategorias().get(i).compareToIgnoreCase(clave) > 0)
        {
            i--;
        }
        padre.getCategorias().add(i + 1, clave);
        padre.getHijos().add(i + 2, der);
        der.setPadre(padre);
        if (padre.getCategorias().size() == 2 * d)
        {
            dividirNodo(padre);
        }
    }

    private NodoBMas buscarHoja(NodoBMas nodo, String categoria)
    {
        if (nodo.isEsHoja()) return nodo;
        int i = 0;
        while (i < nodo.getCategorias().size() && categoria.compareToIgnoreCase(nodo.getCategorias().get(i)) >= 0)
        {
            i++;
        }
        return buscarHoja(nodo.getHijos().get(i), categoria);
    }
    
    public List<Producto> buscarPorCategoria(String categoria)
    {
        List<Producto> productos = new ArrayList<>();
        if (raiz == null) return productos;
        NodoBMas hojaActual = buscarHoja(raiz, categoria);
        while (hojaActual != null)
        {
            int indice = hojaActual.getCategorias().indexOf(categoria);
            if (indice != -1)
            {
                productos.addAll(hojaActual.getListasProductos().get(indice));
            }
            else if (!productos.isEmpty()) break;
            hojaActual = hojaActual.getSiguiente();
        }
        return productos;
    }

    public void eliminarProducto(String categoria, String codigoBarra)
    {
        if (raiz == null) return;
        NodoBMas hoja = buscarHoja(raiz, categoria);
        int indice = hoja.getCategorias().indexOf(categoria);
        if (indice != -1)
        {
            hoja.getListasProductos().get(indice).removeIf(producto -> producto.getCodigoBarra().equals(codigoBarra));
            if (hoja.getListasProductos().get(indice).isEmpty())
            {
                String claveEliminada = hoja.getCategorias().remove(indice);
                hoja.getListasProductos().remove(indice);
                
                if (indice == 0 && !hoja.getCategorias().isEmpty())
                {
                    actualizarClave(raiz, claveEliminada, hoja.getCategorias().get(0));
                }
                
                if (hoja != raiz && hoja.getCategorias().size() < d - 1)
                {
                    rebalancearEliminacion(hoja);
                }
                if (hoja == raiz && raiz.getCategorias().isEmpty())
                {
                    raiz = null;
                }
            }
        }
    }
    
    private void actualizarClave(NodoBMas nodo, String viejaClave, String nuevaClave)
    {
        if (nodo == null || nodo.isEsHoja()) return;
        
        for (int i = 0; i < nodo.getCategorias().size(); i++)
        {
            if (nodo.getCategorias().get(i).equals(viejaClave))
            {
                nodo.getCategorias().set(i, nuevaClave);
                return; 
            }
        }
        for (NodoBMas hijo : nodo.getHijos())
        {
            actualizarClave(hijo, viejaClave, nuevaClave);
        }
    }
    
    private void rebalancearEliminacion(NodoBMas nodo)
    {
        if (nodo == raiz) return;
        NodoBMas padre = nodo.getPadre();
        int indiceEnPadre = padre.getHijos().indexOf(nodo);
        if (indiceEnPadre > 0)
        {
            NodoBMas hermanoIzq = padre.getHijos().get(indiceEnPadre - 1);
            if (hermanoIzq.getCategorias().size() >= d)
            {
                String categoriaPrestada = hermanoIzq.getCategorias().remove(hermanoIzq.getCategorias().size() - 1);
                List<Producto> listaPrestada = hermanoIzq.getListasProductos().remove(hermanoIzq.getListasProductos().size() - 1);
                nodo.getCategorias().add(0, categoriaPrestada);
                nodo.getListasProductos().add(0, listaPrestada);
                padre.getCategorias().set(indiceEnPadre - 1, nodo.getCategorias().get(0));
                return;
            }
        }
        if (indiceEnPadre < padre.getHijos().size() - 1)
        {
            NodoBMas hermanoDer = padre.getHijos().get(indiceEnPadre + 1);
            if (hermanoDer.getCategorias().size() >= d)
            {
                String categoriaPrestada = hermanoDer.getCategorias().remove(0);
                List<Producto> listaPrestada = hermanoDer.getListasProductos().remove(0);
                nodo.getCategorias().add(categoriaPrestada);
                nodo.getListasProductos().add(listaPrestada);
                padre.getCategorias().set(indiceEnPadre, hermanoDer.getCategorias().get(0));
                return;
            }
        }
        if (indiceEnPadre > 0)
        {
            NodoBMas hermanoIzq = padre.getHijos().get(indiceEnPadre - 1);
            hermanoIzq.getCategorias().addAll(nodo.getCategorias());
            hermanoIzq.getListasProductos().addAll(nodo.getListasProductos());
            hermanoIzq.setSiguiente(nodo.getSiguiente());
            padre.getCategorias().remove(indiceEnPadre - 1);
            padre.getHijos().remove(indiceEnPadre);
        }
        else
        {
            NodoBMas hermanoDer = padre.getHijos().get(indiceEnPadre + 1);
            nodo.getCategorias().addAll(hermanoDer.getCategorias());
            nodo.getListasProductos().addAll(hermanoDer.getListasProductos());
            nodo.setSiguiente(hermanoDer.getSiguiente());
            padre.getCategorias().remove(indiceEnPadre);
            padre.getHijos().remove(indiceEnPadre + 1);
        }
    }
    
    public void crearGrafico(String nombreArchivo)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo)))
        {
            bw.write("digraph ArbolBMas {\n");
            bw.write(" rankdir=TB;\n");
            bw.write(" node [shape=plaintext];\n");
            if (raiz != null)
            {
                int[] contador = {0};
                Map<NodoBMas, Integer> mapaIds = new HashMap<>();
                generarDot(raiz, bw, contador, 0, mapaIds);
                NodoBMas hoja = raiz;
                while (!hoja.isEsHoja()) hoja = hoja.getHijos().get(0);
                while (hoja != null && hoja.getSiguiente() != null)
                {
                    Integer idActual = mapaIds.get(hoja);
                    Integer idSig    = mapaIds.get(hoja.getSiguiente());
                    if (idActual != null && idSig != null)
                    {
                        bw.write("  node" + idActual + " -> node" + idSig + " [color=red, constraint=false];\n");
                    }
                    hoja = hoja.getSiguiente();
                }
            }
            bw.write("}\n");
        }
        catch (IOException e)
        {
            System.err.println("Error al escribir el .dot del Árbol B+: " + e.getMessage());
        }
    }
    
    private void generarDot(NodoBMas nodo, BufferedWriter bw, int[] contador, int idActual, Map<NodoBMas, Integer> mapaIds) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" node").append(idActual).append(" [label=<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\"><TR>");
        for (int i = 0; i < nodo.getCategorias().size(); i++)
        {
            String clave = nodo.getCategorias().get(i);
            sb.append("<TD>");
            if (nodo.isEsHoja())
            {
                sb.append("<B>").append(escribirHtml(clave)).append("</B><BR/>");
                List<Producto> productos = nodo.getListasProductos().get(i);
                sb.append("<FONT POINT-SIZE=\"10\">");
                for (int j = 0; j < productos.size(); j++) 
                {
                    sb.append(escribirHtml(productos.get(j).getNombre()));
                    if (j < productos.size() - 1) sb.append("<BR/>");
                }
                sb.append("</FONT>");
            }
            else
            {
                sb.append(escribirHtml(clave));
            }
            sb.append("</TD>");
        }
        sb.append("</TR></TABLE>>];\n");
        bw.write(sb.toString());
        if (!nodo.isEsHoja())
        {
            for (int i = 0; i < nodo.getHijos().size(); i++)
            {
                NodoBMas hijo = nodo.getHijos().get(i);
                if (hijo != null)
                {
                    int idHijo = ++contador[0];
                    bw.write("  node" + idActual + " -> node" + idHijo + ";\n");
                    generarDot(hijo, bw, contador, idHijo, mapaIds);
                }
            }
        }
        else
        {
            mapaIds.put(nodo, idActual);
        }
    }
 
    private String escribirHtml(String texto)
    {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}