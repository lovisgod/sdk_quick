package com.interswitchng.smartpos.shared.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.interswitchng.smartpos.R

class Keyboard constructor(
    context: Context,
    param: AttributeSet
) : ConstraintLayout(context, param), View.OnClickListener {

    private val _currentAmount = MutableLiveData<String>().apply { value = "" }
    val currentAmount: LiveData<String>
        get() = _currentAmount

    init {
        inflate(context, R.layout.isw_keyboard_view, this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.isw_keypad_zero -> setAmount("0")
            R.id.isw_keypad_one -> setAmount("1")
            R.id.isw_keypad_two -> setAmount("2")
            R.id.isw_keypad_three -> setAmount("3")
            R.id.isw_keypad_four -> setAmount("4")
            R.id.isw_keypad_five -> setAmount("5")
            R.id.isw_keypad_six-> setAmount("6")
            R.id.isw_keypad_seven -> setAmount("7")
            R.id.isw_keypad_eight -> setAmount("8")
            R.id.isw_keypad_nine -> setAmount("9")
        }
    }

    private fun setAmount(value: String) {
        val currentValue = _currentAmount.value!!
        _currentAmount.postValue(currentValue + value)
    }
}