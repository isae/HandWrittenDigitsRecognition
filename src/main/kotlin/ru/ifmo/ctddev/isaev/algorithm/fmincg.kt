package ru.ifmo.ctddev.isaev.algorithm

val RHO = 0.01                            //a bunch of constants for line searches
val SIG = 0.5       //RHO and SIG are the constants in the Wolfe-Powell conditions
val INT = 0.1    //don't reevaluate within 0.1 of the limit of the current bracket
val EXT = 3.0                   //extrapolate maximum 3 times the current bracket
val MAX = 20                        //max 20 function evaluations per line search
val RATIO = 100.0                                     //maximum allowed slope ratio
val realmin = 2.2251e-308

fun fmincg(f: (DoubleArray) -> CostGradientTuple,
           originalX: DoubleArray,
           length: Int): Triple<DoubleArray, ArrayList<Double>, Int> {

    var X = originalX

    val red = 1.0

    var i = 0                                           //zero the run length counter
    var ls_failed = false                             //no previous line search has failed
    val fX = ArrayList<Double>()
    var (f1, df1) = f(X)
    i += getInt(length < 0)                                            //count epochs?!
    var s = -df1                                        //search direction is steepest
    var d1 = -s.dot(s)                                                 //this is the slope
    var z1 = red / (1.0 - d1)                                 //initial step is red/(|s|+1)

    while (i < Math.abs(length)) {                                      //while not finished
        i += getInt(length > 0)                                      //count iterations?!

        val X0 = X
        val f0 = f1
        val df0 = df1                   //make a copy of current values
        X += z1 * s
        var (f2, df2) = f(X)
        i += getInt(length < 0)                                          //count epochs?!
        var d2 = df2.dot(s)
        var f3 = f1
        var d3 = d1
        var z3 = -z1             //initialize point 3 equal to point 1
        var M: Int
        if (length > 0) {
            M = MAX
        } else {
            M = Math.min(MAX, -length - i)
        }
        var success = false
        var limit = -1.0                    //initialize quanteties
        while (true) {
            while (((f2 > (f1 + z1 * RHO * d1)) || (d2 > -SIG * d1)) && (M > 0)) {
                limit = z1                                         //tighten the bracket
                var z2: Double
                if (f2 > f1) {
                    z2 = z3 - (0.5 * d3 * z3 * z3) / (d3 * z3 + f2 - f3) //quadratic fit
                } else {
                    val A = 6.0 * (f2 - f3) / z3 + 3.0 * (d2 + d3)                                 //cubic fit
                    val B = 3.0 * (f3 - f2) - z3 * (d3 + 2.0 * d2)
                    z2 = (Math.sqrt(B * B - A * d2 * z3 * z3) - B) / A       //numerical error possible - ok!
                }
                if (z2.isNaN() || z2.isInfinite()) {
                    z2 = z3 / 2.0                  //if we had a numerical problem then bisect
                }
                z2 = Math.max(Math.min(z2, INT * z3), (1 - INT) * z3)  //don't accept too close to limits
                z1 += z2                                           //update the step
                X += z2 * s
                val (tmpf2, tmpdf2) = f(X)
                f2 = tmpf2
                df2 = tmpdf2
                M -= 1
                i += getInt(length < 0)                           //count epochs?!
                d2 = df2.dot(s)
                z3 -= z2                    //z3 is now relative to the location of z2
            }
            if (f2 > (f1 + z1 * RHO * d1) || (d2 > -SIG * d1)) {
                break                                                //this is a failure
            } else if (d2 > SIG * d1) {
                success = true
                break                                             //success
            } else if (M == 0) {
                break
            }                                             //failure
            val A = 6.0 * (f2 - f3) / z3 + 3 * (d2 + d3)                      //make cubic extrapolation
            val B = 3.0 * (f3 - f2) - z3 * (d3 + 2 * d2)
            var z2 = -d2 * z3 * z3 / (B + Math.sqrt(B * B - A * d2 * z3 * z3))       //num. error possible - ok!
            if (/*!isreal(z2) ||*/ z2.isNaN() || z2.isInfinite() || z2 < 0) {   //num prob or wrong sign?
                if (limit < -0.5) {                               //if we have no upper limit
                    z2 = z1 * (EXT - 1.0)                 //the extrapolate the maximum amount
                } else {
                    z2 = (limit - z1) / 2.0                                   //otherwise bisect
                }
            } else if ((limit > -0.5) && (z2 + z1 > limit)) {          //extraplation beyond max?
                z2 = (limit - z1) / 2.0                                               //bisect
            } else if ((limit < -0.5) && (z2 + z1 > z1 * EXT)) {       //extrapolation beyond limit
                z2 = z1 * (EXT - 1.0)                           //set to extrapolation limit
            } else if (z2 < (-z3 * INT)) {
                z2 = -z3 * INT
            } else if ((limit > -0.5) && (z2 < (limit - z1) * (1.0 - INT))) {   //too close to limit?
                z2 = (limit - z1) * (1.0 - INT)
            }
            f3 = f2
            d3 = d2
            z3 = -z2                 //set point 3 equal to point 2
            z1 += z2
            X += z2 * s                     //update current estimates
            val (tmpf2, tmpdf2) = f(X)
            f2 = tmpf2
            df2 = tmpdf2
            M -= 1
            i += getInt(length < 0)                             //count epochs?!
            d2 = df2.dot(s)
        }                                         //end of line search

        if (success) {                                         //if line search succeeded
            f1 = f2
            fX.add(f1)
            println("Iteration $i | Cost: $f1")
            s = (df2.dot(df2) - df1.dot(df2)) / (df1.dot(df1)) * s - df2      //Polack-Ribiere direction
            val tmp = df1
            df1 = df2
            df2 = tmp                         //swap derivatives
            d2 = df1.dot(s)
            if (d2 > 0) {                                      //new slope must be negative
                s = -df1                              //otherwise use steepest direction
                d2 = -s.dot(s)
            }
            z1 *= Math.min(RATIO, d1 / (d2 - realmin))          //slope ratio but max RATIO
            d1 = d2
            ls_failed = false                             //this line search did not fail
        } else {
            X = X0
            f1 = f0
            df1 = df0  //restore point from before failed line search
            if (ls_failed || i > Math.abs(length)) {          //line search failed twice in a row
                break                             //or we ran out of time, so we give up
            }
            val tmp = df1
            df1 = df2
            df2 = tmp                         //swap derivatives
            s = -df1                                                    //try steepest
            d1 = -s.dot(s)
            z1 = 1.0 / (1.0 - d1)
            ls_failed = true                                    //this line search failed
        }
    }
    return Triple(X, fX, i)
}

private operator fun Double.times(s: DoubleArray): DoubleArray {
    return s * this
}

private operator fun DoubleArray.unaryMinus(): DoubleArray {
    return this.map { -it }
            .toDoubleArray()
}

fun getInt(cause: Boolean): Int {
    return if (cause) 1 else 0
}

operator fun DoubleArray.minus(other: DoubleArray): DoubleArray {
    return this.zip(other)
            .map { it.first - it.second }
            .toDoubleArray()
}

@Override
operator fun DoubleArray.plus(other: DoubleArray): DoubleArray {
    return this.zip(other)
            .map { it.first + it.second }
            .toDoubleArray()
}


private fun DoubleArray.dot(other: DoubleArray): Double {
    return this.zip(other)
            .map { it.first * it.second }
            .sum()
}

private operator fun DoubleArray.times(toMultiply: Double): DoubleArray {
    return this.map { it * toMultiply }
            .toDoubleArray()
}
