package analizlexic;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Esta clase se encarga de convertir una expresión regular a un autómata finito no determinista (AFN),
 * utilizando el método de Thompson. Soporta operadores como:
 * - | para unión,
 * - & para concatenación (explícita),
 * - * cerradura de Kleene,
 * - + cerradura positiva,
 * - ? opcional,
 * - paréntesis para agrupar,
 * - rangos tipo [a-z], [0-9], etc.
 */
public class ER_AFN {
    private final String regExp;        // Expresión regular ya expandida
    private final Stack<AFN> pila;      // Pila para construir el AFN
    private int index = 0;              // Índice del carácter actual en la expresión
    private int idAFN;                  // ID que se asignará a los AFNs generados

    // Constructor que recibe la expresión regular y un ID para el AFN
    public ER_AFN(String regex, int idAFN) {
        this.regExp = expandirRangos(regex);
        this.idAFN = idAFN;
        this.pila = new Stack<>();
    }

    /**
     * Este método detecta rangos tipo [a-z] y los convierte a una forma entendible por el algoritmo:
     * Por ejemplo: [a-c] → (a|b|c)
     */
    private String expandirRangos(String regex) {
        Pattern pattern = Pattern.compile("\\[([a-zA-Z0-9])-([a-zA-Z0-9])\\]");
        Matcher matcher = pattern.matcher(regex);
        StringBuffer resultado = new StringBuffer();

        while (matcher.find()) {
            char inicio = matcher.group(1).charAt(0);
            char fin = matcher.group(2).charAt(0);
            StringBuilder reemplazo = new StringBuilder("(");
            for (char c = inicio; c <= fin; c++) {
                reemplazo.append(c).append("|");
            }
            reemplazo.deleteCharAt(reemplazo.length() - 1).append(")");
            matcher.appendReplacement(resultado, Matcher.quoteReplacement(reemplazo.toString()));
        }
        matcher.appendTail(resultado);
        return resultado.toString();
    }

    /**
     * Método principal que inicia el análisis de la expresión y retorna el AFN final.
     * Si la expresión es válida, construye y devuelve el AFN. Si no, devuelve null.
     */
    public AFN construir() {
        index = 0;
        if (E() && pila.size() == 1) {
            AFN afn = pila.pop();
            afn.setIdAFN(idAFN);  // Asignar el ID al AFN generado
            return afn; // El resultado final es el único AFN que queda en la pila
        } else {
            System.err.println("Error: Expresión regular inválida.");
            return null;
        }
    }

    // E → T E'
    public boolean E() {
        if (T()) {
            while (peek('|')) {
                consume(); // consumir '|'
                if (!T()) return false;
                AFN f2 = pila.pop();
                AFN f1 = pila.pop();
                f1.unirAFNs(f2);  // Operación de unión
                pila.push(f1);
            }
            return true;
        }
        return false;
    }

    // T → C T'
    private boolean T() {
    if (C()) {
        while (index < regExp.length() && 
              (isInicioFactor(regExp.charAt(index)) || peek('&'))) {
            if (peek('&')) {
                consume(); // consumir '&'
            }
            if (!C()) return false;
            AFN f2 = pila.pop();
            AFN f1 = pila.pop();
            f1.concatenarAFNs(f2);
            pila.push(f1);
        }
        return true;
    }
    return false;
}

    // C → F C'
    private boolean C() {
        if (F()) {
            while (peek('*') || peek('+') || peek('?')) {
                char op = consume(); // *, + o ?
                AFN f = pila.pop();
                switch (op) {
                    case '*' -> f.cerraduraKleene();  // Cerradura de Kleene
                    case '+' -> f.cerraduraPositiva();  // Cerradura positiva
                    case '?' -> f.opcional();  // Opcional
                }
                pila.push(f);
            }
            return true;
        }
        return false;
    }

    // F → (E) | símbolo
    private boolean F() {
        if (peek('(')) {
            consume(); // consumir '('
            if (!E() || !match(')')) return false;
            return true;
        } else if (index < regExp.length()) {
            char actual = regExp.charAt(index);
            if (!isOperador(actual)) {
                AFN f = new AFN();
                f = f.crearAFNBasico(actual, actual, idAFN);
                pila.push(f);
                index++;
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si un carácter puede iniciar un nuevo factor (símbolo o paréntesis).
     */
    private boolean isInicioFactor(char c) {
        return c == '(' || (!isOperador(c) && c != '&');
    }

    /**
     * Verifica si un carácter es uno de los operadores que manejamos.
     */
    private boolean isOperador(char c) {
        return c == '*' || c == '+' || c == '?' || c == '|' || c == ')' || c == '&';
    }

    /**
     * Revisa si el carácter actual en la expresión coincide con el esperado.
     */
    private boolean peek(char c) {
        return index < regExp.length() && regExp.charAt(index) == c;
    }

    /**
     * Verifica y consume un carácter específico.
     */
    private boolean match(char c) {
        if (peek(c)) {
            index++;
            return true;
        }
        return false;
    }

    /**
     * Devuelve el carácter actual y avanza al siguiente.
     */
    private char consume() {
        return regExp.charAt(index++);
    }
}
