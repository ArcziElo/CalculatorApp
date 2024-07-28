package com.example.kalkulator.arkadiusz.kalkulator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private var lastNumeric: Boolean = false
    private var stateError: Boolean = false
    private var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.result)
    }

    fun onDigit(view: View) {
        if (stateError) {
            resultTextView.text = (view as Button).text
            stateError = false
        } else {
            resultTextView.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            resultTextView.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onClear(view: View) {
        resultTextView.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }

    fun onDecimalPoint(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            resultTextView.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    fun onEqual(view: View) {
        if (lastNumeric && !stateError) {
            val txt = resultTextView.text.toString()
            val expression = txt.replace(Regex("([*/+-])"), " $1 ")
            try {
                val result = evaluate(expression)
                resultTextView.text = result.toString()
                lastDot = true
            } catch (ex: Exception) {
                resultTextView.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    private fun evaluate(expression: String): Double {
        val tokens = expression.split(" ")
        val values = Stack<Double>()
        val ops = Stack<Char>()

        for (token in tokens) {
            when {
                token.isEmpty() -> continue
                token[0].isDigit() -> values.push(token.toDouble())
                else -> {
                    while (!ops.isEmpty() && hasPrecedence(token[0], ops.peek()))
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()))
                    ops.push(token[0])
                }
            }
        }

        while (!ops.isEmpty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()))

        return values.pop()
    }

    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')')
            return false
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false
        return true
    }

    private fun applyOp(op: Char, b: Double, a: Double): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            else -> throw UnsupportedOperationException("Unknown operator: $op")
        }
    }
}
