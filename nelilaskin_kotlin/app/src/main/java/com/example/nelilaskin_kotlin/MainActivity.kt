package com.example.nelilaskin_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputField1 = findViewById<EditText>(R.id.inputField1)
        val inputField2 = findViewById<EditText>(R.id.inputField2)
        val resultLabel = findViewById<TextView>(R.id.resultLabel)
        val calculateButton = findViewById<Button>(R.id.calculateButton)

        calculateButton.setOnClickListener {
            val number1 = inputField1.text.toString().toFloatOrNull() ?: 0f
            val number2 = inputField2.text.toString().toFloatOrNull() ?: 0f
            val sum = number1 + number2
            resultLabel.text = sum.toString()
        }
    }
}