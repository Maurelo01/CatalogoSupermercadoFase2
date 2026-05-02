package com.mycompany.catalogosupermercado.estructuras;

import com.mycompany.catalogosupermercado.modelos.Arista;
import com.mycompany.catalogosupermercado.modelos.Sucursal;
import com.mycompany.catalogosupermercado.modelos.Producto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public Pila<Sucursal> encontrarRutaMasCorta(int origenId, int destinoId, boolean esPorTiempo)
    {
        Sucursal origen = buscarSucursal(origenId);
        Sucursal destino = buscarSucursal(destinoId);
        if (origen == null || destino == null)
        {
            System.out.println("Error: Sucursal de origen o destino no encontrados.");
            return null;
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
            if (sucursalActual.getId() == destinoId) break;
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
            return null;
        }
        Pila<Sucursal> ruta = new Pila<>();
        Sucursal actual = destino;
        while (actual != null)
        {
            ruta.push(actual);
            int indice = sucursales.indexOf(actual);
            actual = predecesores[indice];
        }
        return ruta;
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
    
    public String realizarTransferencia(Producto producto, int origenId, int destinoId, boolean esPorTiempo)
    {
        StringBuilder consola = new StringBuilder();
        Pila<Sucursal> pilaRuta = encontrarRutaMasCorta(origenId, destinoId, esPorTiempo);
        if (pilaRuta == null || pilaRuta.estaVacia())
        {
            return ("Error: No se encontró ruta, transferencia no realizada");
            
        }
        List<Sucursal> ruta = new ArrayList<>();
        while (!pilaRuta.estaVacia())
        {
            ruta.add(pilaRuta.pop());
        }
        consola.append("-------------------------------------------------------\n");
        consola.append("Transfiriendo: ").append(producto.getNombre()).append("\n");
        consola.append("Ruta por ").append(esPorTiempo ? "Tiempo" : "Costo").append(": ");
        for (int i = 0; i < ruta.size(); i++)
        {
            consola.append(ruta.get(i).getNombre()).append(i == ruta.size() - 1 ? "" : " -> ");
        }
        consola.append("\n-------------------------------------------------------\n");
        for (int i = 0; i < ruta.size(); i++)
        {
            Sucursal sucursal = ruta.get(i);
            boolean esOrigen = (i == 0);
            boolean esDestinoFinal = (i == ruta.size() - 1);
            sucursal.getColaIngreso().encolar(producto);
            consola.append(sucursal.getNombre()).append(" - Cola de Ingreso: Procesando (").append(sucursal.getTiempoIngreso()).append( "s)\n");
            sucursal.getColaIngreso().desencolar();
            if (esDestinoFinal)
            {
                sucursal.getInventarioSucursal().agregarProducto(producto);
                consola.append("El producto se guardo exitosamente en el inventario de ").append(sucursal.getNombre()).append("\n");
            }
            else
            {
                if (!esOrigen)
                {
                    sucursal.getColaTraspaso().encolar(producto);
                    consola.append(sucursal.getNombre()).append(" - Cola de Traspaso: reenviando (").append(sucursal.getTiempoTraspaso()).append("s)\n");
                    sucursal.getColaTraspaso().desencolar();
                }
                sucursal.getColaSalida().encolar(producto);
                consola.append(sucursal.getNombre()).append(" - Cola de Salida: Enviando producto (").append(sucursal.getTiempoEntrega()).append("s)\n");
                sucursal.getColaSalida().desencolar();
                consola.append("Entrando a ").append(ruta.get(i + 1).getNombre()).append("\n");
            }
        }
        consola.append("¡¡¡TRANSFERENCIA COMPLETADA!!!");
        return consola.toString();
    }
    
    public void cargarCSVSucursales(String ruta)
    {
        try(BufferedReader br = new BufferedReader(new FileReader(ruta)); BufferedWriter errorLog = new BufferedWriter(new FileWriter("errors.log", true)))
        { 
            String linea;
            while ((linea = br.readLine()) != null)
            {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",", -1);
                if (partes.length < 6)
                {
                    errorLog.write("No se definó correctamente la sucursal: " + linea + "\n");
                    continue;
                }
                try
                {
                    int id = Integer.parseInt(partes[0].trim());
                    String nombre = partes[1].trim();
                    String ubicacion = partes[2].trim();
                    int tiempoIngreso = Integer.parseInt(partes[3].trim());
                    int tiempoTraspaso = Integer.parseInt(partes[4].trim());
                    int tiempoEntrega = Integer.parseInt(partes[5].trim());
                    if (buscarSucursal(id) == null)
                    {
                        agregarSucursal(new Sucursal(id, nombre, ubicacion, tiempoIngreso, tiempoTraspaso, tiempoEntrega));
                    }
                }
                catch(NumberFormatException e)
                {
                    errorLog.write("Error de número en la sucursal: " + linea + "\n");
                }
            }
            System.out.println(" Las sucursales fueron cargadas exitosamente.");
        }
        catch(IOException e)
        {
            System.err.println("Error de lectura de CSV de sucursales: " + e.getMessage());
        }
    }
    
    public void cargarCSVAristas(String ruta)
    {
        try(BufferedReader br = new BufferedReader(new FileReader(ruta)); BufferedWriter errorLog = new BufferedWriter(new FileWriter("errors.log", true)))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",", -1);
                if (partes.length < 4)
                {
                    errorLog.write("No se definó correctamente la arista: " + linea + "\n");
                    continue;
                }
                try
                {
                    int origenId = Integer.parseInt(partes[0].trim());
                    int destinoId = Integer.parseInt(partes[1].trim());
                    double tiempo = Double.parseDouble(partes[2].trim());
                    double costo = Double.parseDouble(partes[3].trim());
                    agregarArista(origenId, destinoId, tiempo, costo);
                }
                catch(NumberFormatException e)
                {
                    errorLog.write("Error de número en la arista: " + linea + "\n");
                }
            }
            System.out.println(" Las aristas fueron cargadas exitosamente.");
        }
        catch(IOException e)
        {
            System.err.println("Error de lectura de CSV de Aristas: " + e.getMessage());
        }
    }
    
    public void cargarCSVProductos(String ruta)
    {
        int cargados = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(ruta)); BufferedWriter errorLog = new BufferedWriter(new FileWriter("errors.log", true)))
        {
            String linea;
            while ((linea = br.readLine()) != null)
            {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",", -1);
                if (partes.length < 8)
                {
                    errorLog.write("No se definó correctamente el producto: " + linea + "\n");
                    continue;
                }
                try
                {
                    int sucursalId = Integer.parseInt(partes[0].trim());
                    Producto producto = new Producto(partes[1].trim(), partes[2].trim(), partes[3].trim(), partes[4].trim(), partes[5].trim(), Double.parseDouble(partes[6].trim()), Integer.parseInt(partes[7].trim()));
                    Sucursal sucursal = buscarSucursal(sucursalId);
                    if (sucursal != null)
                    {
                        if (sucursal.getInventarioSucursal().agregarProducto(producto))
                        {
                            cargados++;
                        }
                        else
                        {
                            errorLog.write("Codigo duplicado en la sucursal " + sucursalId + ": " + producto.getCodigoBarra() + "\n");
                        }
                    }
                    else 
                    {
                        errorLog.write("No existe la sucursal con id " + sucursalId + " para el producto: " + producto.getNombre() + "\n");
                    }
                }
                catch(NumberFormatException e)
                {
                    errorLog.write("Error de número en el producto: " + linea + "\n");
                }
            }
            System.out.println("Productos cargados. (" + cargados + " insertados)");
        }
        catch(IOException e)
        {
            System.err.println("Error de lectura de CSV de Productos: " + e.getMessage());
        }
    }
    
    public void crearGrafico(String nombreArchivo)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo)))
        {
            bw.write("digraph RedSucursales {\n");
            bw.write(" rankdir=LR;\n");
            bw.write(" node [shape=box, style=filled, fillcolor=white, fontname=\"Arial\"];\n");
            for (Sucursal s : sucursales)
            {
                bw.write("  " + s.getId() + " [label=\"" + s.getNombre() + "\\n" + s.getUbicacion() + "\"];\n");
                for (Arista a : s.getAristas())
                {
                    bw.write("  " + s.getId() + " -> " + a.getDestino().getId() + " [label=\"Tiempo:" + a.getTiempo() + " | Costo:" + a.getCosto() + "\", fontsize=12];\n");
                }
            }
            bw.write("}\n");
        }
        catch (IOException e)
        {
            System.err.println("Error al escribir el .dot del Grafo: " + e.getMessage());
        }
    }
}