package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class frmAnalizarCadena {
    private JFrame frame;
    private JTextField txtRutaAFD;
    private JTextField txtCadena;
    private JTextArea resultado;

    public frmAnalizarCadena() {
        inicializar();
    }

    private void inicializar() {
        frame = new JFrame("Analizar Cadena con AFD");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 10, 10));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelSuperior.setBackground(new Color(30, 30, 30));

        // Fila 1: Archivo
        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setBackground(new Color(30, 30, 30));
        JLabel lblArchivo = new JLabel("Archivo AFD:");
        lblArchivo.setForeground(Color.WHITE);
        txtRutaAFD = new JTextField();
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> seleccionarArchivoAFD());
        fila1.add(lblArchivo, BorderLayout.WEST);
        fila1.add(txtRutaAFD, BorderLayout.CENTER);
        fila1.add(btnBuscar, BorderLayout.EAST);

        // Fila 2: Cadena
        JPanel fila2 = new JPanel(new BorderLayout());
        fila2.setBackground(new Color(30, 30, 30));
        JLabel lblCadena = new JLabel("Cadena a analizar:");
        lblCadena.setForeground(Color.WHITE);
        txtCadena = new JTextField();
        fila2.add(lblCadena, BorderLayout.WEST);
        fila2.add(txtCadena, BorderLayout.CENTER);

        // Fila 3: Botón analizar
        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        fila3.setBackground(new Color(30, 30, 30));
        JButton btnAnalizar = new JButton("Analizar");
        btnAnalizar.addActionListener(e -> analizarCadena());
        fila3.add(btnAnalizar);

        panelSuperior.add(fila1);
        panelSuperior.add(fila2);
        panelSuperior.add(fila3);

        // Resultado
        resultado = new JTextArea();
        resultado.setEditable(false);
        resultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(resultado);

        frame.add(panelSuperior, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void seleccionarArchivoAFD() {
        JFileChooser fileChooser = new JFileChooser();
        int opcion = fileChooser.showOpenDialog(frame);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            txtRutaAFD.setText(archivo.getAbsolutePath());
        }
    }

    private void analizarCadena() {
        String ruta = txtRutaAFD.getText().trim();
        String cadena = txtCadena.getText();

        if (ruta.isEmpty() || cadena.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Debes ingresar la ruta del AFD y la cadena a analizar.");
            return;
        }

        AFD afd = AFD.leerDesdeBinario(ruta);  // Asegúrate de tener este método implementado
        if (afd == null) {
            resultado.setText("❌ No se pudo cargar el AFD desde el archivo:\n" + ruta);
            return;
        }

        boolean aceptada = afd.simular(cadena);
        resultado.setText("➡ Cadena: \"" + cadena + "\"\n");
        resultado.append("📁 Archivo: " + ruta + "\n\n");
        resultado.append("Resultado: " + (aceptada ? "✅ ACEPTADA" : "❌ RECHAZADA") + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(frmAnalizarCadena::new);
    }
}
