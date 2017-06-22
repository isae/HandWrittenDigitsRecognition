import org.junit.Test
import ru.ifmo.ctddev.isaev.algorithm.NewNetwork
import ru.ifmo.ctddev.isaev.algorithm.readDataSet
import kotlin.test.assertTrue

/**
 * @author iisaev
 */

class TestAccuracy {


    private val dataset = readDataSet()
    private val DATASET_SIZE = 500
    private val TRAIN_SIZE = (DATASET_SIZE * 0.8).toInt()
    private val TEST_SIZE = 1000
    private val network = NewNetwork(784, 50, 10, dataset.subList(0, TRAIN_SIZE), false)

    @Test
    fun testAccuracy() {
        val testSet = dataset.subList(TRAIN_SIZE, TRAIN_SIZE + TEST_SIZE)
        val correct = testSet.sumBy { (data, expected) ->
            val actual = network.predictResult(data)
            if (expected == actual) 1 else 0
        }
        val accuracy = correct.toDouble() / testSet.size
        println("Accuracy: ${accuracy * 100}%")
        assertTrue(accuracy > 0.9, "Accuracy > 90%")
    }
}