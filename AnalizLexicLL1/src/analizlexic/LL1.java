/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizlexic;

import java.util.Stack;
import java.util.Map;

/**
 *
 * @author leone
 */
public class LL1 {
    
    public LL1Tabla tabla;
    public Stack<String> pila; //Pila para el analisis por descenso recursivo
    public Lexic lexer; //Analizador lexico
    public int tokenActual; //Token actual determinado por el lexer
    public String lexemaActual;
    public String accion; //Accion a ejecutar durante el descenso
    public String cadenaActual;
    
    public LL1 (LL1Tabla tab, Lexic lex) {
        this.tabla = tab;
        this.pila = new Stack();
        this.lexer = lex;
        this.accion = "";
        this.cadenaActual = lexer.sigma + "$";
        
        this.pila.push("0"); //inicializamos la pila con el simbolo inicial y el fin de cadena
        this.pila.push(tab.ReglaA.get(0).nameSimbolo); //simbolo inicial de la gramatica G
        
        //obtencion del primer token del lexer mediante yylex
        this.tokenActual = lexer.yylex();
        if(tokenActual == 0){
            System.out.println("Error: Primer token no encontrado. Por favor verifique el analizador lexico");
        }
        this.lexemaActual = lexer.yytext(); //accedemos al lexema
        
        System.out.println("Token inicial: " + tokenActual + ". Lexema: " + lexemaActual);
    }
    
    /*----- METODO PRINCIPAL PARA REALIZAR EL ANALISIS SINTACTICO POR DESCENSO RECURSIVO -----*/
    public boolean analisisRecursivo(){
        System.out.println("Lista de tokens:");
        //bucles forEach anidados para el descenso a traves de reglas y sus respectivos nodos 
        for(Regla r : tabla.ReglaA){
            for(Nodo n : r.lista){
                System.out.println("Token de " + n.nameSimbolo + ": " + n.token);
            }
        }
        System.out.println("Contenido de la pila\tToken actual\tLexema\tAcción");
        cadenaActual = lexer.sigma + "$"; //Recordamos que la pila inicia con $
        
        while (!pila.isEmpty()){
            String top = pila.peek(); //simbolo en la cima de la pila
            accion = ""; //accion de reset
            imprimirEstadoPila(top);
            
            //verificamos si el tope de la pila es un simbolo terminal, lo que deberia de efectuar un pop en la pila actual
            if(isTerminal(top)){
                if(Integer.parseInt(top) == tokenActual) { //Y ademas se verifica que el tope de la pila sea el mismo que el token actual de la cadena ingresada
                    accion = "pop";
                    System.out.println(accion);
                    pila.pop(); //sacamos el ultimo elemento de la pila para luego pedir otro token
                    cadenaActual = cadenaActual.substring(1); //lo mismo que $1 en un LR1
                    tokenActual = lexer.yylex(); //Aqui pedimos el siguiente token
                    lexemaActual = lexer.yytext(); //Accedemos al contenido del yylex  
                } else {
                    System.out.println("Error: El token se esperaba que coincidiera:  " + tokenActual + " (" + lexemaActual + ")");
                    return false;
                }
            } else { //El tope es un no terminal, por lo que debemos sustituirlo por los no terminales de la regla asociada a la accion en un orden inverso
                String regla = getProduccion(top, tokenActual);
                if(regla == null) { //Si la regla no contiene nada, no hay produccion valida para el tope de la pila
                    System.out.println("Error: Produccion no válida para el tope de la pila. [" + top + "][" + tokenActual + "]");
                    return false;
                } else { //El tope sigue siendo un no terminal, pero si se tiene una regla diferente de nulo
                    accion = regla + "," + top; //La salida es de tipo "FT',5" como se vio en clase
                    System.out.println(accion);
                    pila.pop(); //Se hace un pop para luego realizar una insercion de los simbolos de la produccion en la pila, en un orden de derecha a izquierda
                    
                    String[] simbolos = regla.split(" ");
                    for(int i = simbolos.length -1; i >= 0; i--){ //recorrido inverso (decremental)
                        if(!simbolos[i].equals("Epsilon")){ //Si el simbolo es un epsilon, evidentemente no se agrega nada a la pila
                            pila.push(simbolos[i]);
                        }
                    }
                }
            }
        }
        
        //Finalmente, verificamos que la pila se encuentre vacia y que el token actual sea de tipo $ con $ (Aceptacion)
        if(pila.isEmpty() && tokenActual == SimbolosEspeciales.FIN){
            accion = "Accept";
            System.out.println(accion);
            imprimirEstadoPila(""); //Pila vacia
            return true;
        }
        return false;
    }
    
    private void imprimirEstadoPila(String tope) {
        String pilaEstado = pila.toString().replace("[", "").replace("]", "").replace(", ", " ");
        System.out.println(pilaEstado + "\t" + tokenActual + "\t" + lexemaActual + "\t" + accion);
    }
    
    private boolean isTerminal(String simb) {
        // Un terminal se representa como un número entero (token)
        try {
            Integer.parseInt(simb);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getProduccion(String noTerminal, int terminal) {
        //Se convierte el token terminal a una cadena reconocible por el programa
        String tokenCadena = String.valueOf(terminal);
        
        //Busqueda en la tabla LL1
        if (tabla.tablaLL1.containsKey(noTerminal)){
            Map<String, String> prods = tabla.tablaLL1.get(noTerminal);
            if(prods.containsKey(tokenCadena)){
                return prods.get(tokenCadena);
            }
        }
        return null; //Es decir, no existe la produccion
    }
    
}
