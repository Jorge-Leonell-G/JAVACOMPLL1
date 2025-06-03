package analizlexic;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class frmCerraduraOpcional extends JFrame {
    public frmCerraduraOpcional() {
        setTitle("Cerradura Opcional");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        JComboBox<Integer> combo = new JComboBox<>();
        List<AFN> listaAFNs = ProyectoAFN.listaAFNs;

        for (AFN afn : listaAFNs) {
            if (afn.getEdoInicial() != null) {
                combo.addItem(afn.getIdAFN());
            }
        }

        JButton aplicar = new JButton("Aplicar");

        aplicar.addActionListener(e -> {
            Integer idSeleccionado = (Integer) combo.getSelectedItem();
            if (idSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "⚠️ Seleccione un AFN válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            AFN seleccionado = null;
            for (AFN afn : listaAFNs) {
                if (afn.getIdAFN() == idSeleccionado) {
                    seleccionado = afn;
                    break;
                }
            }

            if (seleccionado != null) {
                seleccionado.opcional();
                JOptionPane.showMessageDialog(this, "✅ Cerradura opcional aplicada al AFN con ID " + idSeleccionado);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se encontró el AFN seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(new JLabel("Seleccione AFN:"));
        add(combo);
        add(aplicar);

        setVisible(true);
    }
}
