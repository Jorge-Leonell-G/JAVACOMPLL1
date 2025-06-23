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
import analizlexic.Regla;
import analizlexic.Nodo;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private LL1TablaDialog popup;

    // Componentes de interfaz
    private JLabel jLabel2;
    private JButton AnalizarBoton;
    private JButton selecArchivoBoton;
    private JTextField sigmaText;
    private JTextField nombreArchivo;
    private JTable tablaLL1;
    private JScrollPane scrollTablaLL1;

    
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
    //private JButton analizarGramaticaButton;
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
        //inicializacion de tabla LL1
        tablaLL1 = new JTable();
        scrollTablaLL1 = new JScrollPane(tablaLL1);
        scrollTablaLL1.setBounds(20, 300, 700, 250);
        this.add(scrollTablaLL1);

        
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
        //analizarGramaticaButton = new JButton("Analizar Gramática");
        cambiarTokensButton = new JButton("Asignar Tokens");
        generarTablaLL1Button = new JButton("Generar Tablas");
        
        // Configurar action listeners
        probarLexicoButton.addActionListener(this::probarLexicoButtonActionPerformed);
        //analizarGramaticaButton.addActionListener(this::analizarGramaticaButtonActionPerformed);
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
        //grammarPanel.add(analizarGramaticaButton, BorderLayout.SOUTH);
        
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
    String sigma = sigmaText.getText().trim();
    if (sigma.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa una cadena para analizar.",
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (t == null) {
        JOptionPane.showMessageDialog(this, "Primero analiza una gramática válida.",
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    realizarAnalisisSintactico(sigma);
}
    
    private void realizarAnalisisSintactico(String sigma) { // sigma = cadena del usuario ("simb OR simb")
    try {
        // === Validaciones ===
        if (t == null) throw new Exception("Primero genera la tabla LL(1)");
        if (sigma.isEmpty()) throw new Exception("Ingresa una cadena a analizar");

        // === 1. Análisis léxico ===
        analizador = new Lexic(sigma, "ruta/afd.bin"); // Usa el AFD, NO la gramática
        List<Simbolo> tokens = analizador.analizarCadena(sigma);
        
        // Verifica tokens válidos (deben ser terminales: números positivos)
        for (Simbolo s : tokens) {
            if (s.getToken() <= 0) { // -1 es para no terminales
                throw new Exception("Token inválido: '" + s.getLexema() + "'");
            }
        }

        // === 2. Análisis sintáctico ===
        parser = new LL1(t, (Lexic) tokens); // Pasa solo los tokens de la cadena
        // ... resto del análisis ...
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
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
/*
    private void analizarGramaticaButtonActionPerformed(java.awt.event.ActionEvent evt) {
    // 1. Validar y formatear gramática
    String gramatica = textArea.getText()
        .replaceAll("[ ]{2,}", " ")
        .replaceAll("\\s*->\\s*", " -> ")
        .replaceAll("\\s*\\|\\s*", " | ")
        .replaceAll("\\s*;\\s*", ";\n")
        .trim();
    
    if (gramatica.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa una gramática válida.");
        return;
    }
    
    System.out.println("GRAMÁTICA ENVIADA:\n" + gramatica);

    // 2. Analizar estructura gramatical
    try {
        g = new DescRecGram(gramatica, "C:\\Users\\leone\\Documents\\LEXLL1");
        
        if (!g.analizarEstructuraGramatical()) {
            JOptionPane.showMessageDialog(this, 
                "La gramática tiene errores estructurales:\n" +
                "- Formato incorrecto de producciones\n" +
                "- Símbolos mal formados\n" +
                "- Otra validación sintáctica",
                "Error en gramática", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Extraer símbolos en orden de aparición
        List<String> ordenTerminales = new ArrayList<>();
        List<String> ordenNoTerminales = new ArrayList<>();
        Set<String> terminalesUnicos = new LinkedHashSet<>();
        Set<String> noTerminalesUnicos = new LinkedHashSet<>();
        //Filtrado de terminales para excluir los no terminales
        List<String> terminalesFiltrados = new ArrayList<>();
        for (String simbolo : ordenTerminales) {
            if (!noTerminalesUnicos.contains(simbolo) && 
                !simbolo.equals("EPSILON") && 
                !simbolo.equals("ε") && !simbolo.equals("$")) {
                terminalesFiltrados.add(simbolo);
            }
        }

        String[] lineas = gramatica.split(";");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;

            String[] partes = linea.split("->");
            if (partes.length != 2) continue;

            String ladoIzq = partes[0].trim();
            if (!noTerminalesUnicos.contains(ladoIzq)) {
                noTerminalesUnicos.add(ladoIzq);
                ordenNoTerminales.add(ladoIzq);
            }

            String[] producciones = partes[1].trim().split("\\|");
            for (String prod : producciones) {
                String[] simbolos = prod.trim().split("\\s+");
                for (String simbolo : simbolos) {
                    if (simbolo.isEmpty() || simbolo.equals("Epsilon") || simbolo.equals("ε")) continue;
                    
                    // Es terminal si: es "simb" o está en mayúsculas y no es no terminal
                    boolean esTerminal = simbolo.equals("simb") || simbolo.equals("guion") ||
                                       (simbolo.equals(simbolo.toUpperCase()) && !g.Vn.contains(simbolo) && !simbolo.equals("$"));
                    
                    if (esTerminal && !terminalesUnicos.contains(simbolo) && !g.Vn.contains(simbolo)) {
                        terminalesUnicos.add(simbolo);
                        ordenTerminales.add(simbolo);
                    } else if (!esTerminal && !noTerminalesUnicos.contains(simbolo)) {
                        noTerminalesUnicos.add(simbolo);
                        ordenNoTerminales.add(simbolo);
                    }
                }
            }
        }

        // Asegurar terminales específicos (solo si no son no terminales)
        if (!terminalesFiltrados.contains("PARD") && !noTerminalesUnicos.contains("PARD")) {
            terminalesFiltrados.add("PARD");
        }
        if (!terminalesFiltrados.contains("CORD") && !noTerminalesUnicos.contains("CORD")) {
            terminalesFiltrados.add("CORD");
        }

        // 4. Mostrar terminales y no terminales en orden de aparición
        terminalTableModel.setRowCount(0);
        noTerminalTableModel.setRowCount(0);

        // Mostrar terminales filtrados
        for (String terminal : terminalesFiltrados) {
            terminalTableModel.addRow(new Object[]{terminal, ""});
        }
        // Añadir símbolo de fin de cadena al final
        //terminalTableModel.addRow(new Object[]{"$", ""});

        // Mostrar no terminales (en orden de aparición)
        for (String noTerminal : ordenNoTerminales) {
            if (!noTerminal.equals("EPSILON") && !noTerminal.equals("ε")) {
                noTerminalTableModel.addRow(new Object[]{noTerminal});
            }
        }
        // Añadir símbolo de fin de cadena al final (no terminal)
        noTerminalTableModel.addRow(new Object[]{"$"});

        // 5. Generar estructura básica de tabla LL(1)
        t = new LL1Tabla(g.NumReglas, g.arrReglas, g.Vn, g.Vt);
        t.construirEstructuraBasica();

        JOptionPane.showMessageDialog(this, 
            "Análisis estructural completado:\n" +
            "- Terminales y no terminales identificados\n" +
            "- Estructura LL(1) preparada\n\n" +
            "Asigna tokens a los terminales y luego usa 'Analizar Sintácticamente'",
            "Análisis exitoso", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al analizar la gramática: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
*/
private void generarTablaLL1ButtonActionPerformed(java.awt.event.ActionEvent evt) {
    // 1. Validar y formatear gramática
    String gramatica = textArea.getText()
        .replaceAll("[ ]{2,}", " ")
        .replaceAll("\\s*->\\s*", " -> ")
        .replaceAll("\\s*\\|\\s*", " | ")
        .replaceAll("\\s*;\\s*", ";\n")
        .trim();

    if (gramatica.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa una gramática válida.");
        return;
    }

    System.out.println("GRAMÁTICA ENVIADA:\n" + gramatica);

    // 2. Extraer reglas y símbolos en orden de aparición
    Set<String> noTerminales = new LinkedHashSet<>();
    List<Regla> reglas = new ArrayList<>();
    List<String> ordenTerminales = new ArrayList<>();
    Set<String> terminalesUnicos = new LinkedHashSet<>();

    t = new LL1Tabla();

    String[] lineas = gramatica.split(";");
    for (String linea : lineas) {
        if (linea.trim().isEmpty()) continue;

        String[] partes = linea.split("->");
        if (partes.length != 2) continue;

        String ladoIzq = partes[0].trim();
        noTerminales.add(ladoIzq);

        String[] producciones = partes[1].trim().split("\\|");
        for (String prod : producciones) {
            String[] simbolos = prod.trim().split("\\s+");
            List<Nodo> listaNodos = new ArrayList<>();
            for (String simbolo : simbolos) {
                if (simbolo.isEmpty()) continue;
                if (simbolo.equals("Epsilon")) simbolo = "ε";
                
                // Identificar terminales
                boolean esTerminal = simbolo.equals("simb") || simbolo.equals("guion") ||
                                   (simbolo.equals(simbolo.toUpperCase()) && !noTerminales.contains(simbolo));
                
                if (esTerminal && !terminalesUnicos.contains(simbolo)) {
                    terminalesUnicos.add(simbolo);
                    ordenTerminales.add(simbolo);
                }
                
                Nodo nodo = new Nodo(simbolo, esTerminal);
                listaNodos.add(nodo);
            }
            reglas.add(new Regla(ladoIzq, listaNodos));
        }
    }
    
    // Filtrar terminales para excluir no terminales
    List<String> terminalesFiltrados = new ArrayList<>();
    for (String simbolo : ordenTerminales) {
        if (!noTerminales.contains(simbolo) && 
            !simbolo.equals("EPSILON") && 
            !simbolo.equals("ε")) {
            terminalesFiltrados.add(simbolo);
        }
    }

    // Asegurar terminales específicos
    if (!terminalesFiltrados.contains("PARD") && !noTerminales.contains("PARD")) {
        terminalesFiltrados.add("PARD");
    }
    if (!terminalesFiltrados.contains("CORD") && !noTerminales.contains("CORD")) {
        terminalesFiltrados.add("CORD");
    }
    // Añadir símbolo de fin de cadena
    //terminalesFiltrados.add("$");

    // 3. Configurar objeto LL1Tabla
    t.ReglaA = reglas;
    t.numReglas = reglas.size();
    t.Vn = new LinkedHashSet<>(noTerminales);
    t.Vn.add("$"); // Añadir $ como no terminal

    // 4. Construir tabla LL(1)
    t.construirTablaLL1();
    Map<String, Map<String, String>> tablaLL1 = t.tablaLL1;

    // 5. Mostrar terminales y no terminales en orden de aparición
    terminalTableModel.setRowCount(0);
    noTerminalTableModel.setRowCount(0);

    // Mostrar terminales filtrados en orden de aparición
    // Mostrar terminales filtrados
    for (String terminal : terminalesFiltrados) {
        terminalTableModel.addRow(new Object[]{terminal, ""});
    }
    
    // Mostrar no terminales en orden de aparición
    List<String> noTerminalesOrdenados = new ArrayList<>(noTerminales);
    for (String noTerminal : noTerminalesOrdenados) {
        if (!noTerminal.equals("EPSILON") && !noTerminal.equals("ε")) {
            noTerminalTableModel.addRow(new Object[]{noTerminal});
        }
    }
    noTerminalTableModel.addRow(new Object[]{"$"});

    // 6. Mostrar tabla LL(1)
    ordenTerminales.add(0, "No Terminal");
    ll1DynamicTableModel.setColumnIdentifiers(ordenTerminales.toArray());
    ll1DynamicTableModel.setRowCount(0);

    // Añadir filas para todos los no terminales (incluyendo $)
    Set<String> todosNoTerminales = new LinkedHashSet<>(noTerminales);
    todosNoTerminales.add("$");
    for (String noTerminal : todosNoTerminales) {
        if (noTerminal.equals("EPSILON") || noTerminal.equals("ε")) continue;
        
        Object[] row = new Object[ordenTerminales.size()];
        row[0] = noTerminal;
        for (int i = 1; i < ordenTerminales.size(); i++) {
            String terminal = ordenTerminales.get(i);
            String valor = tablaLL1.containsKey(noTerminal) ? 
                         tablaLL1.get(noTerminal).getOrDefault(terminal, "-") : "-";
            row[i] = valor;
        }
        ll1DynamicTableModel.addRow(row);
    }

    // 7. Mostrar mensaje de éxito
    JOptionPane.showMessageDialog(this,
        "Tablas generadas correctamente:\n" +
        "- Terminales identificados: " + (ordenTerminales.size() - 2) + "\n" + // -2 por "No Terminal" y $
        "- No terminales identificados: " + (noTerminales.size() + 1) + "\n" +
        "Ahora puedes asignar tokens a los terminales.",
        "Éxito", JOptionPane.INFORMATION_MESSAGE);

    popup = new LL1TablaDialog(this, "Tabla LL(1)", ll1DynamicTableModel);
    popup.setVisible(true);
}
 
    private void cambiarTokensButtonActionPerformed(java.awt.event.ActionEvent evt) {
    // Verifica si la tabla emergente está creada y visible
    if (t == null || popup == null || !popup.isDisplayable()) {
        JOptionPane.showMessageDialog(this, "Primero genera la tabla LL(1).", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Map<String, Integer> tokensUsuario = new HashMap<>();

    // Lee los tokens ingresados para cada terminal
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

    // Llama a la clase TablaLL1 (o como se llame tu clase de ventana) para reemplazar
    t.convertirTerminalesATokens(tokensUsuario);
    t.imprimirTablaLL1(); // Opcional: imprime en consola para depuración

    JOptionPane.showMessageDialog(this, "Terminales reemplazados por tokens correctamente.",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
}

    
    public void mostrarTablaLL1(LL1Tabla tabla) {
    Map<String, Map<String, String>> datos = tabla.tablaLL1;

    // Obtener terminales únicos
    Set<String> terminalesSet = new HashSet<>();
    for (Map<String, String> fila : datos.values()) {
        terminalesSet.addAll(fila.keySet());
    }

    List<String> terminales = new ArrayList<>(terminalesSet);
    terminales.sort(String::compareTo);

    // Encabezados: primero columna NoTerminal
    String[] columnas = new String[terminales.size() + 1];
    columnas[0] = "No Terminal";
    for (int i = 0; i < terminales.size(); i++) {
        columnas[i + 1] = terminales.get(i);
    }

    // Filas
    Object[][] filas = new Object[datos.size()][columnas.length];
    int filaIdx = 0;
    for (String noTerminal : datos.keySet()) {
        filas[filaIdx][0] = noTerminal;
        Map<String, String> producciones = datos.get(noTerminal);
        for (int colIdx = 0; colIdx < terminales.size(); colIdx++) {
            String terminal = terminales.get(colIdx);
            filas[filaIdx][colIdx + 1] = producciones.getOrDefault(terminal, "");
        }
        filaIdx++;
    }

    // Actualizar la tabla visual
    DefaultTableModel modelo = new DefaultTableModel(filas, columnas);
    tablaLL1.setModel(modelo);
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