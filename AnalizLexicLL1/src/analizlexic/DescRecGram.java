/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author leone
 */
public class DescRecGram {
    public String Gramatica;
    public Lexic L;

    public HashSet<String> Vn = new HashSet<>(); //conjunto de no terminales
    public HashSet<String> Vt = new HashSet<>(); //conjunto de terminales

    public int NumReglas = 0;
    public List<Regla> arrReglas = new ArrayList<>();

    public DescRecGram(String sigma, String FileAFD /*int idAFD*/) {
        Gramatica = sigma;
        L = new Lexic(Gramatica, FileAFD);
        Vn.clear();
        Vt.clear();
    }

    public DescRecGram(String FileAFD /*int idAFD*/) {
        L = new Lexic("", FileAFD);
        Vn.clear(); //
        Vt.clear();
    }

    public boolean SetGramatica(String sigma) {
        Gramatica = sigma;
        L.SetSigma(sigma);
        return true;
    }

    public boolean AnalizarGramatica() {
        int token;
        System.out.println("Analizando gramática...");
        if (G()) {
            token = L.yylex();
            if (token == 0) {
                System.out.println("Gramática analizada exitosamente.");
                identificarTerminales();
                return true;
            }
        }
        System.out.println("Error al analizar la gramática.");
        return false;
    }

    //G -> ListaReglas
    private boolean G() {
        System.out.println("G -> ListaReglas;");
        if(ListaReglas()){
            System.out.println("Return true en G.");
            return true;
        }      
        return false;
    }

    private boolean ListaReglas() { //PC = ; (terminal)
        System.out.println("ListaReglas -> Reglas PC ListaReglasP;");
        int token; //token asociado al punto y coma
        if (Reglas()) {
            token = L.yylex();
            System.out.println(L.yytext);
            if (token == 10) { // Punto y coma ;
                if (ListaReglasP()) {
                    System.out.println("Return true en ListaReglas.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ListaReglasP() {
        System.out.println("ListaReglasP -> Reglas PC ListaReglasP | epsilon;");
        int token;
        Lexic estadoScanner = L.getEdoAnalizLexico();
        if (Reglas()) {
            token = L.yylex();
            System.out.println(L.yytext);
            if (token == 10) { // Punto y coma ;
                if (ListaReglasP()) {
                    System.out.println("Return true en ListaReglasP.");
                    return true;
                }
            }
            return false;
        }
        L.setEdoAnalizLexico(estadoScanner);
        System.out.println("Return true en ListaReglasP con un epsilon.");
        return true;
    }

    private boolean Reglas() {
        System.out.println("Reglas -> LadoIzquierdo FLECHA LadosDerechos;");
        int token;
        ObjetoCadena simboloIzquierdo = new ObjetoCadena();
        if (LadoIzquierdo(simboloIzquierdo)) {            
            token = L.yylex();
            System.out.println(L.yytext);
            if (token == 20) { // Flecha ->
                if (LadosDerechos(simboloIzquierdo)) {
                    System.out.println("Return true en Reglas.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean LadoIzquierdo(ObjetoCadena Simb) {
        System.out.println("LadoIzquierdo -> SIMBOLO;");
        int token = L.yylex();
        System.out.println(L.yytext);
        if (token == 30) { // Token para símbolo
            //System.out.println("Condición IF de LadoIzquierdo");
            Simb.setS(L.yytext);                    
            Vn.add(Simb.getS().substring(1, Simb.getS().length() - 1));
            System.out.println("Return true en LadoIzq.");
            return true;
        }
        return false;
    }

    private boolean LadosDerechos(ObjetoCadena SimbIzq) {
        System.out.println("LadosDerechos -> LadoDerecho LadosDerechosP;");
        List<Nodo> l = new ArrayList<>();
        if (LadoDerecho(l)) {
            Regla regla = new Regla();
            regla.nameSimbolo = SimbIzq.getS().substring(1, SimbIzq.getS().length() - 1);
            regla.lista = l;
            
            for(Nodo n : regla.lista)
                n.isTerminal = false;
            
            arrReglas.add(regla);
            NumReglas++;
            
            if(LadosDerechosP(SimbIzq)){
                System.out.println("Return true en LadosDerechos.");
                return true;
            }
        }
        return false;
    }
    
    private boolean LadosDerechosP(ObjetoCadena SimbIzq) {
        System.out.println("LadosDerechosP -> OR LadoDerecho LadosDerechosP | epsilon");
        List<Nodo> l = new ArrayList<>();
        int token;
        token = L.yylex();
        System.out.println(L.yytext);
        if(token == 40) {
            if(LadoDerecho(l)) {
                Regla regla = new Regla();
                regla.nameSimbolo = SimbIzq.getS().substring(1, SimbIzq.getS().length() - 1);
                regla.lista = l;
                
                for(Nodo n : regla.lista)
                    n.isTerminal = false;
                
                arrReglas.add(regla);
                NumReglas++;
                
                if(LadosDerechosP(SimbIzq)){
                    System.out.println("Return true en LadosDerechosP.");
                    return true;
                }
            }            
            return false;
        }
        L.UndoToken();
        System.out.println("Return true en LadosDerechosP con un epsilon.");
        return true;
    }

    private boolean LadoDerecho(List<Nodo> l) {
        System.out.println("LadoDerecho -> SecSimbolos;");
        if(SecSimbolos(l)){
            System.out.println("Return true en LadoDerecho.");
            return true;
        }
        return false;
    }

    private boolean SecSimbolos(List<Nodo> l) {
        System.out.println("SecSimbolos -> SIMBOLO SecSimbolosP;");
        int token;
        ObjetoCadena simb = new ObjetoCadena();
        token = L.yylex();
        System.out.println(L.yytext);
        if (token == 30) { // Token del símbolo
            simb.setS(L.yytext());
            simb.setS(simb.getS().substring(1, simb.getS().length() - 1));
            l.add(new Nodo(simb.getS(), false));
            if(SecSimbolosP(l)){
                System.out.println("Retorno verdadero en Simbolos.");
                return true;
            }
        }
        return false;
    }

    private boolean SecSimbolosP(List<Nodo> l) {
        System.out.println("SecSimbolosP -> SIMBOLO SecSimbolosP | Epsilon;");
        int token;
        ObjetoCadena simb = new ObjetoCadena();
        token = L.yylex();
        System.out.println(L.yytext);

        if (token == 30) { // Token símbolo
            simb.setS(L.yytext());
            simb.setS(simb.getS().substring(1, simb.getS().length() - 1));
            Nodo nodo = new Nodo(simb.getS(), false);
        
            l.add(nodo); // Agregar el nodo actuL a la lista ANTES de la llamada recursiva
        
            if (SecSimbolosP(l)) {
                System.out.println("Return true en SecSimbolosP.");
                return true;
            }
        
            return false;
        }
        L.UndoToken();
        System.out.println("Return true en SecSimbolosP con un epsilon.");
        return true;
    }
    
    public void identificarTerminales() {
        /*
        for (Regla regla : arrReglas) {
            for (Nodo N : regla.lista) {
                if (!Vn.contains(N.nameSimbolo) && !N.nameSimbolo.equals("Epsilon")) {
                    N.isTerminal = true;
                    Vt.add(N.nameSimbolo);
                }
            }
        }
        */
        int i;
        Regla regla = new Regla();
        for (i = 0; i < NumReglas; i++){
            for(Nodo N : regla.lista){
                if(!Vn.contains(N.nameSimbolo) && !N.nameSimbolo.equals("Epsilon")){
                    N.isTerminal = true;
                    Vt.add(N.nameSimbolo);
                }
            }
        }
        System.out.println("Terminales identificados: " + Vt);
        System.out.println("No Terminales identificados: " + Vn);
    }
}
