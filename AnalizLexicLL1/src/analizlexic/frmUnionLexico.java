package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class frmUnionLexico {
    private JFrame frame;
    private JTable tablaAFNs;
    private JTextField txtIdAFNResultado;

    public frmUnionLexico() {
        inicializar();
    }

    private void inicializar() {
        frame = new JFrame("Unir AFNs para léxico");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel instrucciones = new JLabel("Seleccione los AFNs a unir y asigne los tokens:");
        instrucciones.setForeground(Color.BLACK);

        JLabel advertencia = new JLabel("Para ignorar una clase léxica, use el TOKEN 20001");
        advertencia.setForeground(Color.RED);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.add(instrucciones);
        encabezado.add(advertencia);

        String[] columnas = {"AFNs", "Seleccionar AFN", "Token"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        // Evitar duplicados
        Set<Integer> idsAgregados = new HashSet<>();
        for (AFN afn : ProyectoAFN.listaAFNs) {
            if (idsAgregados.add(afn.getIdAFN())) {
                model.addRow(new Object[]{afn.getIdAFN(), Boolean.FALSE, ""});
            }
        }

        tablaAFNs = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tablaAFNs);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.add(new JLabel("ID del AFN resultante:"));
        txtIdAFNResultado = new JTextField(5);
        panelInferior.add(txtIdAFNResultado);

        JButton btnUnir = new JButton("Unir AFNs");
        panelInferior.add(btnUnir);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelInferior, BorderLayout.SOUTH);
        frame.add(panel);

        btnUnir.addActionListener(e -> {
            List<AFN> seleccionados = new ArrayList<>();
            List<Integer> tokens = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {
                boolean seleccionado = (Boolean) model.getValueAt(i, 1);
                if (seleccionado) {
                    int idAFN = Integer.parseInt(model.getValueAt(i, 0).toString());
                    int token = 20001;
                    try {
                        token = Integer.parseInt(model.getValueAt(i, 2).toString().trim());
                    } catch (NumberFormatException ignored) {}

                    AFN afn = ProyectoAFN.obtenerAFN(idAFN);
                    if (afn != null) {
                        // Asignar token a cada estado de aceptación individual
                        for (Estado edo : afn.getEdosAceptacion()) {
                            edo.setToken(token);
                        }
                        afn.setToken(token);
                        seleccionados.add(afn);
                        tokens.add(token);
                    }
                }
            }

            if (seleccionados.size() < 2) {
                JOptionPane.showMessageDialog(frame, "Selecciona al menos 2 AFNs para unir.");
                return;
            }

            int nuevoId;
            try {
                nuevoId = Integer.parseInt(txtIdAFNResultado.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingresa un ID numérico válido para el AFN resultante.");
                return;
            }

            try {
                AFN resultado = UnionLexico.unirAFNs(seleccionados, nuevoId, tokens);

                ProyectoAFN.listaAFNs.clear();
                ProyectoAFN.listaAFNs.add(resultado);

                JOptionPane.showMessageDialog(frame,
                        "✅ AFNs unidos correctamente. Solo el nuevo AFN fue conservado con ID: " + resultado.getIdAFN());
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Error al unir AFNs: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(frmUnionLexico::new);
    }
}
