package analizlexic;
/**
 * Clase que representa un conjunto de estados Si, utilizado durante la construcción
 * de un AFD a partir de un AFN. Este conjunto representa un "estado compuesto"
 * en el proceso de determinización.
 * 
 * @author Akari Aguilera
 */

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

class Si {

    /**
     * Identificador del conjunto de estados (Si).
     * Se utiliza para asignar un ID único a cada conjunto generado en el AFD.
     */
    public int j;

    /**
     * Conjunto de objetos Estado que forman parte del conjunto Si.
     * Este conjunto representa los estados del AFN que se agrupan para
     * formar un estado determinista en el AFD.
     */
    public Set<Estado> ConjI;

    /**
     * Arreglo que representa las transiciones posibles desde este conjunto de estados
     * para cada símbolo del alfabeto. El índice del arreglo representa el valor ASCII
     * del símbolo, y el valor almacenado indica el índice del conjunto destino.
     * 
     * Ejemplo: TransicionesAFD[97] = 2 significa que con el símbolo 'a' (ASCII 97)
     * se transiciona al conjunto con ID 2.
     */
    public int[] TransicionesAFD;

    /**
     * Constructor del conjunto Si. Inicializa el conjunto de estados como vacío
     * y el arreglo de transiciones con -1 (sin transición).
     */
    public Si() {
        j = -1; // Por defecto se inicializa en -1 hasta que se asigne su número real
        ConjI = new HashSet<>(); // Se crea el conjunto vacío de estados
        TransicionesAFD = new int[257]; // Para cubrir todos los símbolos ASCII + extra
        Arrays.fill(TransicionesAFD, -1); // Todas las transiciones comienzan sin destino
    }
}
