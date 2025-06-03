package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clase que representa el menú principal del Analizador Léxico.
 * Este menú proporciona acceso a todas las funcionalidades de construcción y prueba
 * de autómatas: creación de AFNs básicos, operaciones como unión o concatenación,
 * conversión a AFD, análisis de cadenas y pruebas léxicas.
 * 
 * @author Akari Aguilera
 */
public class Menu {

    private AFN afnSeleccionado; // En esta versión no se usa directamente, pero está disponible si quieres guardar un AFN activo

    public Menu() {
        // =======================
        // Estilos personalizados
        // =======================
        Color fondo = new Color(30, 30, 30);             // Color de fondo general
        Color texto = new Color(245, 245, 220);          // Color del texto de botones y etiquetas
        Color boton = new Color(60, 60, 60);             // Color del fondo de los botones
        Font fuente = new Font("SansSerif", Font.BOLD, 14);

        // =======================
        // Ventana principal
        // =======================
        JFrame frame = new JFrame("Analizador léxico");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(fondo);

        // =======================
        // Título
        // =======================
        JLabel titulo = new JLabel("Analizador léxico");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(texto);
        titulo.setBorder(new EmptyBorder(15, 20, 15, 10));

        // =======================
        // Panel lateral izquierdo con botones
        // =======================
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBackground(fondo);
        panelLateral.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Opciones de funcionalidades
        String[] opciones = {
                "Básico",
                "Unir",
                "Concatenar",
                "Cerradura +",
                "Cerradura *",
                "Opcional",
                "ER -> AFN",
                "Unión para Analizador Léxico",
                "Convertir AFN a AFD",
                "Analizar una Cadena",
                "Probar Analizador Léxico",
                "Analizador LL1"
        };

        // =======================
        // Generar botones
        // =======================
        for (String textoBoton : opciones) {
            JButton botonMenu = new JButton(textoBoton);
            botonMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            botonMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            botonMenu.setBackground(boton);
            botonMenu.setForeground(texto);
            botonMenu.setFont(fuente);
            botonMenu.setFocusPainted(false);
            botonMenu.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Acción al hacer clic sobre cada botón
            botonMenu.addActionListener(e -> {
                switch (textoBoton) {
                    case "Básico" -> new frmAFNBasico();
                    case "Unir" -> new frmUnionAFNs();
                    case "Concatenar" -> new frmConcatenarAFNs();
                    case "Cerradura +" -> new frmCerraduraPositiva();
                    case "Cerradura *" -> new frmCerraduraKleene();
                    case "Opcional" -> new frmCerraduraOpcional();
                    case "ER -> AFN" -> new frmER_AFN();
                    case "Unión para Analizador Léxico" -> new frmUnionLexico();
                    case "Convertir AFN a AFD" -> new frmConvertirAFNaAFD();
                    case "Analizar una Cadena" -> new frmAnalizarCadena(); // Clase "Si" debe estar definida correctamente
                    case "Probar Analizador Léxico" -> new frmAnalizadorLexico();
                    case "Analizador LL1" -> new frmAnalisisLL1();
                    default -> JOptionPane.showMessageDialog(frame, "Funcionalidad no implementada aún.");
                }
            });

            panelLateral.add(botonMenu);
            panelLateral.add(Box.createVerticalStrut(10)); // Espacio entre botones
        }

        // Agregamos componentes al frame principal
        frame.add(titulo, BorderLayout.NORTH);
        frame.add(panelLateral, BorderLayout.WEST);

        // Mostrar ventana
        frame.setVisible(true);
    }

    /**
     * Punto de entrada de la aplicación. Crea y muestra el menú principal.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::new);
    }
}
