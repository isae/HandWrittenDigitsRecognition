import ru.ifmo.ctddev.isaev.algorithm.NeuralNetwork
import ru.ifmo.ctddev.isaev.algorithm.readDataSet

/**
 * @author iisaev
 */
fun main(args: Array<String>) {
    val network = NeuralNetwork(784, 50, 10, readDataSet())
    val b = true
}