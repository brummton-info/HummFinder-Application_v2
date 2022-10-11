package hummfinderapp.alpha.calibration

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import hummfinderapp.alpha.R
import kotlinx.android.synthetic.main.activity_calibration.*

val REQUEST_CODE = 200
class CalibrationActivity : AppCompatActivity() {

    //AUDIO PERMISSION VARIABLES
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    //VIEWMODEL AND ACTIVITY VARIABLES
    val viewModel by lazy { ViewModelProvider(this).get(calibrationViewModel::class.java) }
    private var isRecording:Boolean = false
    private val TAG = "CALIBRATION ACTIVITY"
    private val CalPlusButton = findViewById<ImageButton>(R.id.calPlusButton)
    private val CalMinusButton = findViewById<ImageButton>(R.id.calMinusButton)
    private val CalSeekBar = findViewById<SeekBar>(R.id.calSeekBar)
    private val CalPlay = findViewById<ImageButton>(R.id.btnCalPlay)

    //AUDIO RECORD VARIABLES
    private var RECORDER_SAMPLE_RATE = 44100
    private var DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT
    private var CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT_16 = AudioFormat.ENCODING_PCM_16BIT

    // AUDIO RECORD AND MIN BUFFER
    private lateinit var audioRecord: AudioRecord
    private val MINIMUM_BUFFER_SIZE = AudioRecord.getMinBufferSize(
        RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT_16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //LEVEL OBSERVE
        //viewModel.level().observe(this, {})

        viewModel.readFromDataStoreFrequency.observe(this, {
                frequency -> viewModel.TGFrequency = frequency.toDouble()
        })
        viewModel.readFromDataStoreLevel.observe(this,{
                level -> viewModel.TGLevel = level.toDouble()
        })


        btnCalPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        viewModel.generatedBitmap.observe(this, {
            calibrationspektrum.setImageBitmap(it)
        })

        //ASK FOR PERMISSION
        permissionGranted =ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)
        }

        //ON CLICK LISTENER PLAY BUTTON
        CalPlay.setOnClickListener {
            when{
                isRecording -> calibrationStop()
                else -> calibrationStart()
            }
        }

        CalPlusButton.setOnClickListener {
            if(isRecording) viewModel.increaseLevelByOne()
        }

        CalMinusButton.setOnClickListener {
            if(isRecording) viewModel.decreaseLevelByOne()
        }

        CalSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                viewModel.onSeekBarProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }

        })

    }

    fun calibrationStart(){
        if (ActivityCompat.checkSelfPermission(
                this,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }
        else {
            isRecording = true

            //SET IMAGE RESOURCE
            btnCalPlay.setImageResource(R.drawable.ic_baseline_stop_24)

            //START TONE GENERATOR
            viewModel.startToneGenerator()
            Toast.makeText(this,"Humm at ${viewModel.TGFrequency} Hz",Toast.LENGTH_SHORT).show()

            //INITIALIZE AUDIO RECORD
            audioRecord = AudioRecord(
                DEFAULT_AUDIO_SOURCE,
                RECORDER_SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT_16,
                MINIMUM_BUFFER_SIZE)
            //7168 > minimum buffer size


            if (audioRecord!!.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "error initializing AudioRecord")
                return
            }
            audioRecord.startRecording()
            viewModel.audioToSpectrum(audioRecord)
        }
    }

    fun calibrationStop(){
        isRecording = false

        //SET IMAGE RESOURCE
        btnCalPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        //STOP TONE GENERATOR
        viewModel.stopToneGenerator()

        //RELEASE/STOP AUDIO
        Log.d(TAG,"AUDIORECORD STOPPED WITH ${audioRecord.state} AND ${audioRecord.recordingState}")
        audioRecord.stop()
        audioRecord.release()

        //STOP SPECTRUM
    }

    //MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuwithsave,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frequency = viewModel.TGFrequency.toString()
        val level = viewModel.TGLevel.toString()
        when (item.itemId){
            android.R.id.home -> onBackPressed() /*onBackPressed() override*/
            R.id.misave -> viewModel.saveToDataStore(frequency,level)
            R.id.givefeedback -> Toast.makeText(this,"Thanks for your feedback",Toast.LENGTH_SHORT).show()
            R.id.closeapp -> finish()
        }
        return true
    }

    //PERMISSION
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}