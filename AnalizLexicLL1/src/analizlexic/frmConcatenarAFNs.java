package analizlexic;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class frmConcatenarAFNs extends JFrame {
    public frmConcatenarAFNs() {
        setTitle("Concatenar AFNs");
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

        JButton btn = new JButton("Concatenar");

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

            afn1.concatenarAFNs(afn2); // ya elimina internamente afn2 del conjunto

            JOptionPane.showMessageDialog(this, "✅ AFNs concatenados correctamente.");
            dispose();
        });

        add(label1); add(combo1);
        add(label2); add(combo2);
        add(new JLabel()); add(btn);

        setVisible(true);
    }
}
