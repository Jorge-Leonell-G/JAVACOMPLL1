package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class frmAnalizadorLexico {
    private JFrame frame;
    private JTextArea inputArea;
    private JTable tablaTokens;
    private DefaultTableModel tableModel;
    private File archivoAFD;
    private JTextField txtRutaAFD;

    public frmAnalizadorLexico() {
        inicializar();
    }

    private void inicializar() {
        frame = new JFrame("Analizador Léxico");
        frame.setSize(850, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(30, 30, 30));
        frame.setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(new Color(30, 30, 30));

        JButton botonSeleccionarAFD = new JButton("Seleccionar archivo AFD");
        botonSeleccionarAFD.setBackground(new Color(60, 60, 60));
        botonSeleccionarAFD.setForeground(new Color(245, 245, 220));
        panelSuperior.add(botonSeleccionarAFD);

        txtRutaAFD = new JTextField(30);
        txtRutaAFD.setEditable(false);
        txtRutaAFD.setBackground(new Color(40, 40, 40));
        txtRutaAFD.setForeground(new Color(245, 245, 220));
        panelSuperior.add(txtRutaAFD);

        inputArea = new JTextArea(4, 40);
        inputArea.setBackground(new Color(40, 40, 40));
        inputArea.setForeground(new Color(245, 245, 220));
        inputArea.setBorder(BorderFactory.createTitledBorder("Cadena a analizar"));

        JButton botonAnalizar = new JButton("Analizar");
        botonAnalizar.setBackground(new Color(60, 60, 60));
        botonAnalizar.setForeground(new Color(245, 245, 220));

        tableModel = new DefaultTableModel(new String[]{"Lexema", "Token"}, 0);
        tablaTokens = new JTable(tableModel);
        JScrollPane scrollTabla = new JScrollPane(tablaTokens);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Resultado"));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(new Color(30, 30, 30));
        centro.setBorder(new EmptyBorder(10, 10, 10, 10));
        centro.add(inputArea);
        centro.add(Box.createVerticalStrut(10));
        centro.add(botonAnalizar);
        centro.add(Box.createVerticalStrut(10));
        centro.add(scrollTabla);

        frame.add(panelSuperior, BorderLayout.NORTH);
        frame.add(centro, BorderLayout.CENTER);

        botonSeleccionarAFD.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int opcion = chooser.showOpenDialog(frame);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                archivoAFD = chooser.getSelectedFile();
                txtRutaAFD.setText(archivoAFD.getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "✅ Archivo AFD cargado correctamente.");
            }
        });

        botonAnalizar.addActionListener(e -> {
            if (archivoAFD == null) {
                JOptionPane.showMessageDialog(frame, "Primero debes cargar un archivo AFD");
                return;
            }

            String cadena = inputArea.getText().trim();
            if (cadena.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Por favor, ingresa una cadena para analizar.");
                return;
            }

            try {
                AnalizLexico lexico = new AnalizLexico(cadena, archivoAFD.getAbsolutePath());
                List<Simbolo> simbolos = lexico.obtenerSimbolos();

                tableModel.setRowCount(0); 

                if (simbolos.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No se encontraron símbolos en la cadena.");
                    return;
                }

                for (Simbolo s : simbolos) {
                    tableModel.addRow(new Object[]{s.getLexema(), s.getToken()});
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al procesar el archivo AFD: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}
