package com.example.texttemplateapp

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button

class MyImeService : InputMethodService() {
    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(
            R.layout.ime_view,
            null,
            false
        )
        val button =
            view.findViewById<Button>(R.id.openButton)

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        return view
    }
}