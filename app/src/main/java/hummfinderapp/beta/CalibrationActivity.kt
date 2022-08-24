package hummfinderapp.beta

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.app.ActivityCompat

const val REQUEST_CODE = 200
class CalibrationActivity : AppCompatActivity() {
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)
        val play = findViewById<ImageButton>(R.id.calMuteButton)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        play.setOnClickListener {
            startRecording()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE){
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording(){
        if(!permissionGranted){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }
        //start recording
    }

}