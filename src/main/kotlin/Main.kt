
import ru.ifmo.ctddev.isaev.algorithm.NewNetwork
import ru.ifmo.ctddev.isaev.algorithm.readDataSet

/**
 * @author iisaev
 */
fun main(args: Array<String>) {
    val network = NewNetwork(784, 50, 10, readDataSet())
    val b = true
}