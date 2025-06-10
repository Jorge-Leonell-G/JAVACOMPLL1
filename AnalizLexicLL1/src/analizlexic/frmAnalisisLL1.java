/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

import analizlexic.AFN;
import analizlexic.Lexic;
import analizlexic.DescRecGram;
import analizlexic.SimbolosEspeciales;
import analizlexic.LL1Tabla;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class frmAnalisisLL1 extends javax.swing.JFrame {

    // Componentes de análisis
    private Lexic analizador;
    private DescRecGram g;
    private LL1Tabla t;
    private LL1 parser;    

    // Componentes de interfaz
    private JLabel jLabel2;
    private JButton AnalizarBoton;
    private JButton selecArchivoBoton;
    private JTextField sigmaText;
    private JTextField nombreArchivo;
    
    // Componentes de tablas
    private DefaultTableModel tableModel;
    private JTable resultsTable;
    private JTextArea textArea;
    private DefaultTableModel ll1TableModel;
    private JTable ll1Table;
    private DefaultTableModel ll1DynamicTableModel;
    private JTable ll1DynamicTable;
    private JTable terminalTable;
    private JTable noTerminalTable;
    private DefaultTableModel terminalTableModel;
    private DefaultTableModel noTerminalTableModel;
    
    // Botones
    private JButton probarLexicoButton;
    private JButton analizarGramaticaButton;
    private JButton cambiarTokensButton;
    private JButton generarTablaLL1Button;
    
    private File archivoAFD;

    public frmAnalisisLL1() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Inicializar componentes
        initComponents();
        initializeModelsAndTables();
        initializeButtons();
        
        // Configurar interfaz
        setupMainLayout();
        setVisible(true);
    }

    private void initComponents() {
        jLabel2 = new JLabel("Sigma:");
        AnalizarBoton = new JButton("Analizar Sintácticamente");
        selecArchivoBoton = new JButton("Seleccionar Archivo AFD");
        sigmaText = new JTextField(15);
        nombreArchivo = new JTextField(20);
        
        // Configurar action listeners
        selecArchivoBoton.addActionListener(this::selecArchivoBotonActionPerformed);
        AnalizarBoton.addActionListener(this::AnalizarBotonActionPerformed);
        sigmaText.addActionListener(this::sigmaTextActionPerformed);
        nombreArchivo.addActionListener(this::nombreArchivoActionPerformed);
    }

    private void initializeModelsAndTables() {
        // Modelo y tabla para análisis léxico
        tableModel = new DefaultTableModel(new String[]{"Lexema", "Token"}, 0);
        resultsTable = new JTable(tableModel);
        
        // Área de texto para gramática
        textArea = new JTextArea();
        textArea.setText("E ->  T Ep;\n"
                + "Ep ->  OR T Ep | Epsilon;\n"
                + "T ->  C Tp;\n"
                + "Tp ->  CONC C Tp | Epsilon;\n"
                + "C ->  F Cp;\n"
                + "Cp ->  POS Cp | KLEEN Cp | OPC Cp | Epsilon;\n"
                + "F ->  PARI E PARD | simb | CORI simb guion simb CORD;");
        
        // Tabla LL1
        ll1TableModel = new DefaultTableModel(new String[]{"Pila", "Cadena", "Acción"}, 0);
        ll1Table = new JTable(ll1TableModel);
        
        // Tabla dinámica LL1
        ll1DynamicTableModel = new DefaultTableModel();
        ll1DynamicTable = new JTable(ll1DynamicTableModel);
        
        // Tablas de terminales y no terminales
        terminalTableModel = new DefaultTableModel(new String[]{"Terminal", "Token"}, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
            @Override public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        terminalTable = new JTable(terminalTableModel);
        
        noTerminalTableModel = new DefaultTableModel(new String[]{"No Terminal"}, 0);
        noTerminalTable = new JTable(noTerminalTableModel);
    }

    private void initializeButtons() {
        probarLexicoButton = new JButton("Probar Analizador Léxico");
        analizarGramaticaButton = new JButton("Analizar Gramática");
        cambiarTokensButton = new JButton("Cambiar a Tokens");
        generarTablaLL1Button = new JButton("Generar Tabla LL(1)");
        
        // Configurar action listeners
        probarLexicoButton.addActionListener(this::probarLexicoButtonActionPerformed);
        analizarGramaticaButton.addActionListener(this::analizarGramaticaButtonActionPerformed);
        cambiarTokensButton.addActionListener(this::cambiarTokensButtonActionPerformed);
        generarTablaLL1Button.addActionListener(this::generarTablaLL1ButtonActionPerformed);
    }

    private void setupMainLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(selecArchivoBoton);
        topPanel.add(nombreArchivo);
        topPanel.add(jLabel2);
        topPanel.add(sigmaText);
        topPanel.add(AnalizarBoton);
        
        // Panel central con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña 1: Analizador Léxico
        JPanel lexPanel = new JPanel(new BorderLayout());
        lexPanel.add(new JLabel("Analizador Léxico"), BorderLayout.NORTH);
        lexPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        lexPanel.add(probarLexicoButton, BorderLayout.SOUTH);
        
        // Pestaña 2: Gramática
        JPanel grammarPanel = new JPanel(new BorderLayout());
        grammarPanel.add(new JLabel("Gramática LL(1)"), BorderLayout.NORTH);
        grammarPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        grammarPanel.add(analizarGramaticaButton, BorderLayout.SOUTH);
        
        // Pestaña 3: Tablas
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2));
        tablesPanel.add(new JScrollPane(terminalTable));
        tablesPanel.add(new JScrollPane(noTerminalTable));
        
        tabbedPane.addTab("Léxico", lexPanel);
        tabbedPane.addTab("Gramática", grammarPanel);
        tabbedPane.addTab("Símbolos", tablesPanel);
        
        // Panel inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Análisis Sintáctico LL(1)"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(ll1Table), BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel actionPanel = new JPanel();
        actionPanel.add(cambiarTokensButton);
        actionPanel.add(generarTablaLL1Button);
        
        // Ensamblar interfaz
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(actionPanel, BorderLayout.PAGE_END);
        
        add(mainPanel);
    }

    // Métodos de acción
    private void selecArchivoBotonActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        int opcion = chooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            archivoAFD = chooser.getSelectedFile();
            nombreArchivo.setText(archivoAFD.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "✅ Archivo AFD cargado correctamente.");
        }
    }

    private void AnalizarBotonActionPerformed(java.awt.event.ActionEvent evt) {
        if (t == null || analizador == null) {
            JOptionPane.showMessageDialog(this, "Primero carga una tabla LL(1) válida y realiza el análisis léxico.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reinicializar el analizador léxico
        String sigma = sigmaText.getText().trim();
        String archivoRuta = nombreArchivo.getText().trim();

        try {
            analizador = new Lexic(sigma, archivoRuta);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al reiniciar el analizador léxico: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear el analizador sintáctico LL(1)
        parser = new LL1(t, analizador);
        ll1TableModel.setRowCount(0);

        // Ejecutar análisis sintáctico
        while (!parser.pila.isEmpty()) {
            String pilaEstado = obtenerPilaComoString(parser.pila);
            String cadenaActual = parser.cadenaActual;
            String accion = parser.accion;

            System.out.println("Pila: " + pilaEstado + " | Cadena: " + cadenaActual + " | Acción: " + accion);
            agregarFilaLL1Dinamica(pilaEstado, cadenaActual, accion);

            if (!parser.analisisRecursivo()) {
                JOptionPane.showMessageDialog(this, "Error durante el análisis sintáctico.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        agregarFilaLL1Dinamica("", "$", "Aceptar");
        JOptionPane.showMessageDialog(this, "Análisis sintáctico completado exitosamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void probarLexicoButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String archivoRuta = nombreArchivo.getText().trim();
        String sigma = sigmaText.getText().trim();

        if (archivoRuta.isEmpty() || sigma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un archivo y proporciona un sigma válido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            AnalizLexico lexico = new AnalizLexico(sigma, archivoRuta);
            List<Simbolo> simbolos = lexico.obtenerSimbolos();

            tableModel.setRowCount(0); 

            if (simbolos.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontraron símbolos en la cadena.");
                return;
            }

            for (Simbolo s : simbolos) {
                tableModel.addRow(new Object[]{s.getLexema(), s.getToken()});
            }
            /*
            //analizador = new Lexic(sigma, archivoRuta);
            AnalizLexico lexico = new AnalizLexico(sigma, archivoRuta);
            tableModel.setRowCount(0);

            int token;
            while ((token = lexico.yylex()) != 0) {
                String lexema = anali.yytext();
                tableModel.addRow(new Object[]{lexema, token});
            }
            */
            JOptionPane.showMessageDialog(this, "Análisis léxico completado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error durante el análisis léxico: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void analizarGramaticaButtonActionPerformed(java.awt.event.ActionEvent evt) {
    String gramatica = textArea.getText()
        .replaceAll("[ ]{2,}", " ") // conserva saltos de línea, elimina solo espacios repetidos
        .replaceAll("\\s*->\\s*", " -> ")
        .replaceAll("\\s*\\|\\s*", " | ")
        .replaceAll("\\s*;\\s*", ";\n")
        .trim();

    if (gramatica.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa una gramática válida.");
        return;
    }

    System.out.println("GRAMÁTICA ENVIADA:\n" + gramatica);

    g = new DescRecGram(gramatica, "C:\\Users\\leone\\Documents\\LEXLL1");

    if (g.AnalizarGramatica()) {
        JOptionPane.showMessageDialog(this, "Gramática analizada correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);

        terminalTableModel.setRowCount(0);
        noTerminalTableModel.setRowCount(0);

        for (String terminal : g.Vt) {
            if (!terminal.equals("Epsilon")) {
                terminalTableModel.addRow(new Object[]{terminal, ""});
            }
        }

        for (String noTerminal : g.Vn) {
            noTerminalTableModel.addRow(new Object[]{noTerminal});
        }

    } else {
        JOptionPane.showMessageDialog(this, "La gramática es incorrecta.",
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void cambiarTokensButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Primero genera la tabla LL(1).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, Integer> tokensUsuario = new HashMap<>();
        for (int i = 0; i < terminalTableModel.getRowCount(); i++) {
            String terminal = terminalTableModel.getValueAt(i, 0).toString().trim();
            Object tokenValue = terminalTableModel.getValueAt(i, 1);

            if (tokenValue == null || tokenValue.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Token vacío para el terminal: " + terminal
                        + " en la fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int token = Integer.parseInt(tokenValue.toString().trim());
                tokensUsuario.put(terminal, token);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Token inválido para el terminal: " + terminal,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        t.convertirTerminalesATokens(tokensUsuario);
        t.imprimirTablaLL1();
        JOptionPane.showMessageDialog(this, "Terminales reemplazados por tokens correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarTablaLL1ButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (g == null) {
            JOptionPane.showMessageDialog(this, "Primero analiza la gramática.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        t = new LL1Tabla(g.NumReglas, g.arrReglas, g.Vn, g.Vt);
        t.construirTablaLL1();

        Map<String, Map<String, String>> tablaNumeros = t.convertirTablaLL1ANumerosDeRegla();
        Set<String> terminales = new HashSet<>();
        
        for (Map<String, String> fila : tablaNumeros.values()) {
            terminales.addAll(fila.keySet());
        }
        if (!terminales.contains("$")) {
            terminales.add("$");
        }

        List<String> headers = new ArrayList<>(terminales);
        headers.sort(String::compareTo);
        headers.add(0, "No Terminal");

        ll1DynamicTableModel.setColumnIdentifiers(headers.toArray());
        ll1DynamicTableModel.setRowCount(0);

        for (String noTerminal : tablaNumeros.keySet()) {
            Object[] row = new Object[headers.size()];
            row[0] = noTerminal;
            for (int i = 1; i < headers.size(); i++) {
                row[i] = tablaNumeros.get(noTerminal).getOrDefault(headers.get(i), "-1");
            }
            ll1DynamicTableModel.addRow(row);
        }

        Object[] ultimaFila = new Object[headers.size()];
        ultimaFila[0] = "$";
        for (int i = 1; i < headers.size(); i++) {
            ultimaFila[i] = headers.get(i).equals("$") ? "Aceptar" : "-1";
        }
        ll1DynamicTableModel.addRow(ultimaFila);

        JOptionPane.showMessageDialog(this, "Tabla LL(1) generada correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos auxiliares
    private void agregarFilaLL1Dinamica(String pila, String cadena, String accion) {
        ll1TableModel.addRow(new Object[]{pila, cadena, accion});
    }

    private String obtenerPilaComoString(Stack<String> pila) {
        return pila.toString().replace("[", "").replace("]", "").replace(", ", " ");
    }

    private void sigmaTextActionPerformed(java.awt.event.ActionEvent evt) {
        // Implementación vacía
    }

    private void nombreArchivoActionPerformed(java.awt.event.ActionEvent evt) {
        // Implementación vacía
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new frmAnalisisLL1().setVisible(true);
        });
    }
}