package analizlexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DescRecGram {
    public String Gramatica;
    public Lexic Scanner;

    public HashSet<String> Vn = new HashSet<>(); // Conjunto de no terminales
    public HashSet<String> Vt = new HashSet<>(); // Conjunto de terminales

    public int NumReglas = 0;
    public List<Regla> arrReglas = new ArrayList<>();

    public DescRecGram(String sigma, String FileAFD) {
        Gramatica = sigma;
        Scanner = new Lexic(Gramatica, FileAFD);
        Vn.clear();
        Vt.clear();
    }

    public DescRecGram(String FileAFD) {
        Scanner = new Lexic("", FileAFD);
        Vn.clear();
        Vt.clear();
    }

    public boolean SetGramatica(String sigma) {
        Gramatica = sigma;
        Scanner.SetSigma(sigma);
        return true;
    }

    /**
     * Analiza solo la estructura de la gramática sin validar tokens
     */
    public boolean analizarEstructuraGramatical() {
        System.out.println("Analizando estructura gramatical...");
        
        // Validaciones básicas de estructura
        if (Gramatica == null || Gramatica.trim().isEmpty()) {
            System.out.println("Gramática vacía");
            return false;
        }

        // Reiniciar estructuras de datos
        Vn.clear();
        Vt.clear();
        arrReglas.clear();
        NumReglas = 0;

        if (G()) {
            System.out.println("Estructura gramatical válida");
            identificarTerminales();
            return true;
        }
        
        System.out.println("Error en la estructura gramatical");
        return false;
    }

    /**
     * Analiza la gramática completa (incluyendo tokens)
     */
    public boolean AnalizarGramatica() {
        System.out.println("Analizando gramática completa...");
        if (!analizarEstructuraGramatical()) {
            return false;
        }

        int token = Scanner.yylex();
        if (token == 0) { // FIN
            System.out.println("Gramática analizada exitosamente.");
            return true;
        }
        
        System.out.println("Error en tokens de la gramática");
        return false;
    }

    private boolean G() {
        System.out.println("G -> ListaReglas;");
        return ListaReglas();
    }

    private boolean ListaReglas() {
        System.out.println("ListaReglas -> Reglas PC ListaReglasP;");
        int token;
        if (Reglas()) {
            token = Scanner.yylex();
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
            if (token == 10) { // Punto y coma ;
                return ListaReglasP();
            }
        }
        return false;
    }

    private boolean ListaReglasP() {
        System.out.println("ListaReglasP -> Reglas PC ListaReglasP | epsilon;");
        Lexic estadoScanner = Scanner.getEdoAnalizLexico();
        if (Reglas()) {
            int token = Scanner.yylex();
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
            if (token == 10) { // Punto y coma ;
                return ListaReglasP();
            }
            return false;
        }
        Scanner.setEdoAnalizLexico(estadoScanner);
        System.out.println("Return true en ListaReglasP con un epsilon.");
        return true;
    }

    private boolean Reglas() {
        System.out.println("Reglas -> LadoIzquierdo FLECHA LadosDerechos;");
        ObjetoCadena simboloIzquierdo = new ObjetoCadena();
        if (LadoIzquierdo(simboloIzquierdo)) {            
            int token = Scanner.yylex();
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
            if (token == 20) { // Flecha ->
                return LadosDerechos(simboloIzquierdo);
            }
        }
        return false;
    }

    private boolean LadoIzquierdo(ObjetoCadena simb) {
        System.out.println("Entrando a LadoIzquierdo");
        int token = Scanner.yylex();
        System.out.println("Se ha obtenido el simbolo: " + Scanner.yytext + " con token: " + token);
        if (token == 30) { // Token para símbolo
            simb.setCadena(Scanner.yytext);    
            Vn.add(simb.getCadena());
            System.out.println("Return true en LadoIzquierdo");
            return true;
        }
        return false;
    }

    private boolean LadosDerechos(ObjetoCadena SimbIzq) {
        System.out.println("LadosDerechos -> LadoDerecho LadosDerechosP;");
        List<Nodo> lista = new ArrayList<>();
        if (LadoDerecho(lista)) {
            Regla regla = new Regla();
            regla.nameSimbolo = SimbIzq.getCadena();
            regla.lista = lista;
            
            for(Nodo n : regla.lista) {
                n.isTerminal = false;
            }
            
            arrReglas.add(regla);
            NumReglas++;
            
            return LadosDerechosP(SimbIzq);
        }
        return false;
    }
    
    private boolean LadosDerechosP(ObjetoCadena SimbIzq) {
        System.out.println("LadosDerechosP -> OR LadoDerecho LadosDerechosP | epsilon");
        int token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);
        if(token == 40) {
            List<Nodo> l = new ArrayList<>();
            if(LadoDerecho(l)) {
                Regla regla = new Regla();
                regla.nameSimbolo = SimbIzq.getCadena();
                regla.lista = l;
                
                for(Nodo n : regla.lista) {
                    n.isTerminal = false;
                }
                
                arrReglas.add(regla);
                NumReglas++;
                
                return LadosDerechosP(SimbIzq);
            }            
            return false;
        }
        Scanner.UndoToken();
        System.out.println("Return true en LadosDerechosP con un epsilon.");
        return true;
    }

    private boolean LadoDerecho(List<Nodo> lista) {
        System.out.println("LadoDerecho -> SecSimbolos;");
        return SecSimbolos(lista);
    }

    private boolean SecSimbolos(List<Nodo> lista) {
        System.out.println("SecSimbolos -> SIMBOLO SecSimbolosP;");
        ObjetoCadena simb = new ObjetoCadena();
        int token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);
        if (token == 30) { // Token del símbolo
            simb.setCadena(Scanner.yytext());
            lista.add(new Nodo(simb.getCadena(), false));
            return SecSimbolosP(lista);
        }
        return false;
    }

    private boolean SecSimbolosP(List<Nodo> lista) {
        System.out.println("SecSimbolosP -> SIMBOLO SecSimbolosP | Epsilon;");
        int token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);

        if (token == 30) { // Token símbolo
            ObjetoCadena simb = new ObjetoCadena();
            simb.setCadena(Scanner.yytext());
            lista.add(new Nodo(simb.getCadena(), false));
            return SecSimbolosP(lista);
        }
        Scanner.UndoToken();
        System.out.println("Return true en SecSimbolosP con un epsilon.");
        return true;
    }
    
    public void identificarTerminales() {
        for (Regla regla : arrReglas) {
            for (Nodo N : regla.lista) {
                if (!Vn.contains(N.nameSimbolo)) {
                    if (N.nameSimbolo.equals("Epsilon")) {
                        N.isTerminal = false;  // Epsilon es especial
                    } else {
                        N.isTerminal = true;
                        Vt.add(N.nameSimbolo);
                    }
                }
            }
        }
        System.out.println("Terminales identificados: " + Vt);
        System.out.println("No Terminales identificados: " + Vn);
    }
}