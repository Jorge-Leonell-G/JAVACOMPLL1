package analizlexic;

import java.util.*;

public class Lexic {
    public int token;
    public int estadoActual;
    public int estadoTransicion;
    public String sigma; //sigma = cadena de entrada
    public String yytext; //yytext = lexema
    public boolean isAceptacion;
    public int inicioLexema, finLexema, indiceCaracterActual;
    public char caracterActual;
    public Stack<Integer> pila;
    public AFD automataAFD;
    public StringBuilder pasoAPaso;

    private int ultimaPosAceptada = -1;
    private Estado ultimoEstadoAceptado = null;

    public Lexic() {
        this.automataAFD = null;
        this.sigma = "";
        this.isAceptacion = false;
        this.inicioLexema = -1;
        this.finLexema = -1;
        this.indiceCaracterActual = -1;
        this.token = -1;
        this.pila = new Stack<>();
        this.pasoAPaso = new StringBuilder();
    }

    //constructor que recibe cadena de entrada sigma y archivo del AFD
    public Lexic(String entrada, String archivoAFD) {
        this();
        this.automataAFD = new AFD();
        this.sigma = entrada;
        this.isAceptacion = false;
        this.inicioLexema = 0;
        this.finLexema = -1;
        this.indiceCaracterActual = 0;
        this.token = -1;
        this.pila = new Stack<>();
        this.automataAFD = AFD.leerDesdeBinario(archivoAFD);
        this.pasoAPaso = new StringBuilder();
    }

    public void SetSigma(String cadena) {
        this.sigma = cadena;
        this.indiceCaracterActual = 0;
        this.pila.clear();
    }

    /* -----METODO CLAVE PARA EL ANALISIS LEXICO VISTO EN CLASE-----*/
    public int yylex() {
        int i = indiceCaracterActual;
        while(true){
            pila.push(i);
            if(i >= sigma.length()){
                yytext = ""; //el lexema se encuentra vacio por desbordamiento
                return SimbolosEspeciales.FIN;
            }
            
            if (automataAFD == null || automataAFD.getEstadoInicial() == null) {
                return SimbolosEspeciales.ERROR;
            }
            
            inicioLexema = i;
            estadoActual = 0;
            isAceptacion = false;
            finLexema = -1;
            token = -1;
            
            estadoActual = automataAFD.getEstadoInicial().getIdEdo();
            Estado EdoActual = automataAFD.getEstadoInicial();
            
            while (i < sigma.length()) {
                caracterActual = sigma.charAt(i);
                
                //Accedemos a la tabla de transicion del AFD
                pasoAPaso.append("Estado ").append(estadoActual)
                     .append(", caracter: '").append(caracterActual).append("'\n");

                //Se verifica si el estado de la transicion es de aceptacion
                Map<String, Estado> transiciones = automataAFD.getTablaDeTransiciones().get(estadoActual);
                if (transiciones == null || !transiciones.containsKey(String.valueOf(caracterActual))) {
                    break; // No hay transición válida
                }

                Estado destino = transiciones.get(String.valueOf(caracterActual));
                if (destino == null) break;

                yytext += caracterActual;
                estadoActual = destino.getIdEdo();
                EdoActual = destino;

                if (automataAFD.getEstadosAceptacion().contains(destino)) {
                    isAceptacion = true;
                    ultimaPosAceptada = i + 1;
                    ultimoEstadoAceptado = destino;
                }
                i++;
                //estadoActual = estadoTransicion;
            }
            
            if (!isAceptacion) {
                //i = inicioLexema + 1;
                yytext = sigma.substring(inicioLexema, inicioLexema + 1);
                token = SimbolosEspeciales.ERROR;
                indiceCaracterActual = inicioLexema + 1;
                //token = ultimoEstadoAceptado.getToken();
                //i = ultimaPosAceptada;
                return token;
            } else {
                // Se detectó error, avanzar 1 posición para continuar analizando
                token = ultimoEstadoAceptado.getToken();
                //yytext = String.valueOf(sigma.charAt(i));
                yytext = sigma.substring(inicioLexema, ultimaPosAceptada);
                indiceCaracterActual = ultimaPosAceptada; // AVANZAR EL INDICE
                /*
                i++;
                token = SimbolosEspeciales.ERROR;
                */
            }
            // Extraer lexema y ajustar el índice
            /*
            yytext = sigma.substring(inicioLexema, finLexema + 1);
            indiceCaracterActual = finLexema + 1;
            */
            
            // Verificar si se omite el token, en cuyo caso se continúa
            if (token == SimbolosEspeciales.OMITIR) {
                continue;
            } else {
                return token;
            }
            //return token; 
        }
    }

    public String getResultado() {
        return "Token: " + token + ", Texto: '" + yytext + "'";
    }

    public String getStepByStep() {
        return pasoAPaso.toString();
    }

    public String getYytext() {
        return yytext;
    }

    //regresamos a la ultima posicion para cuando se requiera
    public boolean UndoToken() {
        if (pila.isEmpty()) {
            return false;
        }
        indiceCaracterActual = pila.pop();
        return true;
    }
    
    public String yytext() {
        return this.yytext;
    }

    /*
    public void analizarCadena(String cadena) {
        SetSigma(cadena);
        int tkn;
        do {
            tkn = yylex();
            // Puedes imprimir o guardar los tokens aquí también si lo deseas
        } while (tkn != SimbolosEspeciales.FIN && indiceCaracterActual < cadena.length());
    }
*/
    public List<Simbolo> analizarCadena(String cadena) {
    SetSigma(cadena);
    List<Simbolo> simbolos = new ArrayList<>();
    int tkn;
    do {
        tkn = yylex();
        if (tkn != SimbolosEspeciales.OMITIR && tkn != SimbolosEspeciales.FIN) {
            simbolos.add(new Simbolo(
                yytext, 
                tkn, 
                1,  // línea (ajusta según necesites)
                1,  // columna (ajusta según necesites)
                obtenerTipoToken(tkn)  // Método que clasifica el token
            ));
        }
    } while (tkn != SimbolosEspeciales.FIN && indiceCaracterActual < cadena.length());
    
    return simbolos;
}

private String obtenerTipoToken(int token) {
    // Implementa lógica para clasificar tokens
    if (token >= 10 && token < 20) return "Identificador";
    if (token >= 20 && token < 30) return "Número";
    // ... etc
    return "Token";
}
    
    // Método para clonar el estado actual del analizador léxico
    public Lexic getEdoAnalizLexico() {
        Lexic edoActualAnalyze = new Lexic();
        edoActualAnalyze.caracterActual = this.caracterActual;
        edoActualAnalyze.estadoActual = this.estadoActual;
        edoActualAnalyze.estadoTransicion = this.estadoTransicion;
        edoActualAnalyze.finLexema = this.finLexema;
        edoActualAnalyze.indiceCaracterActual = this.indiceCaracterActual;
        edoActualAnalyze.inicioLexema = this.inicioLexema;
        edoActualAnalyze.yytext = this.yytext;
        edoActualAnalyze.isAceptacion = this.isAceptacion;
        edoActualAnalyze.token = this.token;
        // Realizar una copia profunda de la pila
        edoActualAnalyze.pila = (Stack<Integer>) this.pila.clone();
        return edoActualAnalyze;
    }
    
    // Método para restaurar un estado previamente guardado
    public boolean setEdoAnalizLexico(Lexic e) {
        this.caracterActual = e.caracterActual;
        this.estadoActual = e.estadoActual;
        this.estadoTransicion = e.estadoTransicion;
        this.inicioLexema = e.inicioLexema;
        this.finLexema = e.finLexema;
        this.indiceCaracterActual = e.indiceCaracterActual;
        this.yytext = e.yytext;
        this.isAceptacion = e.isAceptacion;
        this.token = e.token;
        this.pila = (Stack<Integer>) e.pila.clone();
        return true;
    }
}