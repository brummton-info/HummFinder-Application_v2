package hummfinderapp.beta.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import hummfinderapp.beta.MainActivity
import hummfinderapp.beta.R

class loginactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)

        var Username = findViewById<EditText>(R.id.Username)
        var Password = findViewById<EditText>(R.id.Username)

        var btnlogin = findViewById<Button>(R.id.btnlogin)
        var loginIntent = Intent(this, MainActivity::class.java)
        btnlogin.setOnClickListener {
            if(Username.text.toString().equals("admin") && Password.text.toString().equals("admin")){
                startActivity(loginIntent)
            }
            else{
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}