package com.mycompany.catalogosupermercado;
import com.mycompany.catalogosupermercado.Nodos.NodoAVL;

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
        NodoAVL x = y.getIzquierdo();
        NodoAVL T2 = x.getDerecho();
        x.setDerecho(y);
        y.setIzquierdo(T2);
        y.setAltura(max(altura(y.getIzquierdo()), altura(y.getDerecho())) + 1);
        x.setAltura(max(altura(x.getIzquierdo()), altura(x.getDerecho())) + 1);
        return x;
    }

    private NodoAVL rotacionIzquierda(NodoAVL x)
    {
        NodoAVL y = x.getDerecho();
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
        int comparacion = producto.getNombre().compareToIgnoreCase(nodo.getProducto().getNombre());
        if (comparacion < 0)
        {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), producto));
        }
        else if (comparacion > 0)
        {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), producto));
        }
        else
        {
            return nodo;
        }
        nodo.setAltura(1 + max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
        int balance = obtenerBalance(nodo);
        if (balance > 1 && producto.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getProducto().getNombre()) < 0)
        {
            return rotacionDerecha(nodo);
        }
        if (balance < -1 && producto.getNombre().compareToIgnoreCase(nodo.getDerecho().getProducto().getNombre()) > 0)
        {
            return rotacionIzquierda(nodo);
        }
        if (balance > 1 && producto.getNombre().compareToIgnoreCase(nodo.getIzquierdo().getProducto().getNombre()) > 0)
        {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
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

    private NodoAVL buscarRecursivo(NodoAVL raiz, String nombre)
    {
        if (raiz == null || raiz.getProducto().getNombre().equalsIgnoreCase(nombre))
        {
            return raiz;
        }
        if (raiz.getProducto().getNombre().compareToIgnoreCase(nombre) > 0)
        {
            return buscarRecursivo(raiz.getIzquierdo(), nombre);
        }
        return buscarRecursivo(raiz.getDerecho(), nombre);
    }

    public void listarEnOrden()
    {
        System.out.println("--- Catálogo Alfabético ---");
        listarInOrdenRecursivo(raiz);
        System.out.println("---------------------------");
    }

    private void listarInOrdenRecursivo(NodoAVL nodo)
    {
        if (nodo != null)
        {
            listarInOrdenRecursivo(nodo.getIzquierdo());
            System.out.println(nodo.getProducto().getNombre() + " | Stock: " + nodo.getProducto().getStock() + " | Q" + nodo.getProducto().getPrecio());
            listarInOrdenRecursivo(nodo.getDerecho());
        }
    }
    
    public int getContadorNodos()
    {
        return contadorNodos;
    }
}