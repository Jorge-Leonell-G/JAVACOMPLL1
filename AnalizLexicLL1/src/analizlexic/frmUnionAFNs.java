package analizlexic;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class frmUnionAFNs extends JFrame {
    public frmUnionAFNs() {
        setTitle("Unir AFNs");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel label1 = new JLabel("AFN 1:");
        JComboBox<Integer> combo1 = new JComboBox<>();
        JLabel label2 = new JLabel("AFN 2:");
        JComboBox<Integer> combo2 = new JComboBox<>();

        List<AFN> listaAFNs = ProyectoAFN.listaAFNs;

        for (AFN afn : listaAFNs) {
            if (afn.getEdoInicial() != null) {
                combo1.addItem(afn.getIdAFN());
                combo2.addItem(afn.getIdAFN());
            }
        }

        JButton btn = new JButton("Unir");

        btn.addActionListener(e -> {
            Integer id1 = (Integer) combo1.getSelectedItem();
            Integer id2 = (Integer) combo2.getSelectedItem();

            if (id1 == null || id2 == null) {
                JOptionPane.showMessageDialog(this, "⚠️ Debes seleccionar ambos AFNs.");
                return;
            }

            if (id1.equals(id2)) {
                JOptionPane.showMessageDialog(this, "⚠️ Debes seleccionar dos AFNs distintos.");
                return;
            }

            AFN afn1 = null, afn2 = null;
            for (AFN afn : listaAFNs) {
                if (afn.getIdAFN() == id1) afn1 = afn;
                if (afn.getIdAFN() == id2) afn2 = afn;
            }

            if (afn1 == null || afn2 == null) {
                JOptionPane.showMessageDialog(this, "❌ Uno o ambos AFNs no fueron encontrados.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String idNuevo = JOptionPane.showInputDialog(this, "Ingresa el ID para el AFN resultante:");
            if (idNuevo == null || idNuevo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ El ID del AFN resultante es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int nuevoId = Integer.parseInt(idNuevo.trim());

                // Remover los originales
                listaAFNs.remove(afn1);
                listaAFNs.remove(afn2);

                // Unir y agregar el nuevo
                AFN resultado = UnionLexico.unirAFNs(List.of(afn1, afn2), nuevoId, null);
                ProyectoAFN.listaAFNs.add(resultado);

                JOptionPane.showMessageDialog(this, "✅ AFNs unidos correctamente con ID: " + nuevoId);
                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ El ID debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error durante la unión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(label1);
        add(combo1);
        add(label2);
        add(combo2);
        add(new JLabel()); // espacio
        add(btn);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(frmUnionAFNs::new);
    }
}
