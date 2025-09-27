package com.example.expense_splitter

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SplitterActivity : AppCompatActivity() {

    private lateinit var amountEdt: TextInputEditText
    private lateinit var tipPercentEdt: TextInputEditText
    private lateinit var numberOfPeopleEdt: TextInputEditText
    private lateinit var currencySpinner: Spinner
    private lateinit var calculateBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var dynamicLayout: LinearLayout

    private var selectedCurrency: String = "₹"
    private val currencies = arrayOf("₹", "$", "€", "¥", "£")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splitter)

        amountEdt = findViewById(R.id.idEdtAmount)
        tipPercentEdt = findViewById(R.id.idEdtTipPercent)
        numberOfPeopleEdt = findViewById(R.id.idEdtNumberOfPeople)
        currencySpinner = findViewById(R.id.idSpinnerCurrency)
        calculateBtn = findViewById(R.id.idBtnCalculate)
        resetBtn = findViewById(R.id.idBtnReset)
        dynamicLayout = findViewById(R.id.dynamicLayout)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                selectedCurrency = currencies[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        calculateBtn.setOnClickListener { calculateSplit() }
        resetBtn.setOnClickListener { resetFields() }

        numberOfPeopleEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePersonFields()
            }
        })
    }

    private fun updatePersonFields() {
        dynamicLayout.removeAllViews()
        val numPeople = numberOfPeopleEdt.text?.toString()?.toIntOrNull() ?: return

        for (i in 1..numPeople) {
            val textInputLayout = TextInputLayout(this).apply {
                hint = "Person $i Percentage"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }
            }

            val editText = TextInputEditText(this).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            textInputLayout.addView(editText)
            dynamicLayout.addView(textInputLayout)
        }
    }

    private fun calculateSplit() {
        val amount = amountEdt.text?.toString()?.toDoubleOrNull()
        val tipPercent = tipPercentEdt.text?.toString()?.toDoubleOrNull() ?: 0.0
        val numPeople = numberOfPeopleEdt.text?.toString()?.toIntOrNull()

        if (amount == null || amount <= 0) {
            showToast("Enter a valid bill amount")
            return
        }
        if (numPeople == null || numPeople <= 0) {
            showToast("Enter a valid number of people")
            return
        }

        val percentages = mutableListOf<Double>()
        var totalEnteredPercent = 0.0
        var blankCount = 0

        for (i in 0 until numPeople) {
            val textInputLayout = dynamicLayout.getChildAt(i) as? TextInputLayout
            val personEdt = textInputLayout?.editText
            val text = personEdt?.text?.toString()

            val percent = if (text.isNullOrBlank()) {
                blankCount++
                null
            } else {
                text.toDoubleOrNull()?.also { totalEnteredPercent += it }
            }
            percentages.add(percent ?: 0.0)
        }

        if (totalEnteredPercent > 100.0) {
            showToast("Total percentages cannot exceed 100%")
            return
        }

        val remainingPercent = 100.0 - totalEnteredPercent
        val perBlankPercent = if (blankCount > 0) remainingPercent / blankCount else 0.0

        for (i in 0 until numPeople) {
            if (percentages[i] == 0.0) {
                percentages[i] = perBlankPercent
            }
        }

        val tipAmount = amount * (tipPercent / 100)
        val totalAmount = amount + tipAmount

        val result = StringBuilder()
        result.append("Total with Tip: $selectedCurrency%.2f\nTip Amount: $selectedCurrency%.2f\n\n"
            .format(totalAmount, tipAmount))

        for (i in 0 until numPeople) {
            val personAmount = totalAmount * (percentages[i] / 100)
            result.append("Person ${i + 1} (%.2f%%): $selectedCurrency%.2f\n"
                .format(percentages[i], personAmount))
        }

        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra("resultText", result.toString())
        startActivity(intent)
    }

    private fun resetFields() {
        amountEdt.setText("")
        tipPercentEdt.setText("")
        numberOfPeopleEdt.setText("")
        dynamicLayout.removeAllViews()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
