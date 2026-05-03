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
    
    private class EstadoTransferencia
    {
        Producto producto;
        List<Sucursal> ruta;
        int indiceActual;
        int fase;
        Producto productoOriginal;
        boolean fueEliminadoEnOrigen;
        int stockAnterior;
        public EstadoTransferencia(Producto producto, List<Sucursal> ruta)
        {
            this.producto = producto;
            this.ruta = ruta;
            this.indiceActual = 0;
            this.fase = 1;
        }
    }
    
    private List<EstadoTransferencia> transferenciasActivas = new ArrayList<>();
    
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
    
    public boolean eliminarArista(int idOrigen, int idDestino) 
    {
        Sucursal origen = buscarSucursal(idOrigen);
        if (origen != null) 
        {
            return origen.getAristas().removeIf(arista -> arista.getDestino().getId() == idDestino);
        }
        return false;
    }
    
    public boolean editarArista(int idOrigen, int idDestinoOriginal, int idNuevoDestino, double nuevoTiempo, double nuevoCosto) 
    {
        Sucursal origen = buscarSucursal(idOrigen);
        Sucursal nuevoDestinoObj = buscarSucursal(idNuevoDestino);

        if (origen != null && nuevoDestinoObj != null) 
        {
            for (Arista arista : origen.getAristas()) 
            {
                if (arista.getDestino().getId() == idDestinoOriginal) 
                {
                    arista.setDestino(nuevoDestinoObj);
                    arista.setTiempo(nuevoTiempo);
                    arista.setCosto(nuevoCosto);
                    return true;
                }
            }
        }
        return false;
    }
    
    public String realizarTransferencia(Producto productoOrigen, int cantidad, int origenId, int destinoId, boolean esPorTiempo)
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
        Sucursal origen = buscarSucursal(origenId);
        int stockAnterior = productoOrigen.getStock();
        boolean productoEliminado = false;
        try 
        {
            int nuevoStock = stockAnterior - cantidad;
            if (nuevoStock == 0)
            {
                origen.getInventarioSucursal().eliminarProducto(productoOrigen.getCodigoBarra());
                productoEliminado = true;
            }
            else
            {
                productoOrigen.setStock(nuevoStock);
            }
            Producto producto = new Producto(productoOrigen.getNombre(), productoOrigen.getCodigoBarra(), productoOrigen.getCategoria(), productoOrigen.getFechaCaducidad(), productoOrigen.getMarca(), productoOrigen.getPrecio(), cantidad);
            producto.setEstado("En tránsito");
            consola.append("-------------------------------------------------------\n");
            consola.append("Transfiriendo: ").append(producto.getNombre()).append(" (Cantidad: ").append(cantidad).append(")\n");
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
                    Producto productoDestino = sucursal.getInventarioSucursal().buscarPorCodigo(producto.getCodigoBarra());
                    if (productoDestino != null)
                    {
                        productoDestino.setStock(productoDestino.getStock() + producto.getStock());
                        consola.append("El producto actualizó su stock en el inventario de ").append(sucursal.getNombre());
                    }
                    else
                    {
                        producto.setEstado("Disponible");
                        sucursal.getInventarioSucursal().agregarProducto(producto);
                        consola.append("El producto se guardo como nuevo en el inventario de ").append(sucursal.getNombre());
                    }
                    consola.append("\n");
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
        catch (Exception e)
        {
            consola.append("\nERROR DURANTE LA TRANSFERENCIA: ").append(e.getMessage()).append("\n");
            if (productoEliminado)
            {
                productoOrigen.setStock(stockAnterior);
                origen.getInventarioSucursal().agregarProducto(productoOrigen);
                consola.append("El producto fue restaurado completamente en la sucursal de origen.\n");
            }
            else
            {
                productoOrigen.setStock(stockAnterior);
                consola.append("El stock fue devuelto a la sucursal de origen.\n");
            }
            return consola.toString();
        }
    }
    
    public String iniciarTransferenciaPasoAPaso(Producto productoOrigen, int cantidad, int origenId, int destinoId, boolean esPorTiempo)
    {
        Pila<Sucursal> pilaRuta = encontrarRutaMasCorta(origenId, destinoId, esPorTiempo);
        if (pilaRuta == null || pilaRuta.estaVacia()) return "Error: No se encontró ruta para " + productoOrigen.getNombre() + ".";
        List<Sucursal> ruta = new ArrayList<>();
        while (!pilaRuta.estaVacia()) 
        {
            ruta.add(pilaRuta.pop());
        }
        Sucursal origen = buscarSucursal(origenId);
        int stockAnterior = productoOrigen.getStock();
        boolean productoEliminado = false;
        try
        {
            int nuevoStock = stockAnterior - cantidad;
            if (nuevoStock == 0)
            {
                origen.getInventarioSucursal().eliminarProducto(productoOrigen.getCodigoBarra());
                productoEliminado = true;
            }
            else
            {
                productoOrigen.setStock(nuevoStock);
            }
            Producto productoEnTransito = new Producto(productoOrigen.getNombre(), productoOrigen.getCodigoBarra(), productoOrigen.getCategoria(), productoOrigen.getFechaCaducidad(), productoOrigen.getMarca(), productoOrigen.getPrecio(), cantidad);
            productoEnTransito.setEstado("En tránsito");
            EstadoTransferencia nuevaTransferencia = new EstadoTransferencia(productoEnTransito, ruta);
            nuevaTransferencia.productoOriginal = productoOrigen;
            nuevaTransferencia.fueEliminadoEnOrigen = productoEliminado;
            nuevaTransferencia.stockAnterior = stockAnterior;
            ruta.get(0).getColaIngreso().encolar(productoEnTransito);
            transferenciasActivas.add(nuevaTransferencia);
            return "NUEVA TRANSFERENCIA INICIADA:\nEl producto '" + productoEnTransito.getNombre() + "' (Cantidad: " + cantidad + ") entró a la Cola de INGRESO de " + ruta.get(0).getNombre() + ".\n(Actualmente hay " + transferenciasActivas.size() + " transferencias en curso).";
        }
        catch(Exception e)
        {
            if (productoEliminado)
            {
                productoOrigen.setStock(stockAnterior);
                origen.getInventarioSucursal().agregarProducto(productoOrigen);
            }
            else
            {
                productoOrigen.setStock(stockAnterior);
            }
            return "Error al iniciar transferencia: " + e.getMessage() + "\n- Restablecimiento ejecutado en Origen.";
        }
    }

    public String avanzarPasoTransferencia()
    {
        if (transferenciasActivas.isEmpty()) return "No hay transferencias activas en este momento.";
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- REPORTE DE AVANCE (").append(transferenciasActivas.size()).append(" transferencias en curso) ---\n\n");
        List<EstadoTransferencia> finalizadas = new ArrayList<>();
        for (EstadoTransferencia transferencia : transferenciasActivas) 
        {
            try
            {
                Sucursal sucursalActual = transferencia.ruta.get(transferencia.indiceActual);
                boolean esOrigen = (transferencia.indiceActual == 0);
                boolean esDestinoFinal = (transferencia.indiceActual == transferencia.ruta.size() - 1);
                if (transferencia.fase == 1) 
                { 
                    sucursalActual.getColaIngreso().desencolar();
                    if (esDestinoFinal)
                    {
                        Producto productoDestino = sucursalActual.getInventarioSucursal().buscarPorCodigo(transferencia.producto.getCodigoBarra());
                        if (productoDestino != null)
                        {
                            productoDestino.setStock(productoDestino.getStock() + transferencia.producto.getStock());
                        }
                        else
                        {
                            transferencia.producto.setEstado("Disponible");
                            sucursalActual.getInventarioSucursal().agregarProducto(transferencia.producto);
                        }
                        reporte.append("LLEGADA '").append(transferencia.producto.getNombre()).append("' ingresó al inventario de ").append(sucursalActual.getNombre())
                               .append(" (Tardó ").append(sucursalActual.getTiempoIngreso()).append("s procesándose en ingreso).\n");
                        finalizadas.add(transferencia);
                    }
                    else
                    {
                        if (!esOrigen)
                        {
                            sucursalActual.getColaTraspaso().encolar(transferencia.producto);
                            transferencia.fase = 2;
                            reporte.append("AVANCE '").append(transferencia.producto.getNombre()).append("' pasó a TRASPASO en ").append(sucursalActual.getNombre())
                                   .append(" (Tardó ").append(sucursalActual.getTiempoIngreso()).append("s en el área de ingreso).\n");
                        }
                        else
                        {
                            sucursalActual.getColaSalida().encolar(transferencia.producto);
                            transferencia.fase = 3;
                            reporte.append("AVANCE '").append(transferencia.producto.getNombre()).append("' pasó a SALIDA en ").append(sucursalActual.getNombre())
                                   .append(" (Tardó ").append(sucursalActual.getTiempoIngreso()).append("s en el área de ingreso).\n");
                        }
                    }
                } 
                else if (transferencia.fase == 2) 
                {
                    sucursalActual.getColaTraspaso().desencolar();
                    sucursalActual.getColaSalida().encolar(transferencia.producto);
                    transferencia.fase = 3;
                    reporte.append("AVANCE '").append(transferencia.producto.getNombre()).append("' pasó a SALIDA en ").append(sucursalActual.getNombre())
                           .append(" (Tardó ").append(sucursalActual.getTiempoTraspaso()).append("s preparándose para el traspaso).\n");
                } 
                else if (transferencia.fase == 3) 
                {
                    sucursalActual.getColaSalida().desencolar();
                    Sucursal siguiente = transferencia.ruta.get(transferencia.indiceActual + 1);
                    double tiempoViaje = 0.0;
                    for (Arista arista : sucursalActual.getAristas())
                    {
                        if (arista.getDestino().getId() == siguiente.getId())
                        {
                            tiempoViaje = arista.getTiempo();
                            break;
                        }
                    }
                    transferencia.indiceActual++;
                    siguiente.getColaIngreso().encolar(transferencia.producto);
                    transferencia.fase = 1;
                    reporte.append("VIAJE '").append(transferencia.producto.getNombre()).append("' viajó a la sucursal ").append(siguiente.getNombre()).append(" (Ingreso).\n")
                           .append(" - Tiempo de despacho local: ").append(sucursalActual.getTiempoEntrega()).append("s\n")
                           .append(" - Tiempo de viaje en ruta: ").append(tiempoViaje).append("s\n");
                }
            }
            catch (Exception e)
            {
                reporte.append("\nERROR EN EL TRASPASO de '").append(transferencia.producto.getNombre()).append("']: ").append(e.getMessage()).append("\n");
                Sucursal origenOriginal = transferencia.ruta.get(0);
                if (transferencia.fueEliminadoEnOrigen)
                {
                    transferencia.productoOriginal.setStock(transferencia.stockAnterior);
                    origenOriginal.getInventarioSucursal().agregarProducto(transferencia.productoOriginal);
                    reporte.append("Producto '").append(transferencia.producto.getNombre()).append("' reingresado a la sucursal de origen.\n");
                }
                else
                {
                    transferencia.productoOriginal.setStock(transferencia.productoOriginal.getStock() + transferencia.producto.getStock());
                    reporte.append("Stock devuelto a la sucursal de origen.\n");
                }
                finalizadas.add(transferencia);
            }
        }
        transferenciasActivas.removeAll(finalizadas);
        if (transferenciasActivas.isEmpty() && !finalizadas.isEmpty())
        {
            reporte.append("\n¡Todas las transferencias en curso han llegado a sus destinos o fueron revertidas!");
        }
        return reporte.toString();
    }
    
    public boolean eliminarSucursal(int id) 
    {
        Sucursal aEliminar = buscarSucursal(id);
        if (aEliminar == null) return false;
        sucursales.remove(aEliminar);
        for (Sucursal s : sucursales) 
        {
            s.getAristas().removeIf(arista -> arista.getDestino().getId() == id);
        }
        return true;
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
    
    public List<Sucursal> getSucursales()
    {
        return this.sucursales;
    }
}