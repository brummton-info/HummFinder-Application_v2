package hummfinderapp.alpha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.cardview.widget.CardView
import hummfinderapp.alpha.calibration.CalibrationActivity
import hummfinderapp.alpha.info.InfoActivity
import hummfinderapp.alpha.matching.MatchingActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val materialCardView1 = findViewById<CardView>(R.id.materialCardView1)
        materialCardView1.setOnClickListener{
            val infoIntent = Intent(this, InfoActivity::class.java)
            startActivity(infoIntent)
        }

        val materialCardView2 = findViewById<CardView>(R.id.materialCardView2)
        materialCardView2.setOnClickListener{
            val matchingIntent = Intent(this, MatchingActivity::class.java)
            startActivity(matchingIntent)
        }

        val materialCardView3 = findViewById<CardView>(R.id.materialCardView3)
        materialCardView3.setOnClickListener{
            val calibrationIntent = Intent(this, CalibrationActivity::class.java)
            startActivity(calibrationIntent)
        }

        val materialCardView4 = findViewById<CardView>(R.id.materialCardView4)
        materialCardView4.setOnClickListener{
            val unassignedIntent = Intent(this, CalibrationActivityTwo::class.java)
            startActivity(unassignedIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homemenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homegivefeedback -> Toast.makeText(this,"Thanks for your feedback",Toast.LENGTH_SHORT).show()
        }
        return true
    }
}