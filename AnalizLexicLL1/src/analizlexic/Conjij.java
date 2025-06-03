package analizlexic;
/**
 *
 * @author Akari Aguilera
 */
import java.util.HashSet;
import java.util.Arrays;
import javax.swing.JFrame;
/**
 * Esta clase representa un conjunto de estados Ij, que se utiliza para construir un AFD
 * a partir de un AFN, aplicando el algoritmo de subconjuntos (o subconjuntos deterministas).
 * Aquí se guarda el grupo de estados del AFN que forman un solo estado del AFD,
 * y se maneja la tabla de transiciones para cada símbolo del alfabeto.
 */
public class Conjij extends JFrame {

    /**
     * Número identificador del conjunto Ij (por ejemplo: I0, I1, I2...).
     */
    public int j;

    /**
     * Conjunto de estados del AFN que están agrupados en este conjunto Ij.
     */
    public HashSet<Estado> ConjI;

    /**
     * Arreglo donde cada posición representa una transición para un símbolo del alfabeto.
     * La última posición se usa para almacenar el token (si aplica).
     * Si no hay transición definida, se deja como -1.
     */
    public int[] TransicionesAFD;

    /**
     * Constructor que crea el conjunto Ij con una cantidad de símbolos conocida.
     *
     * @param CardAlf Número de símbolos en el alfabeto del AFN (tamaño del arreglo de transiciones).
     */
    public Conjij(int CardAlf) {
        j = -1; // Se inicia sin número asignado
        ConjI = new HashSet<>(); // Se crea el conjunto de estados
        ConjI.clear(); // Por seguridad, se limpia el conjunto (aunque ya está vacío)
        TransicionesAFD = new int[CardAlf + 1]; // El +1 es para guardar el token en la última posición
        Arrays.fill(TransicionesAFD, -1); // Todas las transiciones se inicializan con -1 (sin transición)
    }

    /**
     * Agrega un estado al conjunto Ij.
     * @param estado Estado del AFN que queremos añadir a este conjunto.
     */
    public void agregarEstado(Estado estado) {
        ConjI.add(estado);
    }

    /**
     * Devuelve todos los estados que forman parte de este conjunto.
     * @return Conjunto de estados del tipo HashSet<Estado>.
     */
    public HashSet<Estado> obtenerConjuntoEstados() {
        return ConjI;
    }

    /**
     * Establece hacia qué conjunto se transita al leer un símbolo.
     * @param simbolo Índice del símbolo (en la tabla de transiciones).
     * @param estadoDestino El conjunto Ij al que se debe ir (representado como un número).
     */
    public void establecerTransicion(int simbolo, int estadoDestino) {
        TransicionesAFD[simbolo] = estadoDestino;
    }

    /**
     * Devuelve el índice del conjunto destino (Ij) para el símbolo dado.
     * @param simbolo Índice del símbolo.
     * @return Número del conjunto destino, o -1 si no existe transición.
     */
    public int obtenerEstadoDestino(int simbolo) {
        return TransicionesAFD[simbolo];
    }

    /**
     * Asocia un token al conjunto, en caso de que alguno de sus estados sea de aceptación.
     * @param token El número de token que se desea asignar.
     */
    public void establecerToken(int token) {
        TransicionesAFD[TransicionesAFD.length - 1] = token;
    }

    /**
     * Obtiene el token asociado a este conjunto.
     * @return El token si existe, o -1 si no tiene asignado ninguno.
     */
    public int obtenerToken() {
        return TransicionesAFD[TransicionesAFD.length - 1];
    }
}
