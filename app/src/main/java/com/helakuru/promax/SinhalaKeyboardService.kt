
package com.helakuru.promax

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SinhalaKeyboardService : InputMethodService() {
    private var isSinhalaMode = true
    private var isTranslateOn = false
    private var currentSiBuffer = ""
    private lateinit var translator: TranslatorManager
    private lateinit var translationBar: LinearLayout
    private lateinit var tvTranslation: TextView
    
    // හෙලකුරු layout එක - ඔයාට ඕන නම් මෙතන අකුරු වෙනස් කරන්න පුළුවන්
    private val helakuruRows = listOf(
        listOf("අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ"),
        listOf("ක", "ග", "ච", "ජ", "ට", "ඩ", "ණ", "ත"),
        listOf("ද", "න", "ප", "බ", "ම", "ය", "ර", "ල"),
        listOf("ව", "ස", "හ", "ළ", "ං", "ඃ", "්", "ා")
    )
    private val englishRows = listOf(
        listOf("q","w","e","r","t","y","u","i","o","p"),
        listOf("a","s","d","f","g","h","j","k","l"),
        listOf("z","x","c","v","b","n","m")
    )
    private val singlishMap = mapOf(
        "oya" to "ඔයා", "kohomada" to "කොහොමද", "mama" to "මම",
        "api" to "අපි", "mata" to "මට", "oyata" to "ඔයාට",
        "hari" to "හරි", "lassanai" to "ලස්සනයි", "ayubowan" to "ආයුබෝවන්"
    )

    override fun onCreate() {
        super.onCreate()
        translator = TranslatorManager(this)
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(com.helakuru.promax.R.layout.keyboard_view, null)
        translationBar = view.findViewById(R.id.translationBar)
        tvTranslation = view.findViewById(R.id.tvTranslation)
        val container = view.findViewById<LinearLayout>(R.id.keyboardContainer)
        val btnToggle = view.findViewById<Button>(R.id.btnToggleTranslate)
        val btnSendEn = view.findViewById<Button>(R.id.btnSendEn)
        
        buildKeyboard(container)

        btnToggle.setOnClickListener {
            isTranslateOn = !isTranslateOn
            btnToggle.text = if(isTranslateOn) "🌐 EN ON" else "🌐 EN OFF"
            translationBar.visibility = if(isTranslateOn && currentSiBuffer.isNotEmpty()) View.VISIBLE else View.GONE
            if(isTranslateOn) translator.downloadModelIfNeeded()
        }
        btnSendEn.setOnClickListener {
            val enText = tvTranslation.text.toString().removePrefix("EN: ")
            currentInputConnection?.commitText(enText + " ", 1)
            currentSiBuffer = ""
            translationBar.visibility = View.GONE
        }
        return view
    }

    private fun buildKeyboard(container: LinearLayout) {
        container.removeAllViews()
        val rows = if(isSinhalaMode) helakuruRows else englishRows
        for(row in rows) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(-1, -2)
            }
            for(key in row) {
                val btn = Button(this).apply {
                    text = key
                    layoutParams = LinearLayout.LayoutParams(0, 120, 1f).apply { setMargins(2,2,2,2) }
                    setOnClickListener { onKeyPress(key) }
                }
                rowLayout.addView(btn)
            }
            container.addView(rowLayout)
        }
        // bottom row - space, delete, language switch
        val bottom = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        bottom.addView(Button(this).apply { text="සිං/EN"; layoutParams=LinearLayout.LayoutParams(0,-2,1f); setOnClickListener { isSinhalaMode=!isSinhalaMode; buildKeyboard(container) } })
        bottom.addView(Button(this).apply { text="SPACE"; layoutParams=LinearLayout.LayoutParams(0,-2,2f); setOnClickListener { onKeyPress(" ") } })
        bottom.addView(Button(this).apply { text="⌫"; layoutParams=LinearLayout.LayoutParams(0,-2,1f); setOnClickListener { currentInputConnection?.deleteSurroundingText(1,0); currentSiBuffer = currentSiBuffer.dropLast(1); updateTranslate() } })
        container.addView(bottom)
    }

    private fun onKeyPress(key: String) {
        val ic = currentInputConnection ?: return
        if(key == " ") {
            // Singlish check
            val converted = singlishMap[currentSiBuffer.lowercase()]
            if(converted != null && isSinhalaMode) {
                ic.deleteSurroundingText(currentSiBuffer.length, 0)
                ic.commitText(converted + " ", 1)
            } else {
                ic.commitText(key, 1)
            }
            currentSiBuffer = ""
            updateTranslate()
        } else {
            ic.commitText(key, 1)
            currentSiBuffer += key
            updateTranslate()
        }
    }

    private fun updateTranslate() {
        if(!isTranslateOn || currentSiBuffer.isEmpty()) {
            translationBar.visibility = View.GONE
            return
        }
        translator.translateSiToEn(currentSiBuffer) { en ->
            tvTranslation.text = "EN: $en"
            translationBar.visibility = View.VISIBLE
        }
    }
}
