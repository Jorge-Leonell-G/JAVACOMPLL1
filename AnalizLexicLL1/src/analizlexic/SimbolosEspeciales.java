package analizlexic;

/**
 * Clase que contiene constantes utilizadas para representar los diferentes tipos de símbolos
 * o tokens que puede encontrar el analizador léxico.
 * 
 * También incluye algunas utilidades para validar si un tipo corresponde a operadores,
 * delimitadores, tipos de dato, etc.
 * 
 * Esta clase funciona como referencia central para toda la categorización léxica.
 * 
 * @author Akari Aguilera
 */
public class SimbolosEspeciales {

    // Constantes para control de flujo del análisis
    public static final int ERROR = -1; // Indica que hubo un error al reconocer un símbolo
    public static final int FIN = 0;    // Marca el final del análisis (token "$")

    // Representación de epsilon (transición vacía) como carácter nulo
    public static final String EPSILON = "\0";

    // ==========================
    // MÉTODOS DE UTILIDAD
    // ==========================

    /**
     * Verifica si el símbolo ingresado es uno de los operadores de expresiones regulares.
     */
    public static boolean esOperadorLexico(String simbolo) {
        return simbolo.equals("|") || simbolo.equals("*") || simbolo.equals("+") || simbolo.equals("?") || simbolo.equals(".");
    }

    // ==========================
    // CATEGORÍAS DE TOKENS
    // ==========================

    // Identificadores y tipos primitivos de datos
    public static final String IDENTIFICADOR   = "IDENTIFICADOR";
    public static final String NUMERO_ENTERO   = "NUMERO_ENTERO";
    public static final String NUMERO_REAL     = "NUMERO_REAL";
    public static final String CADENA          = "CADENA";
    public static final String CARACTER        = "CARACTER";
    public static final String COMENTARIO      = "COMENTARIO";

    // Operadores aritméticos
    public static final String OPERADOR_SUMA            = "OPERADOR_SUMA";
    public static final String OPERADOR_RESTA           = "OPERADOR_RESTA";
    public static final String OPERADOR_MULTIPLICACION  = "OPERADOR_MULTIPLICACION";
    public static final String OPERADOR_DIVISION        = "OPERADOR_DIVISION";
    public static final String OPERADOR_MODULO          = "OPERADOR_MODULO";
    public static final String OPERADOR_ASIGNACION      = "OPERADOR_ASIGNACION";

    // Operadores relacionales
    public static final String OPERADOR_IGUAL           = "OPERADOR_IGUAL";
    public static final String OPERADOR_DIFERENTE       = "OPERADOR_DIFERENTE";
    public static final String OPERADOR_MAYOR           = "OPERADOR_MAYOR";
    public static final String OPERADOR_MENOR           = "OPERADOR_MENOR";
    public static final String OPERADOR_MAYOR_IGUAL     = "OPERADOR_MAYOR_IGUAL";
    public static final String OPERADOR_MENOR_IGUAL     = "OPERADOR_MENOR_IGUAL";

    // Palabras reservadas comunes en lenguajes de programación
    public static final String PALABRA_RESERVADA_IF     = "PALABRA_RESERVADA_IF";
    public static final String PALABRA_RESERVADA_ELSE   = "PALABRA_RESERVADA_ELSE";
    public static final String PALABRA_RESERVADA_WHILE  = "PALABRA_RESERVADA_WHILE";
    public static final String PALABRA_RESERVADA_FOR    = "PALABRA_RESERVADA_FOR";
    public static final String PALABRA_RESERVADA_RETURN = "PALABRA_RESERVADA_RETURN";

    // Tipos de datos
    public static final String TIPO_INT     = "TIPO_INT";
    public static final String TIPO_FLOAT   = "TIPO_FLOAT";
    public static final String TIPO_CHAR    = "TIPO_CHAR";
    public static final String TIPO_STRING  = "TIPO_STRING";
    public static final String TIPO_BOOLEAN = "TIPO_BOOLEAN";

    // Delimitadores (símbolos de agrupación o separación)
    public static final String PARENTESIS_ABIERTO = "PARENTESIS_ABIERTO";
    public static final String PARENTESIS_CERRADO = "PARENTESIS_CERRADO";
    public static final String LLAVE_ABIERTA      = "LLAVE_ABIERTA";
    public static final String LLAVE_CERRADA      = "LLAVE_CERRADA";
    public static final String CORCHETE_ABIERTO   = "CORCHETE_ABIERTO";
    public static final String CORCHETE_CERRADO   = "CORCHETE_CERRADO";
    public static final String PUNTO_Y_COMA       = "PUNTO_Y_COMA";
    public static final String COMA               = "COMA";

    // Otros tipos
    public static final int OMITIR     = 20001;   // Se usa cuando se quiere ignorar un lexema
    public static final String SIMBOLO    = "SIMB";     // Símbolo general
    public static final String ESCAPADO   = "\\\\";     // Para caracteres escapados en expresiones

    // Operadores específicos de expresiones regulares
    public static final String OPERADOR_OR             = "|";
    public static final String OPERADOR_CONCATENACION  = ".";
    public static final String CERRADURA_KLEENE        = "*";
    public static final String CERRADURA_POSITIVA      = "+";
    public static final String OPCIONAL                = "?";

    // ==========================
    // Clasificación para validación
    // ==========================

    /**
     * Retorna verdadero si el tipo representa un operador aritmético o lógico.
     */
    public static boolean esOperador(String tipo) {
        return tipo.startsWith("OPERADOR_");
    }

    /**
     * Retorna verdadero si el tipo representa una palabra reservada del lenguaje.
     */
    public static boolean esPalabraReservada(String tipo) {
        return tipo.startsWith("PALABRA_");
    }

    /**
     * Retorna verdadero si el tipo es uno de los tipos de datos aceptados.
     */
    public static boolean esTipoDato(String tipo) {
        return tipo.startsWith("TIPO_");
    }

    /**
     * Retorna verdadero si el tipo es un delimitador.
     */
    public static boolean esDelimitador(String tipo) {
        return tipo.equals(PARENTESIS_ABIERTO) ||
               tipo.equals(PARENTESIS_CERRADO) ||
               tipo.equals(LLAVE_ABIERTA) ||
               tipo.equals(LLAVE_CERRADA) ||
               tipo.equals(CORCHETE_ABIERTO) ||
               tipo.equals(CORCHETE_CERRADO) ||
               tipo.equals(PUNTO_Y_COMA) ||
               tipo.equals(COMA);
    }
}
