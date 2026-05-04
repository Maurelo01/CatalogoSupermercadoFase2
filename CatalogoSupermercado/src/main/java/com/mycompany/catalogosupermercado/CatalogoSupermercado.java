package com.mycompany.catalogosupermercado;
import com.mycompany.catalogosupermercado.frontend.PestañaInicio;
import javax.swing.SwingUtilities;

public class CatalogoSupermercado
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                PestañaInicio ventana = new PestañaInicio();
                ventana.setVisible(true);
            }
        });
    }
}