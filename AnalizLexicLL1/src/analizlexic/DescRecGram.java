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
    public Lexic Scanner;

    public HashSet<String> Vn = new HashSet<>(); //conjunto de no terminales
    public HashSet<String> Vt = new HashSet<>(); //conjunto de terminales

    public int NumReglas = 0;
    public List<Regla> arrReglas = new ArrayList<>();

    public DescRecGram(String sigma, String FileAFD /*int idAFD*/) {
        Gramatica = sigma;
        Scanner = new Lexic(Gramatica, FileAFD);
        Vn.clear();
        Vt.clear();
    }

    public DescRecGram(String FileAFD /*int idAFD*/) {
        Scanner = new Lexic("", FileAFD);
        Vn.clear(); //
        Vt.clear();
    }

    public boolean SetGramatica(String sigma) {
        Gramatica = sigma;
        Scanner.SetSigma(sigma);
        return true;
    }

    public boolean AnalizarGramatica() {
        int token;
        System.out.println("Analizando gramática...");
        if (G()) {
            token = Scanner.yylex();
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
    
    /*Las primeras funciones no llevaran parametros ya que no se van a efectuar acciones semanticas por ahora*/
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
            token = Scanner.yylex();
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
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
        Lexic estadoScanner = Scanner.getEdoAnalizLexico(); //guardamos el estado del analizador lexico para la operación undoToken
        if (Reglas()) { //luego de guardar el estado, ahora si podemos llamar al siguiente metodo correspondiente
            token = Scanner.yylex(); //pedimos el token
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
            if (token == 10) { // Punto y coma ;
                if (ListaReglasP()) {
                    System.out.println("Return true en ListaReglasP.");
                    return true;
                }
            }
            return false;
        }
        Scanner.setEdoAnalizLexico(estadoScanner); //undoScanner
        System.out.println("Return true en ListaReglasP con un epsilon.");
        return true;
    }

    private boolean Reglas() {
        System.out.println("Reglas -> LadoIzquierdo FLECHA LadosDerechos;");
        int token;
        ObjetoCadena simboloIzquierdo = new ObjetoCadena();
        if (LadoIzquierdo(simboloIzquierdo)) {            
            token = Scanner.yylex();
            System.out.println("Se ha obtenido el token: " + Scanner.yytext);
            if (token == 20) { // Flecha ->
                if (LadosDerechos(simboloIzquierdo)) {
                    System.out.println("Return true en Reglas.");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean LadoIzquierdo(ObjetoCadena simb) {
        System.out.println("LadoIzquierdo -> SIMBOLO;");
        int token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);
        if (token == 30) { // Token para símbolo
            //System.out.println("Condición IF de LadoIzquierdo");
            simb.setCadena(Scanner.yytext);    
            //simb = Scanner.yylex();
            Vn.add(simb.getCadena()); //agregamos los simb como no terminales
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
        token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);
        if(token == 40) {
            if(LadoDerecho(l)) {
                Regla regla = new Regla();
                regla.nameSimbolo = SimbIzq.getCadena();
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
        Scanner.UndoToken();
        System.out.println("Return true en LadosDerechosP con un epsilon.");
        return true;
    }

    private boolean LadoDerecho(List<Nodo> lista) {
        System.out.println("LadoDerecho -> SecSimbolos;");
        if(SecSimbolos(lista)){
            System.out.println("Return true en LadoDerecho.");
            return true;
        }
        return false;
    }

    private boolean SecSimbolos(List<Nodo> lista) {
        System.out.println("SecSimbolos -> SIMBOLO SecSimbolosP;");
        Nodo n;
        int token;
        ObjetoCadena simb = new ObjetoCadena();
        token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);
        if (token == 30) { // Token del símbolo
            simb.setCadena(Scanner.yytext());
            simb.setCadena(simb.getCadena());
            lista.add(new Nodo(simb.getCadena(), false));
            if(SecSimbolosP(lista)){
                System.out.println("Retorno verdadero en Simbolos.");
                return true;
            }
        }
        return false;
    }

    private boolean SecSimbolosP(List<Nodo> lista) {
        System.out.println("SecSimbolosP -> SIMBOLO SecSimbolosP | Epsilon;");
        int token;
        ObjetoCadena simb = new ObjetoCadena();
        token = Scanner.yylex();
        System.out.println("Se ha obtenido el token: " + Scanner.yytext);

        if (token == 30) { // Token símbolo
            simb.setCadena(Scanner.yytext());
            simb.setCadena(simb.getCadena());
            Nodo nodo = new Nodo(simb.getCadena(), false);
        
            lista.add(nodo); // Agregar el nodo actual a la lista ANTES de la llamada recursiva
        
            if (SecSimbolosP(lista)) {
                System.out.println("Return true en SecSimbolosP.");
                return true;
            }
        
            return false;
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
        
        /*
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
        */
        System.out.println("Terminales identificados: " + Vt);
        System.out.println("No Terminales identificados: " + Vn);
    }
}
