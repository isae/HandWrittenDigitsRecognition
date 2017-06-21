package ru.ifmo.ctddev.isaev.algorithm

// extrapolate maximum 3 times the current bracket.
// this can be set higher for bigger extrapolations
var EXT = 3.0

// a bunch of constants for line searches
private val RHO = 0.01
// RHO and SIG are the constants in the Wolfe-Powell conditions
private val SIG = 0.5
// don't reevaluate within 0.1 of the limit of the current bracket
private val INT = 0.1
// max 20 function evaluations per line search
private val MAX = 20
// maximum allowed slope ratio
private val RATIO = 100.0

private var counter = 1

fun fmincg(f: (DoubleArray) -> CostGradientTuple,
           theta: DoubleArray,
           length: Int): DoubleArray {
    var input = theta
    var M = 0
    var i = 0 // zero the run length counter
    val red = 1.0 // starting point
    var ls_failed = 0 // no previous line search has failed
    val evaluateCost = f(input)
    var f1 = evaluateCost.cost
    var df1 = evaluateCost.gradient
    i += (if (length < 0) 1 else 0)
    // search direction is steepest
    var s = df1 * -1.0

    var d1 = (s * -1.0).dot(s) // this is the slope
    var z1 = red / (1.0 - d1) // initial step is red/(|s|+1)

    while (i < Math.abs(length)) {// while not finished
        i += (if (length > 0) 1 else 0)// count iterations?!
        // make a copy of current values
        val X0 = input.copyOf()
        val f0 = f1
        val df0 = df1.copyOf()
        // begin line search
        input += (s * z1)
        val evaluateCost2 = f(input)
        var f2 = evaluateCost2.cost
        var df2 = evaluateCost2.gradient

        i += (if (length < 0) 1 else 0) // count epochs
        var d2 = df2.dot(s)
        // initialize point 3 equal to point 1
        var f3 = f1
        var d3 = d1
        var z3 = -z1
        if (length > 0) {
            M = MAX
        } else {
            M = Math.min(MAX, -length - i)
        }
        // initialize quanteties
        var success = false
        var limit = -1.0

        while (true) {
            println("\tStarted iteration ${++counter}")
            while (((f2 > f1 + z1 * RHO * d1) || (d2 > -SIG * d1)) && (M > 0.0)) {
                // tighten the bracket
                limit = z1
                var z2: Double
                val A: Double
                val B: Double
                if (f2 > f1) {
                    // quadratic fit
                    z2 = z3 - (0.5 * d3 * z3 * z3) / (d3 * z3 + f2 - f3)
                } else {
                    // cubic fit
                    A = 6.0 * (f2 - f3) / z3 + 3 * (d2 + d3)
                    B = 3.0 * (f3 - f2) - z3 * (d3 + 2.0 * d2)
                    // numerical error possible - ok!
                    z2 = (Math.sqrt(B * B - A * d2 * z3 * z3) - B) / A
                }
                if (z2.isNaN() || z2.isInfinite()) {
                    // if we had a numerical problem then bisect
                    z2 = z3 / 2.0
                }
                // don't accept too close to limits
                z2 = Math.max(Math.min(z2, INT * z3), (1.0 - INT) * z3)
                // update the step
                z1 += z2
                input += s * z2
                val evaluateCost3 = f(input)
                f2 = evaluateCost3.cost
                df2 = evaluateCost3.gradient
                M -= 1
                i += (if (length < 0) 1 else 0) // count epochs
                d2 = df2.dot(s)
                // z3 is now relative to the location of z2
                z3 -= z2
                println("\tFinished iteration ${counter}")
            }
            if (f2 > f1 + z1 * RHO * d1 || d2 > -SIG * d1) {
                break // this is a failure
            } else if (d2 > SIG * d1) {
                success = true
                break // success
            } else if (M == 0) {
                break // failure
            }
            // make cubic extrapolation
            val A = 6.0 * (f2 - f3) / z3 + 3.0 * (d2 + d3)
            val B = 3.0 * (f3 - f2) - z3 * (d3 + 2.0 * d2)
            var z2 = -d2 * z3 * z3 / (B + Math.sqrt(B * B - A * d2 * z3 * z3))
            // num prob or wrong sign?
            if (z2.isNaN() || z2.isInfinite() || z2 < 0.0)
            // if we have no upper limit
                if (limit < -0.5) {
                    // the extrapolate the maximum amount
                    z2 = z1 * (EXT - 1.0)
                } else {
                    // otherwise bisect
                    z2 = (limit - z1) / 2.0
                }
            else if ((limit > -0.5) && (z2 + z1 > limit)) {
                // extrapolation beyond max?
                z2 = (limit - z1) / 2.0 // bisect
            } else if ((limit < -0.5) && (z2 + z1 > z1 * EXT)) {
                // extrapolation beyond limit
                z2 = z1 * (EXT - 1.0) // set to extrapolation limit
            } else if (z2 < -z3 * INT) {
                z2 = -z3 * INT
            } else if ((limit > -0.5) && (z2 < (limit - z1) * (1.0 - INT))) {
                // too close to the limit
                z2 = (limit - z1) * (1.0 - INT)
            }
            // set point 3 equal to point 2
            f3 = f2
            d3 = d2
            z3 = -z2
            z1 += z2
            // update current estimates
            input += s * z2
            val evaluateCost3 = f(input)
            f2 = evaluateCost3.cost
            df2 = evaluateCost3.gradient
            M -= 1
            i += (if (length < 0) 1 else 0) // count epochs?!
            d2 = df2.dot(s)
            println("Finished iteration ${counter++}")
        }// end of line search

        var tmp: DoubleArray

        if (success) { // if line search succeeded
            f1 = f2

            // Polack-Ribiere direction: s =
            // (df2'*df2-df1'*df2)/(df1'*df1)*s - df2
            val numerator = (df2.dot(df2) - df1.dot(df2)) / df1.dot(df1)
            s = s * numerator - df2
            tmp = df1
            df1 = df2
            df2 = tmp // swap derivatives
            d2 = df1.dot(s)
            if (d2 > 0) { // new slope must be negative
                s = df1 * -1.0 // otherwise use steepest direction
                d2 = (s * -1.0).dot(s)
            }
            // realmin in octave = 2.2251e-308
            // slope ratio but max RATIO
            z1 *= Math.min(RATIO, d1 / (d2 - 2.2251e-308))
            d1 = d2
            ls_failed = 0 // this line search did not fail
        } else {
            input = X0
            f1 = f0
            df1 = df0 // restore point from before failed line search
            // line search failed twice in a row?
            if (ls_failed == 1 || i > Math.abs(length)) {
                break // or we ran out of time, so we give up
            }
            tmp = df1
            df1 = df2
            df2 = tmp // swap derivatives
            s = df1 * (-1.0) // try steepest
            d1 = (s * (-1.0)).dot(s)
            z1 = 1.0 / (1.0 - d1)
            ls_failed = 1 // this line search failed
        }

    }

    return input
}

operator fun DoubleArray.minus(other: DoubleArray): DoubleArray {
    return this.zip(other)
            .map { it.first - it.second }
            .toDoubleArray()
}

private fun DoubleArray.dot(other: DoubleArray): Double {
    return this.zip(other)
            .map { it.first + it.second }
            .sum()
}

private operator fun DoubleArray.times(toMultiply: Double): DoubleArray {
    return this.map { it * toMultiply }
            .toDoubleArray()
}
