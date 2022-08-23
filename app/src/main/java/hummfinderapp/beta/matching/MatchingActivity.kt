package hummfinderapp.beta.matching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import hummfinderapp.beta.R


class MatchingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)


        val viewModel = ViewModelProvider(this).get(MatchingActivityViewModel::class.java)
        val TextMatching = findViewById<TextView>(R.id.TextMatching)
        val decreaseFrequency = findViewById<ImageButton>(R.id.decreaseFrequency)
        val increaseFrequency = findViewById<ImageButton>(R.id.increaseFrequency)
        val start = findViewById<ImageButton>(R.id.startTone)
        val stop = findViewById<ImageButton>(R.id.stopTone)
        val seekBar = findViewById<SeekBar>(R.id.matchingSeekBar)
        val TAG = "Matching Activity"
        val MAX_FREQUENCY = 500
        var END_ONSTOP = 0

        viewModel.currentFrequencyText.observe(this, { TextMatching.text = it.toString() })
        stop.isEnabled = false
        stop.isClickable = false

        start.setOnClickListener {
            viewModel.startToneGenerator()
            Log.d(TAG, "Tone Generator Started again!")
            stop.isEnabled = true
            stop.isClickable = true
            start.isEnabled = false
            start.isClickable = false

        }

        increaseFrequency.setOnClickListener {
            viewModel.increaseFrequencyByOne()
        }

        decreaseFrequency.setOnClickListener {
            viewModel.decreaseFrequencyByOne()
        }

        stop.setOnClickListener {
            viewModel.stopToneGenerator()
            stop.isEnabled = false
            stop.isClickable = false
            start.isEnabled = true
            start.isClickable = true
        }

        seekBar.max = (MAX_FREQUENCY - 0)
        seekBar.progress = 440

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

