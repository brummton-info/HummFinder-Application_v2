package hummfinderapp.alpha.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "humm_store"

class DataStoreRepository(context:Context) {

    private object PreferenceKeys{
        val frequency = preferencesKey<String>("frequency")
        val level = preferencesKey<String>("level")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun saveToDataStore(frequency: String, level:String){
        dataStore.edit { preference ->
            preference[PreferenceKeys.frequency] = frequency
            preference[PreferenceKeys.level] = level
        }
    }

    val readFromDataStoreFrequency: Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore Frequency e",exception.message.toString())
                emit(emptyPreferences())
            }
            else{
                throw exception
            }
        }
        .map { preference ->
            val frequency = preference[PreferenceKeys.frequency] ?: "150"
            frequency
        }

    val readFromDataStoreLevel: Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore Level e",exception.message.toString())
                emit(emptyPreferences())
            }
            else{
                throw exception
            }
        }
        .map { preference ->
            val level = preference[PreferenceKeys.level] ?: "0.3"
            level
        }
}