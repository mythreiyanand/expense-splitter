package com.example.expense_splitter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val startButton: Button = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            val intent = Intent(this, SplitterActivity::class.java)
            startActivity(intent)
        }
    }
}
