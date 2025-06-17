/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 *
 * @author leone
 */
public class LL1TablaDialog extends JFrame {
    private JTable tabla;
    private DefaultTableModel modelo;

    public LL1TablaDialog(Frame owner, String titulo, DefaultTableModel modeloTabla) {
        super(titulo); // Solo pasamos el título, ya que JFrame no es modal
        this.modelo = modeloTabla;

        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);

        setSize(600, 400);
        setLocationRelativeTo(owner); // Centrar respecto a la ventana principal

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana
    }
}
