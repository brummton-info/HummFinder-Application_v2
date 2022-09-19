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
import hummfinderapp.alpha.R


class MatchingActivity : AppCompatActivity() {

    lateinit var TextMatching: TextView
    val viewModel by lazy { ViewModelProvider(this).get(MatchingActivityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)

        TextMatching = findViewById<TextView>(R.id.TextMatching)
        val decreaseFrequency = findViewById<ImageButton>(R.id.decreaseFrequency)
        val increaseFrequency = findViewById<ImageButton>(R.id.increaseFrequency)
        val seekBar = findViewById<SeekBar>(R.id.matchingSeekBar)
        val TAG = "MATCHING ACTIVITY"
        val MAX_FREQUENCY = 500
        var END_ONSTOP = 0
        var BUTTON_STATE = 0

        val statefulbutton = findViewById<ImageButton>(R.id.startTone)
        statefulbutton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        viewModel.frequency().observe(this,{
            TextMatching.text = it.toString()
        })
        viewModel.readFromDataStoreFrequency.observe(this,{
                frequency -> viewModel.TGFrequency = frequency.toDouble()
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
                END_ONSTOP = viewModel.TGFrequency.toInt()
                if (END_ONSTOP != 0){
                    viewModel.decreaseFrequencyByOne()
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
            else{
                Toast.makeText(this,"Minimum Frequency reached",Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuwithsave,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frequency = TextMatching.text.toString()
        val level = "0.3"
        when (item.itemId){
            R.id.misave -> viewModel.saveToDataStore(frequency,level)
            R.id.givefeedback -> Toast.makeText(this,"Thanks for your feedback",Toast.LENGTH_SHORT).show()
            R.id.closeapp -> finish()
        }
        return true
    }
}

