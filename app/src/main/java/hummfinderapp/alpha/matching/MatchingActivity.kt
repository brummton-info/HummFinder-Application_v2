package hummfinderapp.alpha.matching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.slider.Slider
import hummfinderapp.alpha.R


class MatchingActivity : AppCompatActivity() {

    lateinit var TextMatching: TextView
    val viewModel by lazy { ViewModelProvider(this).get(MatchingActivityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        TextMatching = findViewById<TextView>(R.id.TextMatching)
        val decreaseFrequency = findViewById<ImageButton>(R.id.decreaseFrequency)
        val increaseFrequency = findViewById<ImageButton>(R.id.increaseFrequency)
        val slider = findViewById<Slider>(R.id.matchingSeekBar)
        val TAG = "MATCHING ACTIVITY"
        val MAX_FREQUENCY = 500
        var END_ONSTOP = 0.0
        var BUTTON_STATE = 0

        val statefulbutton = findViewById<ImageButton>(R.id.startTone)
        statefulbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        viewModel.frequency().observe(this,{
            TextMatching.text = it.toString()
            slider.value = it.toFloat()
        })

        //OBSERVE FREQUENCY
        viewModel.readFromDataStoreFrequency.observe(this,{
                frequency -> viewModel.TGFrequency = frequency.toDouble()
        })

        //OBSERVE LEVEL
        viewModel.readFromDataStoreLevel.observe(this,{
            level -> viewModel.TGLevel = level.toDouble()
        })

        statefulbutton.setOnClickListener {
            if (BUTTON_STATE == 0){
                viewModel.startToneGenerator()
                BUTTON_STATE = 1
                statefulbutton.setImageResource(R.drawable.ic_baseline_stop_24)
            }
            else{
                viewModel.stopToneGenerator()
                BUTTON_STATE = 0
                statefulbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        increaseFrequency.setOnClickListener {
            if (viewModel.TGFrequency.toInt() != MAX_FREQUENCY){
                viewModel.increaseFrequencyByOne()
            }
            else{
                Toast.makeText(this,"Maximum Frequency reached",Toast.LENGTH_SHORT).show()
            }
        }

        decreaseFrequency.setOnClickListener {
                END_ONSTOP = viewModel.TGFrequency
                if (END_ONSTOP != 0.0){
                    viewModel.decreaseFrequencyByOne()
                    if (END_ONSTOP == 30.0){
                        Toast.makeText(this@MatchingActivity, "Below large speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP == 50.0){
                        Toast.makeText(this@MatchingActivity, "Below in-ear headphone threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP == 80.0){
                        Toast.makeText(this@MatchingActivity, "Below bluetooth speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP == 100.0){
                        Toast.makeText(this@MatchingActivity, "Below smartphone speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                }
            else{
                Toast.makeText(this,"Minimum Frequency reached",Toast.LENGTH_SHORT).show()
            }
        }

        slider.valueTo = MAX_FREQUENCY.toFloat()

        slider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                if (slider != null) {
                    END_ONSTOP = slider.value.toDouble()
                    if (END_ONSTOP < 30.0){
                        return Toast.makeText(this@MatchingActivity, "Below large speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 50.0){
                        return Toast.makeText(this@MatchingActivity, "Below in-ear headphone threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 80.0){
                        return Toast.makeText(this@MatchingActivity, "Below bluetooth speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 100.0){
                        return Toast.makeText(this@MatchingActivity, "Below smartphone speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        
        slider.addOnChangeListener(object : Slider.OnChangeListener {
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                viewModel.onSeekBarProgressChanged(value)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuwithsave,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frequency = TextMatching.text.toString()
        val level = "0.3"
        when (item.itemId){
            android.R.id.home -> onBackPressed() /*onBackPressed() override*/
            R.id.misave -> viewModel.saveToDataStore(frequency, level)
            R.id.givefeedback -> Toast.makeText(this,"Thanks for your feedback",Toast.LENGTH_SHORT).show()
            R.id.closeapp -> finish()
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

