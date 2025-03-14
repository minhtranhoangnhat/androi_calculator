package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal)
                    binding.workingtv.append(view.text)
                canAddDecimal = false
            } else
                binding.workingtv.append(view.text)
            canAddOperation = true
        }
    }

    fun operatorAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.workingtv.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        binding.workingtv.text = ""
        binding.resulttv.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingtv.text.length
        if (length > 0)
            binding.workingtv.text = binding.workingtv.text.subSequence(0, length - 1)
    }

    fun clearEntryAction(view: View) {
        binding.workingtv.text = ""
    }

    fun equalsAction(view: View){
        binding.resulttv.text = calculateResults()
    }

    private fun calculateResults(): String{
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)

        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float{
        var result = passedList[0] as Float

        for(i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if(operator == '+') result += nextDigit
                if(operator == '-') result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>{
        var list = passedList
        while(list.contains('x') || list.contains('/')){
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator){
                    'x' ->{
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->{
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else ->{
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            if(i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any>{
        var list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in binding.workingtv.text){
            if(character.isDigit() || character == '.')
                currentDigit += character
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "") list.add(currentDigit.toFloat())

        return list
    }
}