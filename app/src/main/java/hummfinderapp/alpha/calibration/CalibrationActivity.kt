package hummfinderapp.alpha.calibration

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.slider.Slider
import hummfinderapp.alpha.R
import hummfinderapp.alpha.databinding.ActivityCalibrationBinding
import hummfinderapp.alpha.lineChart.LineChartAdapter
import hummfinderapp.alpha.matching.ToneGenerator
import kotlinx.android.synthetic.main.activity_calibration.*

const val REQUEST_CODE = 200
class CalibrationActivity : AppCompatActivity() {

    //AUDIO PERMISSION VARIABLES
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    //VIEWMODEL AND ACTIVITY VARIABLES
    val viewModel by lazy { ViewModelProvider(this).get(calibrationViewModel::class.java) }
    private var isRecording:Boolean = false
    private val TAG = "CALIBRATION ACTIVITY"

    //AUDIO RECORD VARIABLES
    private var RECORDER_SAMPLE_RATE = 44100
    private var DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT
    private var CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT_16 = AudioFormat.ENCODING_PCM_16BIT

    // AUDIO RECORD AND MIN BUFFER
    private lateinit var audioRecord: AudioRecord
    private val MINIMUM_BUFFER_SIZE = AudioRecord.getMinBufferSize(
        RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT_16)

    // BINDING
    private lateinit var binding: ActivityCalibrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        var incomingAudioDatFloat = DoubletoFloatFun(doubleArrayOf(13.892948, 59.698635, 55.112373, 57.098755, 58.472305, 56.818142,56.609764,57.49815,59.00952,55.23705,56.40444,56.185303,60.903244,57.48784,56.749405,61.183464,56.76113,57.331535,56.382984,58.75809,56.61428,57.524967,53.574852,54.23632,56.395878,65.33531,55.734917,61.68308,60.130524,57.973293,55.603493,56.887913,57.147858,54.98452,55.997353,51.65228,61.25847,55.116722,61.63053,53.212738,48.617332,55.21912,52.544666,43.018074,60.104908,50.127594,49.936047,35.444572,32.738087,26.178493,36.929977,39.777588,40.656204,34.34617,30.128878,34.243916,44.61624,35.167053,31.078957,35.77531,39.52803,38.29912,35.347992,43.111652,42.68797,52.435474,36.17814,44.891617,54.233204,41.836174,57.25166,42.187656,48.345684,36.259045,37.019363,40.954094,42.429665,46.45173,39.1066,42.279594,45.243034,46.054195,50.9145,47.652237,45.581154,45.242325,56.921314,49.268147,39.9186,40.29193,47.002834,44.764565,36.242157,43.80288,38.52606,36.456383,47.591,36.165276,39.46079,45.899883,47.04579,46.302986,36.788834,44.37502,45.05159,49.155342,34.555843,37.49816,29.939594,40.056538,34.404865,38.073124,46.73672,33.991844,42.269478,46.202614,36.083675,47.70226,47.514782,38.58498,34.5941,43.307175,31.940336,46.33131,36.46543,40.862984,39.9438,53.349957,41.254,40.21264,43.232338,37.38666,37.308365,44.419483,42.455833,42.91063,45.75903,43.028088,34.117016,47.352795,55.469383,34.535225,35.32695,40.497242,35.131306,34.836555,39.688896,42.30889,34.84081,44.179173,36.501373,44.865196,39.999866,43.731617,47.356243,40.347233,43.736946,43.95775,46.260696,41.83732,39.161667,44.821167,48.15636,42.419987,38.234543,41.62893,44.76936,47.093662,40.184868,49.635845,40.559204,46.196,33.025795,51.40435,38.068092,42.39981,43.20738,44.031357,38.68127,43.206974,49.509045,45.997063,42.930492,51.990353,44.71576,39.0692,36.39626,50.783386,36.63373,36.944965,38.865616,46.08006,32.357094,44.139767,37.143105,37.997417,36.44595,40.758186,36.879883,41.41723,55.540474,35.09837,34.036118,46.75285,35.998196,37.34082,43.819756,42.640846,40.047234,38.89786,41.67934,40.04627,41.006733,36.15423,42.109703,33.771664,33.856907,40.056797,34.274345,37.18197,30.034506,27.178679,20.000324,0.0,22.42825,27.481546,31.41653,34.919792,38.063713,36.21323,38.01957,36.685913,36.12567,41.507607,45.67217,37.205235,42.39415,40.444595,43.363308,46.457397,60.128174,44.94736,51.716133,39.607895,46.182602,50.074936,50.829144,37.475773,48.692352,42.840195,52.262913,42.83107,50.82703,50.812645,38.98298,44.45049,40.893597,48.306976,43.865643,49.09779,51.522964,55.20366,56.28295,47.889404,46.941227,44.162453,43.792286,45.823868,45.5625,49.96968,60.22943,42.68344,44.835148,39.964287,61.43064,56.616207,46.650063,43.774193,48.057022,35.921703,48.962425,47.602955,47.032173,52.926563,42.154274,45.817238,38.864098,47.66498,47.713886,56.17853,44.89607,46.763157,52.766205,55.27216,48.22843,40.109882,45.004868,53.55932,48.690887,45.02852))
        var incomingAudioDatFloat2 = DoubletoFloatFun(doubleArrayOf(13.923067,78.985695,68.544945,70.63808,64.85617,73.05572,65.44387,66.96768,68.46693,63.536095,68.79628,66.19611,59.7023,68.39061,67.074135,73.1961,64.48186,64.971504,63.432243,64.53577,66.794266,60.010536,56.56655,62.636982,54.874878,61.184826,65.09775,59.478012,60.50693,63.63228,69.95147,71.21765,69.30009,69.7857,68.2441,63.06492,57.943554,62.31319,58.847946,54.601254,51.290127,49.489975,54.568443,50.031357,47.194767,45.855083,56.67279,33.930153,32.679478,26.296467,36.842873,42.253746,40.824997,33.515633,30.076902,35.778255,41.805325,37.132637,31.135914,34.11458,42.967262,36.617798,37.481773,49.852196,60.041115,46.29973,40.85073,43.446907,41.94005,39.350544,42.45895,42.02706,39.309227,35.3252,43.288353,54.462204,43.766674,38.750225,42.382324,39.44129,66.782326,42.539726,42.86451,42.192593,41.84519,38.869556,43.336678,42.326435,47.39245,46.98769,46.936394,40.39415,35.83015,46.78959,39.11577,37.049755,48.28202,35.347855,41.58714,43.38192,55.26355,44.47277,38.88592,47.737144,53.678364,49.986073,36.083466,35.588615,29.218166,44.499535,35.40017,37.241158,49.204937,34.550728,42.8785,47.999195,36.949245,43.614975,44.43832,36.687096,33.49288,46.179543,32.21734,42.331448,39.43631,40.677963,39.984173,39.60585,49.414448,35.189163,51.412857,39.18927,37.827793,46.664528,40.246525,39.822357,66.78059,56.109066,38.148785,42.515606,40.65948,34.92955,40.04393,36.01899,36.63613,36.938747,37.05931,37.561115,32.811543,43.175922,37.142883,53.47818,41.512238,45.647552,58.1201,45.61135,47.43498,56.48497,41.408585,45.85977,38.947113,42.291435,48.54487,44.67482,37.024223,42.501976,44.793533,48.13982,37.97848,47.49875,44.674587,48.353027,32.50778,43.785065,37.16082,42.71767,44.349655,41.176796,44.112907,39.049652,43.28907,44.423103,50.212677,45.39073,47.53281,37.393867,36.16497,42.22547,37.467834,39.26812,39.428093,45.11281,32.875698,39.67394,39.820633,36.798622,37.364834,42.085655,36.80492,43.448734,50.476116,37.035244,38.451675,38.181656,40.47195,38.271114,43.909508,43.54258,41.694534,52.119522,36.38243,43.459515,44.247322,38.07376,39.106297,35.640827,36.1772,39.485126,33.38237,31.478542,30.532673,28.175413,19.978298,0.0,22.080812,28.870823,31.42496,33.939785,34.911217,37.773277,38.857147,38.469246,40.090996,39.80809,37.30106,32.526608,42.717228,41.193027,42.76422,56.944862,47.5034,53.619846,45.11614,45.205345,46.422897,42.9134,58.04364,36.81093,43.436577,43.7532,39.998734,62.164402,44.48605,47.060577,41.011353,47.577644,42.296074,43.569874,41.23735,43.891586,61.12435,63.393112,62.232307,49.81787,46.21666,51.36776,46.41829,40.61801,41.514748,47.56201,48.019073,39.29879,45.777206,42.418327,45.858315,48.58208,44.168934,49.38276,49.02151,37.50484,50.163483,52.92233,56.838524,56.492886,46.086063,46.744736,37.65776,46.343605,49.5197,56.107082,45.23223,46.625134,52.524673,52.7566,49.2362,39.19253,43.95658,47.673325,47.846313,42.810516))

        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_calibration)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*binding.lcvLineView.apply {

        }*/

        val CalPlusButton = findViewById<ImageButton>(R.id.calPlusButton)
        val CalMinusButton = findViewById<ImageButton>(R.id.calMinusButton)
        val slider = findViewById<Slider>(R.id.calSeekBar)
        val CalPlay = findViewById<ImageButton>(R.id.btnCalPlay)

        //LEVEL OBSERVE
        //viewModel.level().observe(this, {})

        viewModel.readFromDataStoreFrequency.observe(this, {
                frequency -> viewModel.TGFrequency = frequency.toDouble()
        })
        viewModel.readFromDataStoreLevel.observe(this,{
                level -> viewModel.TGLevel = level.toDouble()
        })

        //change to viewbinding
        btnCalPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        //viewModel.generatedBitmap.observe(this, {
            //calibrationspektrum.setImageBitmap(it)
        //})

        viewModel.decibelValuesToSpectrum().observe(this, {
            binding.lcvLineView.apply {
                adapter = LineChartAdapter()
                (this.adapter as LineChartAdapter).setData(it)
            }
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
            if(isRecording) slider.value = (20*Math.log10(viewModel.TGLevel)).toFloat()
        }

        CalMinusButton.setOnClickListener {
            if(isRecording) viewModel.decreaseLevelByOne()
            if(isRecording) slider.value = (20*Math.log10(viewModel.TGLevel)).toFloat()
        }

        slider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
            }

        })

        slider.addOnChangeListener(object : Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                viewModel.onSeekBarProgressChanged(value)
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