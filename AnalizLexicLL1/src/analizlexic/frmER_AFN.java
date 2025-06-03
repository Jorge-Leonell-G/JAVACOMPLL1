package analizlexic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class frmER_AFN {

    public frmER_AFN() {
        JFrame frame = new JFrame("ER → AFN");
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblER = new JLabel("Expresión regular:");
        lblER.setForeground(new Color(245, 245, 220));
        JTextField txtER = new JTextField();

        JLabel lblToken = new JLabel("Token a asignar:");
        lblToken.setForeground(new Color(245, 245, 220));
        JTextField txtToken = new JTextField();

        JButton btnGenerar = new JButton("Generar AFN");
        btnGenerar.setBackground(new Color(60, 60, 60));
        btnGenerar.setForeground(new Color(245, 245, 220));

        panel.add(lblER);
        panel.add(txtER);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblToken);
        panel.add(txtToken);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnGenerar);

        frame.add(panel, BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> {
            String er = txtER.getText().trim();
            String tokenStr = txtToken.getText().trim();

            if (er.isEmpty() || tokenStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Debes ingresar una expresión regular y un token.",
                        "Campos vacíos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int token = Integer.parseInt(tokenStr);
                if (token < 0) {
                    throw new NumberFormatException("Token negativo");
                }

                // Construcción del AFN
                ER_AFN constructor = new ER_AFN(er, token);
                AFN afn = constructor.construir();

                if (afn != null && afn.getEdoInicial() != null) {
                    afn.setToken(token);
                    afn.setIdAFN(token);

                    // ✅ Agregar de forma controlada para evitar duplicados
                    ProyectoAFN.agregarAFN(afn);

                    JOptionPane.showMessageDialog(frame,
                            "✔️ La expresión regular fue convertida correctamente a AFN.\n" +
                                    "🧠 Token asignado: " + token,
                            "Conversión exitosa", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ No se pudo generar el AFN. Verifica la expresión regular.",
                            "Fallo en generación", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "El token debe ser un número entero válido.",
                        "Token inválido", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Ocurrió un error: " + ex.getMessage(),
                        "Error inesperado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }
}
