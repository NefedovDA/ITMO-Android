package ru.ifmo.nefedov.task1calculator

import android.os.Bundle
import android.util.TypedValue
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class MainActivity : AppCompatActivity() {
    enum class State {
        FirstArgument,
        SecondArgument,
        Answer
    }

    enum class Operation(val id: String) {
        Divide("btn_div"),
        Mull("btn_mull"),
        Minus("btn_minus"),
        Plus("btn_plus"),
        Undefined("")
    }

    private val stringEqual: String get() = getString(R.string.btn_equal)
    private val stringComma: String get() = getString(R.string.btn_comma)

    private var currentState: State = State.FirstArgument
    private var currentOperation: Operation = Operation.Undefined

    private var isError: Boolean = false

    private var firstArgument = "R"
    private var secondArgument = ""

    private fun stringOperation(): String = when (currentOperation) {
        Operation.Divide -> getString(R.string.btn_div)
        Operation.Mull -> getString(R.string.btn_mull)
        Operation.Minus -> getString(R.string.btn_minus)
        Operation.Plus -> getString(R.string.btn_plus)
        Operation.Undefined -> ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (0..9).forEach {
            setSymbolOnClick("btn_$it", "$it") { addNumber(it) }
        }

        Operation.values().forEach { if (it != Operation.Undefined) setOperationOnClick(it) }

        setSymbolOnClick("btn_comma", ".") { addComma() }

        btn_delete.setOnClickListener { updateByDelete() }

        btn_clean.setOnClickListener {
            firstArgument = ""
            secondArgument = ""
            currentOperation = Operation.Undefined
            currentState = State.FirstArgument

            updateViewLines()
        }

        btn_equal.setOnClickListener {
            currentState = State.Answer
            if (secondArgument.isEmpty()) {
                currentOperation = Operation.Undefined
            }
            updateViewLines()
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getString(STATE_KEY)?.let { State.valueOf(it) }
                ?: State.FirstArgument
            currentOperation =
                savedInstanceState.getString(OPERATION_KEY)?.let { Operation.valueOf(it) }
                    ?: Operation.Undefined

            isError = savedInstanceState.getBoolean(IS_ERROR_KEY, false)

            firstArgument = savedInstanceState.getString(FIRST_ARGUMENT_KEY, "")
            secondArgument = savedInstanceState.getString(SECOND_ARGUMENT_KEY, "")
        }

        updateViewLines()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_KEY, currentState.toString())
        outState.putString(OPERATION_KEY, currentOperation.toString())

        outState.putBoolean(IS_ERROR_KEY, isError)

        outState.putString(FIRST_ARGUMENT_KEY, firstArgument)
        outState.putString(SECOND_ARGUMENT_KEY, secondArgument)
    }

    private fun updateByDelete() {
        when (currentState) {
            State.FirstArgument -> firstArgument = firstArgument.dropLast(1)
            State.SecondArgument -> {
                if (secondArgument.isEmpty()) {
                    currentState = State.FirstArgument
                    currentOperation = Operation.Undefined
                }
                else {
                    secondArgument = secondArgument.dropLast(1)
                }
            }
            State.Answer -> {
                firstArgument = ""
                secondArgument = ""
                currentOperation = Operation.Undefined
                currentState = State.FirstArgument
            }
        }

        updateViewLines()
    }


    private fun updateByOperation(operation: Operation) {
        if (isError) {
            return
        }

        if ((secondArgument.isNotEmpty() &&
                    currentState == State.SecondArgument) || currentState == State.Answer
        ) {
            firstArgument = processAnswer()
        }
        else if (firstArgument == "") {
            firstArgument = "0"
        }
        secondArgument = ""
        currentOperation = operation
        currentState = State.SecondArgument

        updateViewLines()
    }

    private fun setSymbolOnClick(
        id: String,
        answerFirstArgument: String,
        update: String.() -> String
    ) {
        val button = findViewById<TextView>(getId(id))
        button.setOnClickListener {
            when (currentState) {
                State.FirstArgument -> firstArgument = firstArgument.update()
                State.SecondArgument -> secondArgument = secondArgument.update()
                State.Answer -> {
                    firstArgument = answerFirstArgument
                    secondArgument = ""
                    currentOperation = Operation.Undefined
                    currentState = State.FirstArgument
                }
            }

            updateViewLines()
        }
    }

    private fun updateViewLines() {
        if (currentOperation == Operation.Undefined) {
            first_line_tv.text = ""
            second_line_tv.text = SINGLE_NUMBER_LINE_TEMP.format(firstArgument)
        }
        else {
            first_line_tv.text = SINGLE_NUMBER_LINE_TEMP.format(firstArgument)
            second_line_tv.text =
                SYMBOL_WITH_NUMBER_LINE_TEMP.format(stringOperation(), secondArgument)
        }

        answer_line_tv.text = SYMBOL_WITH_NUMBER_LINE_TEMP.format(stringEqual, processAnswer())

        if (currentState == State.Answer) {
            focus(answer_line_tv, second_line_tv)
        }
        else {
            focus(second_line_tv, answer_line_tv)
        }

        listOf(
            first_line_tv,
            second_line_tv
        ).map { it.parent as HorizontalScrollView }.forEach { scrollView ->
            scrollView.post { scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT); }
        }
    }

    private fun focus(main: TextView, support: TextView) {
        main.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.bigTextSize)
        )
        main.setTextColor(resources.getColor(R.color.text_dark, theme))

        support.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.smallTextSize)
        )
        support.setTextColor(resources.getColor(R.color.text_light, theme))
    }

    private fun getId(strId: String): Int {
        val idField = R.id::class.java.getDeclaredField(strId)
        return idField.getInt(idField)
    }


    private fun setOperationOnClick(operation: Operation) {
        val button = findViewById<TextView>(getId(operation.id))
        button.setOnClickListener { updateByOperation(operation) }
    }

    private fun processAnswer(): String {
        isError = false

        try {
            if (firstArgument.isEmpty()) {
                return formatNumber(0.0)
            }
            if (secondArgument.isEmpty()) {
                return formatNumber(firstArgument.improvedToDouble())
            }
            val first = firstArgument.improvedToDouble()
            val second = secondArgument.improvedToDouble()

            val answer = when (currentOperation) {
                Operation.Divide -> first / second
                Operation.Mull -> first * second
                Operation.Minus -> first - second
                Operation.Plus -> first + second
                Operation.Undefined -> Double.NaN
            }

            return formatNumber(answer)
        } catch (e: NumberFormatException) {
            isError = true
            return getString(R.string.incorrect_input)
        }
    }

    private fun String.improvedToDouble(): Double = when (this) {
        "." -> 0.0
        "∞" -> Double.POSITIVE_INFINITY
        "-∞" -> Double.NEGATIVE_INFINITY
        else -> toDouble()
    }

    private fun formatNumber(number: Double): String {
        val df = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
        df.maximumFractionDigits = 340 //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

        return df.format(number)
    }

    private fun String.addComma(): String =
        if (this.contains(stringComma)) this else "$this$stringComma"

    private fun String.addNumber(number: Int) =
        if (this == "0") "$number" else "$this$number"

    companion object {
        private const val SINGLE_NUMBER_LINE_TEMP = "%s"
        private const val SYMBOL_WITH_NUMBER_LINE_TEMP = "%s %s"

        private const val STATE_KEY = "MainActivity_StateKey"
        private const val OPERATION_KEY = "MainActivity_OperationKey"
        private const val IS_ERROR_KEY = "MainActivity_IsError"
        private const val FIRST_ARGUMENT_KEY = "MainActivity_FirstArgument"
        private const val SECOND_ARGUMENT_KEY = "MainActivity_SecondArgument"
    }
}
