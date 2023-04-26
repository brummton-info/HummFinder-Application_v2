package hummfinderapp.alpha.calibration

import kotlin.math.*

/**
 * Computes the absolute values of the complex valued result from the FFT.
 * The output size is (input.size / 2) + 1.
 *
 * @param input FloatArray that comes from FloatFFT_1D.realForward()
 */
fun absoluteFromComplex(input: FloatArray): FloatArray {
    var output = FloatArray(input.size / 2) { i ->
        //sqrt(input[2 * i].pow(2) + input[2 * i + 1].pow(2))
        hypot(input[2 * i].toDouble(), input[2 * i + 1].toDouble()).toFloat()
    }
    output[0] = input[0].absoluteValue
    output += input[1].absoluteValue

    return output
}

/**
 * Computes the decibel (logarithmic) values from absolutes
 *
 * @param input FloatArray of absolute values
 */
fun decibelFromAbsolute(input: FloatArray): FloatArray {
    val maxValue = input.maxOrNull()!!

    return FloatArray(input.size) {
        20f * log10(input[it] / maxValue)
    }
}

fun decibelFromAbsoluteReverse(input: FloatArray): FloatArray {
    val maxValue = input.maxOrNull()!!

    return FloatArray(input.size) {
        20f * log10(maxValue / input[it])
    }
}

fun DoubletoFloatFun(input: DoubleArray): FloatArray{
    return FloatArray(input.size){
        input[it].toFloat()
    }
}