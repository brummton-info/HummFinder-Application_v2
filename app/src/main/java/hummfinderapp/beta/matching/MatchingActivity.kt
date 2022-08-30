package hummfinderapp.beta.matching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import hummfinderapp.beta.R
import hummfinderapp.beta.loginactivity


class MatchingActivity : AppCompatActivity() {

    val viewModel = ViewModelProvider(this).get(MatchingActivityViewModel::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)


        val TextMatching = findViewById<TextView>(R.id.TextMatching)
        val decreaseFrequency = findViewById<ImageButton>(R.id.decreaseFrequency)
        val increaseFrequency = findViewById<ImageButton>(R.id.increaseFrequency)
        val statefulbutton = findViewById<ImageButton>(R.id.startTone)
        val seekBar = findViewById<SeekBar>(R.id.matchingSeekBar)
        val TAG = "Matching Activity"
        val MAX_FREQUENCY = 500
        var END_ONSTOP = 0
        var BUTTON_STATE = 0
        statefulbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        viewModel.currentFrequencyText.observe(this, { TextMatching.text = it.toString() })

        statefulbutton.setOnClickListener {
            if (BUTTON_STATE == 0){
                viewModel.startToneGenerator()
                Log.d(TAG, "Tone Generator Started!")
                BUTTON_STATE = 1
                Log.d(TAG, "Button State changed to" + BUTTON_STATE.toString())
                statefulbutton.setImageResource(R.drawable.ic_baseline_stop_24)
            }
            else{
                viewModel.stopToneGenerator()
                Log.d(TAG, "Tone Generator Stopped!")
                BUTTON_STATE = 0
                Log.d(TAG, "Button State changed to" + BUTTON_STATE.toString())
                statefulbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        increaseFrequency.setOnClickListener {
            if(BUTTON_STATE == 1){
                viewModel.increaseFrequencyByOne()
            }
        }

        decreaseFrequency.setOnClickListener {
            if(BUTTON_STATE == 1){
                viewModel.decreaseFrequencyByOne()
                END_ONSTOP = viewModel.currentFrequency.toInt()
                if (END_ONSTOP == 30){
                    Toast.makeText(this@MatchingActivity, "Below large speaker threshold",Toast.LENGTH_SHORT).show()
                }
                else if (END_ONSTOP == 50){
                    Toast.makeText(this@MatchingActivity, "Below in-ear headphone threshold",Toast.LENGTH_SHORT).show()
                }
                else if (END_ONSTOP == 80){
                    Toast.makeText(this@MatchingActivity, "Below bluetooth speaker threshold",Toast.LENGTH_SHORT).show()
                }
                else if (END_ONSTOP == 100){
                    Toast.makeText(this@MatchingActivity, "Below smartphone speaker threshold",Toast.LENGTH_SHORT).show()
                }
            }
        }

        seekBar.max = (MAX_FREQUENCY - 0)
        seekBar.progress = 150

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar:SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onSeekBarProgressChanged(progress)
            }
            override fun onStartTrackingTouch(seekBar:SeekBar?) {}
            override fun onStopTrackingTouch(seekBar:SeekBar?) {
                if (seekBar != null) {
                    END_ONSTOP = seekBar.progress
                    if (END_ONSTOP < 30){
                        return Toast.makeText(this@MatchingActivity, "Below large speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 50){
                        return Toast.makeText(this@MatchingActivity, "Below in-ear headphone threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 80){
                        return Toast.makeText(this@MatchingActivity, "Below bluetooth speaker threshold",Toast.LENGTH_SHORT).show()
                    }
                    else if (END_ONSTOP < 100){
                        return Toast.makeText(this@MatchingActivity, "Below smartphone speaker threshold",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })

    }
}

