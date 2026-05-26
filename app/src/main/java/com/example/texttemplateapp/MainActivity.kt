package com.example.texttemplateapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.PinnableContainer
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.texttemplateapp.ui.theme.TextTemplateAppTheme

// MVVMアーキテクチャを採用している
// Model    : TemplateState
// ViewModel: TemplateViewModel
// View     : MainActivity + XML
class MainActivity : ComponentActivity() {
    private lateinit var viewModel:
        TemplateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // XMLからUI構成要素を取得
        val previewText = findViewById<TextView>(R.id.previewText)
        val container = findViewById<LinearLayout>(R.id.container)
        val copyButton = findViewById<Button>(R.id.copyButton)

        viewModel = ViewModelProvider(this)[
            TemplateViewModel::class.java
        ]

        viewModel.loadTemplate(
            "こんにちは、{name}。\n好きなゲームは {game} です。"
        )

        // LiveData監視
        viewModel.previewText.observe(this) {
            previewText.text = it
        }

        // EditText生成
        for (key in viewModel.getPlaceholderKeys()) {
            val editText = EditText(this)
            editText.hint = key
            editText.addTextChangedListener {
                viewModel.updatePlaceholder(
                    key,
                    it.toString()
                )
            }

            container.addView(editText)
        }

        // コピー
        copyButton.setOnClickListener {
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager

            val clip =
                ClipData.newPlainText(
                    "text",
                    previewText.text
                )

            clipboard.setPrimaryClip(clip)

            finish()
        }
    }
}
private val TEMPLATE_REGEX = "\\{(.*?)\\}".toRegex()
data class TemplateState (
    val templateText: String,
    var placeholders: Map<String, String>
) {
    companion object {
        fun create(
            templateText: String
        ): TemplateState {
            val map = mutableMapOf<String, String>()
            for (match in TEMPLATE_REGEX.findAll(templateText)) {
                val matchValue = match.groupValues[1]
                map[matchValue] = matchValue
            }

            return TemplateState(
                templateText,
                map
            )
        }
    }

    fun buildText(): String {
        // マッチするたびに {} の処理が実行される
        return TEMPLATE_REGEX.replace(templateText) { match ->
            val key = match.groupValues[1]
            placeholders[key] ?: ""
        }
    }
}

class TemplateViewModel : ViewModel() {
    private val _previewText = MutableLiveData("")

    val previewText: LiveData<String>
        get() = _previewText

    private lateinit var templateState: TemplateState

    fun loadTemplate(template: String) {
        templateState = TemplateState.create(template)

        updatePreview()
    }

    fun updatePlaceholder(
        key: String,
        value: String
    ) {
        // マップ(Placeholder)に対して要素追加
        val newMap = templateState.placeholders.toMutableMap()
        newMap[key] = value
        templateState = templateState.copy(
            placeholders = newMap
        )

        updatePreview()
    }

    private fun updatePreview() {
        _previewText.value = templateState.buildText()
    }

    fun getPlaceholderKeys(): List<String> {
        return templateState
            .placeholders
            .keys
            .toList()
    }
}
