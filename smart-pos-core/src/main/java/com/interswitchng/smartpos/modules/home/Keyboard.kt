package com.interswitchng.smartpos.modules.home

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.interswitchng.smartpos.R


internal class Keyboard(activity: Activity, callback: KeyBoardListener) : View.OnClickListener {

    private var mContext: Context? = null
    private var result = ""
    private var mCallback: KeyBoardListener? = null

    interface KeyBoardListener {
        fun onTextChange(text: String)

        fun onSubmit(text: String)
    }


    init {
        setupButtons(activity, callback)
    }


    private fun setupButtons(activity: Activity, callback: KeyBoardListener) {
        mContext = activity
        // add buttons
        activity.findViewById<View>(R.id.one).setOnClickListener(this)
        activity.findViewById<View>(R.id.two).setOnClickListener(this)
        activity.findViewById<View>(R.id.three).setOnClickListener(this)
        activity.findViewById<View>(R.id.four).setOnClickListener(this)
        activity.findViewById<View>(R.id.five).setOnClickListener(this)
        activity.findViewById<View>(R.id.six).setOnClickListener(this)
        activity.findViewById<View>(R.id.seven).setOnClickListener(this)
        activity.findViewById<View>(R.id.eight).setOnClickListener(this)
        activity.findViewById<View>(R.id.nine).setOnClickListener(this)
        activity.findViewById<View>(R.id.oneZero).setOnClickListener(this)
        activity.findViewById<View>(R.id.twoZeros).setOnClickListener(this)
        activity.findViewById<View>(R.id.threeZeros).setOnClickListener(this)
        activity.findViewById<View>(R.id.done).setOnClickListener(this)

        val delete = activity.findViewById<ImageView>(R.id.delete)
        delete.setOnClickListener(this)
        // clear text on long click
        delete.setOnLongClickListener { _ ->
            result = ""
            if (mCallback != null) mCallback!!.onTextChange(result)
            true
        }


        // set callback
        mCallback = callback
    }

    override fun onClick(view: View) {
        val maxValue = 1_000_000_00L // 1 million (1,000,000.00)
        var result = this.result

         when (view.id) {
            R.id.one        -> result += "1"
            R.id.two        -> result += "2"
            R.id.three      -> result += "3"
            R.id.four       -> result += "4"
            R.id.five       -> result += "5"
            R.id.six        -> result += "6"
            R.id.seven      -> result += "7"
            R.id.eight      -> result += "8"
            R.id.nine       -> result += "9"
            R.id.oneZero    -> result += "0"
            R.id.twoZeros   -> result += "00"
            R.id.threeZeros -> result += "000"
            R.id.done       -> mCallback?.onSubmit(result)
            R.id.delete     -> if (result.isNotEmpty()) {
                result = result.substring(0, result.length - 1)
            }
            else -> return
        }

        val isDisabled = !result.isEmpty() && result.toLong() > maxValue

        if (!isDisabled) {
            this.result = result
            if (mCallback != null) mCallback!!.onTextChange(result)
        } else {
            // show max input
            Toast.makeText(mContext, "Max value is 1 Million", Toast.LENGTH_SHORT).show()
        }

    }

    fun setText(text: String) {
        result = text
    }
}
