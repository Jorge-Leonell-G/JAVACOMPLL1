package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class frmConvertirAFNaAFD {
    private JFrame frame;
    private JComboBox<String> comboAFNs;
    private JTextField txtIdAFD;
    private JTable tablaAFD;
    private DefaultTableModel tableModel;

    public frmConvertirAFNaAFD() {
        inicializar();
    }

    private void inicializar() {
        frame = new JFrame("Convertir AFN a AFD");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(30, 30, 30));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        comboAFNs = new JComboBox<>();
        cargarAFNsEnCombo();

        txtIdAFD = new JTextField(5);
        JButton btnConvertir = new JButton("Convertir y guardar AFD");
        JButton btnRefrescar = new JButton("Refrescar AFNs");

        JLabel lbl1 = new JLabel("AFN a convertir:");
        JLabel lbl2 = new JLabel("ID del AFD:");
        lbl1.setForeground(Color.WHITE);
        lbl2.setForeground(Color.WHITE);

        topPanel.add(lbl1);
        topPanel.add(comboAFNs);
        topPanel.add(lbl2);
        topPanel.add(txtIdAFD);
        topPanel.add(btnConvertir);
        topPanel.add(btnRefrescar);

        tableModel = new DefaultTableModel();
        tablaAFD = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaAFD);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        btnConvertir.addActionListener(e -> convertirYMostrarAFD());
        btnRefrescar.addActionListener(e -> cargarAFNsEnCombo());

        frame.setVisible(true);
    }

    private void cargarAFNsEnCombo() {
        comboAFNs.removeAllItems();
        for (AFN afn : ProyectoAFN.listaAFNs) {
            if (afn.getEdoInicial() != null && !afn.getEdosAFN().isEmpty()) {
                comboAFNs.addItem(String.valueOf(afn.getIdAFN()));
            }
        }
    }

    private void convertirYMostrarAFD() {
        try {
            if (comboAFNs.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(frame, "Selecciona un AFN válido.");
                return;
            }

            int idAFN = Integer.parseInt(comboAFNs.getSelectedItem().toString());
            int idAFD = Integer.parseInt(txtIdAFD.getText());

            AFN afn = ProyectoAFN.obtenerAFN(idAFN);
            if (afn == null || afn.getEdoInicial() == null || afn.getEdosAFN().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "AFN inválido o incompleto.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ConvertirAFNaAFD convertidor = new ConvertirAFNaAFD(afn);
            AFD afd = convertidor.convertir();
            afd.setIdAFD(idAFD);

            mostrarTablaAFD(afd);
            guardarAFD(afd);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ingresa IDs válidos para el AFN y el AFD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error durante la conversión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTablaAFD(AFD afd) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.addColumn("Estado");
        for (int i = 0; i <= 256; i++) {
            tableModel.addColumn(String.valueOf(i));
        }

        for (Estado estado : afd.getEstados()) {
            Object[] fila = new Object[258];
            fila[0] = estado.getIdEdo();

            for (int i = 0; i <= 256; i++) {
                Estado destino = afd.getEstadoDestino(estado, String.valueOf((char) i));
                fila[i + 1] = (destino != null) ? destino.getIdEdo() : -1;
            }

            tableModel.addRow(fila);
        }
    }

    private void guardarAFD(AFD afd) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar AFD como archivo binario");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                afd.guardarComoBinario(archivo.getAbsolutePath());
                JOptionPane.showMessageDialog(frame, "✅ AFD guardado correctamente en: " + archivo.getAbsolutePath());
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(frmConvertirAFNaAFD::new);
    }
}
