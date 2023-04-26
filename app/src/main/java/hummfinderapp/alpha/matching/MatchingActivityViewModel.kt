package hummfinderapp.alpha.matching


import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import hummfinderapp.alpha.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MatchingActivityViewModel (application: Application): AndroidViewModel(application) {

        //PREFERENCE DATASTORE
        private val repository = DataStoreRepository(application)
        val readFromDataStoreFrequency = repository.readFromDataStoreFrequency.asLiveData()
        val readFromDataStoreLevel = repository.readFromDataStoreLevel.asLiveData()

        fun saveToDataStore(frequency: String, level:String) = viewModelScope.launch(Dispatchers.IO) {
                repository.saveToDataStore(frequency,level)
        }

        private val TONEGENERATOR = ToneGenerator()
        private val TAG = "Matching ViewModel"

        //MUTABLE LIVE DATA FREQUENCY
        private var _frequency = MutableLiveData<Int>()
        fun frequency(): LiveData<Int>{
                return _frequency
        }

        // TONE GENERATOR FREQUENCY VARIABLE INITIALIZATION
        var TGFrequency: Double = 150.0
                set(frequency){
                        field = frequency
                        TONEGENERATOR.frequency = frequency
                        _frequency.value = frequency.toInt()
                }

        var TGLevel: Double = 0.3
                set(level) {
                        field = level
                        TONEGENERATOR.level = level
                }

        fun startToneGenerator(){
                TONEGENERATOR.start()
        }

        fun stopToneGenerator(){
                TONEGENERATOR.stop()
                Log.d(TAG, "Tone Generator Stopped > btn")
        }

        fun increaseFrequencyByOne(){
                TGFrequency = TGFrequency + 1
        }

        fun decreaseFrequencyByOne(){
                TGFrequency = TGFrequency - 1
        }

        fun onSeekBarProgressChanged(progress:Float){
                TGFrequency = progress.toDouble()
        }

        override fun onCleared() {
                super.onCleared()
                TONEGENERATOR.stop()
                Log.d(TAG,"Tone Generator stopped > onBackPressed")
        }
}