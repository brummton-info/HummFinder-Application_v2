package hummfinderapp.alpha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_calibration_two.*
import kotlinx.android.synthetic.main.calibration_item.*
import org.w3c.dom.Text
import java.util.*

class CalibrationActivityTwo : AppCompatActivity() {

    //Widget initialization
    lateinit var calViewPagerBtn: Button
    lateinit var dotI: TextView
    lateinit var dotII: TextView
    lateinit var dotIII: TextView
    lateinit var dotIV: TextView
    lateinit var dotV: TextView
    lateinit var dotVI: TextView
    lateinit var dotVII: TextView
    lateinit var CalibrationVP: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration_two)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val texts = listOf<String>(
            "25 Hz",
            "50 Hz",
            "75 Hz",
            "100 Hz",
            "125 Hz",
            "150 Hz",
            "x Hz"
        )

        //Widgets
        calViewPagerBtn = findViewById(R.id.calViewPagerBtn)
        dotI = findViewById(R.id.dotI)
        dotII = findViewById(R.id.dotII)
        dotIII = findViewById(R.id.dotIII)
        dotIV = findViewById(R.id.dotIV)
        dotV = findViewById(R.id.dotV)
        dotVI = findViewById(R.id.dotVI)
        dotVII = findViewById(R.id.dotVII)
        CalibrationVP = findViewById(R.id.CalibrationVP)

        val adapter = CalViewPagerAdapter(texts)
        CalibrationVP.adapter = adapter

        calViewPagerBtn.setOnClickListener {
            val current = CalibrationVP.currentItem + 1
            if(CalibrationVP != null && current < texts.size - 1){
                CalibrationVP.currentItem = current
            }
            else if (current == texts.size - 1){
                CalibrationVP.currentItem = current
                Toast.makeText(this, "Calibration Done!", Toast.LENGTH_SHORT).show()
                calViewPagerBtn.text = "Save Tinnitus Threshold"
            }
            else{
                //TODO: Save the user Calibrated value from the slider input
            }

            CalibrationVP?.registerOnPageChangeCallback(VPChangeListener)
        }
    }

    //Page Change Listener
    private var VPChangeListener: ViewPager2.OnPageChangeCallback = object:
        ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            when (position) {
                0-> {
                    dotI.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                1 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                2 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                3 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                4 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                5 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
                6 -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.gray))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.Hummfindercolor))
                }
                else -> {
                    dotI.setTextColor(resources.getColor(R.color.gray))
                    dotII.setTextColor(resources.getColor(R.color.Hummfindercolor))
                    dotIII.setTextColor(resources.getColor(R.color.gray))
                    dotIV.setTextColor(resources.getColor(R.color.gray))
                    dotV.setTextColor(resources.getColor(R.color.gray))
                    dotVI.setTextColor(resources.getColor(R.color.gray))
                    dotVII.setTextColor(resources.getColor(R.color.gray))
                }
            }
        }
        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}