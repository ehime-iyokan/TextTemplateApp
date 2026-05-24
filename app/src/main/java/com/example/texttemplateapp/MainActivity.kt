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
import com.example.texttemplateapp.ui.theme.TextTemplateAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val templatePattern = TemplatePattern(
            "こんにちは、{name}。\n私の好きなゲームは {game} です。"
        )
        val templateView = TemplateView(
            findViewById<TextView>(R.id.previewText),
            findViewById<Button>(R.id.copyButton),
            findViewById<LinearLayout>(R.id.container),
            templatePattern
        )

        templateView.SetContainer(
            this,
            templatePattern.placeHoldersList,
            templatePattern.placeHoldersMap
        )
        templateView.copyButton.setOnClickListener {
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager

            val clip = ClipData.newPlainText(
                "text",
                templateView.preViewText.text.toString()
            )

            clipboard.setPrimaryClip(clip)

            // finish()
        }
    }
}
class TemplatePattern (
    val templateText: String
) {
    val currentIndex = 0
    val placeHoldersMap = mutableMapOf<String, String>()
    val placeHoldersList = mutableListOf<String>()

    init {
        val regex = "\\{(.*?)\\}".toRegex()

        for (match in regex.findAll(templateText)) {
            placeHoldersList.add(match.groupValues[1])
            placeHoldersMap[match.groupValues[1]] = ""
        }
    }
    fun buildText(): String {
        val regex = "\\{(.*?)\\}".toRegex()

        return regex.replace(templateText) { match ->
            val key = match.groupValues[1]
            placeHoldersMap[key] ?: ""
        }
    }
}
class TemplateView (
    val preViewText: TextView,
    val copyButton: Button,
    val container: LinearLayout,

    val templatePattern:TemplatePattern
) {
    fun SetContainer(
        context: Context,
        keys: List<String>,
        map: MutableMap<String, String>
    )
    {
        for (key in keys) {
            val editText = EditText(context)
            editText.hint = key

            editText.addTextChangedListener {
                map[key] = editText.text.toString()
                preViewText.text = templatePattern.buildText()
            }

            container.addView(editText)
        }
    }
}
