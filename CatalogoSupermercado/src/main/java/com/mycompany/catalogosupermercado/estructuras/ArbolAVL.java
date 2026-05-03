package com.mycompany.catalogosupermercado.estructuras;
import com.mycompany.catalogosupermercado.modelos.Producto;
import com.mycompany.catalogosupermercado.nodos.NodoAVL;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ArbolAVL
{
    private NodoAVL raiz;
    private int contadorNodos;

    public ArbolAVL()
    {
        this.raiz = null;
        this.contadorNodos = 0;
    }

    private int altura(NodoAVL N)
    {
        if (N == null) return 0;
        return N.getAltura();
    }

    private int max(int a, int b)
    {
        return (a > b) ? a : b;
    }

    private int obtenerBalance(NodoAVL N)
    {
        if (N == null) return 0;
        return altura(N.getIzquierdo()) - altura(N.getDerecho());
    }

    private NodoAVL rotacionDerecha(NodoAVL y)
    {
        NodoAVL x  = y.getIzquierdo();
        NodoAVL T2 = x.getDerecho();
        x.setDerecho(y);
        y.setIzquierdo(T2);
        y.setAltura(max(altura(y.getIzquierdo()), altura(y.getDerecho())) + 1);
        x.setAltura(max(altura(x.getIzquierdo()), altura(x.getDerecho())) + 1);
        return x;
    }

    private NodoAVL rotacionIzquierda(NodoAVL x)
    {
        NodoAVL y  = x.getDerecho();
        NodoAVL T2 = y.getIzquierdo();
        y.setIzquierdo(x);
        x.setDerecho(T2);
        x.setAltura(max(altura(x.getIzquierdo()), altura(x.getDerecho())) + 1);
        y.setAltura(max(altura(y.getIzquierdo()), altura(y.getDerecho())) + 1);
        return y;
    }

    public void insertar(Producto producto)
    {
        raiz = insertarRecursivo(raiz, producto);
    }

    private NodoAVL insertarRecursivo(NodoAVL nodo, Producto producto)
    {
        if (nodo == null)
        {
            contadorNodos++;
            return new NodoAVL(producto);
        }

        int cmp = producto.getNombre().compareToIgnoreCase(nodo.getProducto().getNombre());
        if (cmp < 0)
        {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), producto));
        }
        else if (cmp > 0)
        {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), producto));
        }
        else
        {
            return nodo;
        }

        nodo.setAltura(1 + max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
        int balance = obtenerBalance(nodo);

        // LL
        if (balance > 1 && producto.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getProducto().getNombre()) < 0)
            return rotacionDerecha(nodo);
        // RR
        if (balance < -1 && producto.getNombre().compareToIgnoreCase(nodo.getDerecho().getProducto().getNombre()) > 0)
            return rotacionIzquierda(nodo);
        // LR
        if (balance > 1 && producto.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getProducto().getNombre()) > 0)
        {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        // RL
        if (balance < -1 && producto.getNombre().compareToIgnoreCase(nodo.getDerecho().getProducto().getNombre()) < 0)
        {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }
        return nodo;
    }

    public Producto buscarPorNombre(String nombre)
    {
        NodoAVL resultado = buscarRecursivo(raiz, nombre);
        return (resultado != null) ? resultado.getProducto() : null;
    }

    private NodoAVL buscarRecursivo(NodoAVL nodo, String nombre)
    {
        if (nodo == null || nodo.getProducto().getNombre().equalsIgnoreCase(nombre))
            return nodo;

        if (nodo.getProducto().getNombre().compareToIgnoreCase(nombre) > 0)
            return buscarRecursivo(nodo.getIzquierdo(), nombre);

        return buscarRecursivo(nodo.getDerecho(), nombre);
    }

    public String listarEnOrden()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Catálogo Alfabético ---\n");
        listarInOrdenRecursivo(raiz, sb);
        sb.append("---------------------------\n");
        return sb.toString();
    }

    private void listarInOrdenRecursivo(NodoAVL nodo, StringBuilder sb)
    {
        if (nodo != null)
        {
            listarInOrdenRecursivo(nodo.getIzquierdo(), sb);
            sb.append(nodo.getProducto().getNombre()).append(" | Stock: ").append(nodo.getProducto().getStock()).append(" | Q").append(nodo.getProducto().getPrecio()).append("\n");
            listarInOrdenRecursivo(nodo.getDerecho(), sb);
        }
    }
    
    public void obtenerNombres(List<String> lista, int limite)
    {
        recolectarNombres(raiz, lista, limite);
    }

    private void recolectarNombres(NodoAVL nodo, List<String> lista, int limite)
    {
        if (nodo == null || lista.size() >= limite) return;
        recolectarNombres(nodo.getIzquierdo(), lista, limite);
        if (lista.size() < limite)
        {
            lista.add(nodo.getProducto().getNombre());
        }
        recolectarNombres(nodo.getDerecho(), lista, limite);
    }
    
    public void eliminar(String nombre) 
    {
        raiz = eliminarRecursivo(raiz, nombre);
    }

    private NodoAVL eliminarRecursivo(NodoAVL nodo, String nombre)
    {
        if (nodo == null) return nodo;
        int comparacion = nombre.compareToIgnoreCase(nodo.getProducto().getNombre());
        if (comparacion < 0)
        {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), nombre));
        }
        else if (comparacion > 0)
        {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), nombre));
        }
        else
        {
            if ((nodo.getIzquierdo() == null) || (nodo.getDerecho() == null))
            {
                NodoAVL temp = (nodo.getIzquierdo() != null) ? nodo.getIzquierdo() : nodo.getDerecho();
                if (temp == null)
                {
                    temp = nodo;
                    nodo = null;
                }
                else
                { 
                    nodo = temp; 
                }
                contadorNodos--;
            }
            else
            {
                NodoAVL temp = nodoConValorMinimo(nodo.getDerecho());
                nodo.setProducto(temp.getProducto());
                nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), temp.getProducto().getNombre()));
            }
        }
        if (nodo == null) return nodo;
        nodo.setAltura(max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())) + 1);
        int balance = obtenerBalance(nodo);
        // LL
        if (balance > 1 && obtenerBalance(nodo.getIzquierdo()) >= 0)
        {
            return rotacionDerecha(nodo);
        }
        // LR
        if (balance > 1 && obtenerBalance(nodo.getIzquierdo()) < 0)
        {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        // RR
        if (balance < -1 && obtenerBalance(nodo.getDerecho()) <= 0)
        {
            return rotacionIzquierda(nodo);
        }
        // RL
        if (balance < -1 && obtenerBalance(nodo.getDerecho()) > 0)
        {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }

        return nodo;
    }

    private NodoAVL nodoConValorMinimo(NodoAVL nodo)
    {
        NodoAVL actual = nodo;
        while (actual.getIzquierdo() != null)
        {
            actual = actual.getIzquierdo();
        }
        return actual;
    }

    public int getContadorNodos()
    {
        return contadorNodos;
    }
    
     public void crearGrafico(String nombreArchivo)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo)))
        {
            bw.write("digraph G {\n");
            bw.write("  node [shape=record];\n");
            generarDot(raiz, bw);
            bw.write("}\n");
        }
        catch (IOException e)
        {
            System.err.println("Error al escribir el .dot del AVL: " + e.getMessage());
        }
    }
 
    private void generarDot(NodoAVL nodo, BufferedWriter bw) throws IOException
    {
        if (nodo == null) return;
 
        String nombre = escaparDot(nodo.getProducto().getNombre());
        String codigo = nodo.getProducto().getCodigoBarra();
 
        bw.write("  \"" + nombre + "\" [label=\"Nombre: " + nombre + "\\nCódigo: " + codigo + "\"];\n");
        if (nodo.getIzquierdo() != null)
        {
            String izqNombre = escaparDot(nodo.getIzquierdo().getProducto().getNombre());
            bw.write("  \"" + nombre + "\" -> \"" + izqNombre + "\" [label=\"L\"];\n");
            generarDot(nodo.getIzquierdo(), bw);
        }
        if (nodo.getDerecho() != null)
        {
            String derNombre = escaparDot(nodo.getDerecho().getProducto().getNombre());
            bw.write("  \"" + nombre + "\" -> \"" + derNombre + "\" [label=\"R\"];\n");
            generarDot(nodo.getDerecho(), bw);
        }
    }
 
    private String escaparDot(String texto)
    {
        return texto.replace("\"", "\\\"");
    }
}