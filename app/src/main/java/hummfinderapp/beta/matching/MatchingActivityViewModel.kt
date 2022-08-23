package hummfinderapp.beta.matching


import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MatchingActivityViewModel: ViewModel() {
        private val toneGenerator = ToneGenerator()

        companion object {
                const val MAX_FREQUENCY = 20000.0
        }

        var currentFrequency: Double = 440.0
                set(frequency){
                field = frequency
                _currentFrequencyText.value =String.format("%.1f", frequency)
                toneGenerator.frequency = frequency
                }

        private val _currentFrequencyText = MutableLiveData("440,0")
        val currentFrequencyText:LiveData<String>
                get() =_currentFrequencyText

        fun startToneGenerator(){
                toneGenerator.start()
        }

        //function stop >
        fun stopToneGenerator(){
                toneGenerator.stop()
        }

        //increaseFrequencyBy1
        fun increaseFrequencyByOne(){
                currentFrequency = currentFrequency + 1
        }

        //decreaseFrequencyBy1
        fun decreaseFrequencyByOne(){
                currentFrequency = currentFrequency - 1
        }

        //seekbarchange
        fun onSeekBarProgressChanged(progress:Int){
                currentFrequency = progress.toDouble()
                //val frequency = 10.0.pow(progress.toDouble() / 100.0 * log10(MAX_FREQUENCY))
                //currentFrequency = frequency
        }
}