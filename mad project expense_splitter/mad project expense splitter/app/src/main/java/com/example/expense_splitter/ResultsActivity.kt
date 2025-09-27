package com.example.expense_splitter

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val resultText = intent.getStringExtra("resultText")
        val resultTV: TextView = findViewById(R.id.idTVFinalResult)
        val backBtn: Button = findViewById(R.id.idBtnBack)

        resultTV.text = resultText

        backBtn.setOnClickListener {
            finish()
        }
    }
}
