package com.example.texttemplateapp

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button

class SelectTemplateService : InputMethodService() {
    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(
            R.layout.select_template_service,
            null,
            false
        )
        val button =
            view.findViewById<Button>(R.id.openButton)

        button.setOnClickListener {
            val intent = Intent(this, UseTemplateActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            switchToNextInputMethod(false)
        }

        return view
    }
}