package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class frmAFNBasico {
    private JFrame frame;
    private JTextField txtInferior;
    private JTextField txtSuperior;
    private JTextField txtToken;

    public frmAFNBasico() {
        inicializar();
    }

    private void inicializar() {
        Color fondo = new Color(30, 30, 30);
        Color texto = new Color(245, 245, 220);
        Color fondoCampo = new Color(50, 50, 50);
        Font fuente = new Font("SansSerif", Font.PLAIN, 14);

        frame = new JFrame("Creación de AFN básico");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(fondo);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JCheckBox usarAscii = new JCheckBox("Usar código ASCII");
        usarAscii.setForeground(texto);
        usarAscii.setBackground(fondo);

        JLabel lblInf = new JLabel("Carácter Inferior:");
        JLabel lblSup = new JLabel("Carácter Superior:");
        JLabel lblToken = new JLabel("ID del Token:");

        for (JLabel lbl : new JLabel[]{lblInf, lblSup, lblToken}) {
            lbl.setForeground(texto);
        }

        txtInferior = new JTextField(5);
        txtSuperior = new JTextField(5);
        txtToken = new JTextField(5);

        for (JTextField txt : new JTextField[]{txtInferior, txtSuperior, txtToken}) {
            txt.setBackground(fondoCampo);
            txt.setForeground(texto);
            txt.setCaretColor(texto);
        }

        JButton btnCrear = new JButton("Crear AFN");
        btnCrear.setBackground(new Color(60, 60, 60));
        btnCrear.setForeground(texto);
        btnCrear.setFocusPainted(false);

        panel.add(usarAscii);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblInf);
        panel.add(txtInferior);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblSup);
        panel.add(txtSuperior);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblToken);
        panel.add(txtToken);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnCrear);

        frame.add(panel, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> {
            try {
                char inf, sup;
                if (usarAscii.isSelected()) {
                    inf = (char) Integer.parseInt(txtInferior.getText());
                    sup = (char) Integer.parseInt(txtSuperior.getText());
                } else {
                    inf = txtInferior.getText().charAt(0);
                    sup = txtSuperior.getText().charAt(0);
                }

                int token = Integer.parseInt(txtToken.getText());

                // Crear el AFN con el método que ya incluye el registro
                AFN nuevoAFN = new AFN().crearAFNBasico(inf, sup, token);

                JOptionPane.showMessageDialog(frame, "✅ AFN creado y guardado con éxito.");
                frame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "❌ Error al crear el AFN.\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(frmAFNBasico::new);
    }
}
