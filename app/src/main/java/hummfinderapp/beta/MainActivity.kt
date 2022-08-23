package hummfinderapp.beta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import hummfinderapp.beta.matching.MatchingActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val materialCardView1 = findViewById<CardView>(R.id.materialCardView1)
        materialCardView1.setOnClickListener{
            val infoIntent = Intent(this,InfoActivity::class.java)
            startActivity(infoIntent)
        }

        val materialCardView2 = findViewById<CardView>(R.id.materialCardView2)
        materialCardView2.setOnClickListener{
            val matchingIntent = Intent(this, MatchingActivity::class.java)
            startActivity(matchingIntent)
        }

        val materialCardView3 = findViewById<CardView>(R.id.materialCardView3)
        materialCardView3.setOnClickListener{
            val calibrationIntent = Intent(this,CalibrationActivity::class.java)
            startActivity(calibrationIntent)
        }

        val materialCardView4 = findViewById<CardView>(R.id.materialCardView4)
        materialCardView4.setOnClickListener{
            val unassignedIntent = Intent(this,UnassignedActivity::class.java)
            startActivity(unassignedIntent)
        }

    }
}