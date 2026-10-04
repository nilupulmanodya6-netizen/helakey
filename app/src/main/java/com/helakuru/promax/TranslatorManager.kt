
package com.helakuru.promax
import android.content.Context
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*

class TranslatorManager(context: Context) {
    private var translator: Translator? = null
    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.SINHALA)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        translator = Translation.getClient(options)
    }
    fun downloadModelIfNeeded() {
        translator?.downloadModelIfNeeded(DownloadConditions.Builder().build())
    }
    fun translateSiToEn(text: String, onResult: (String)->Unit) {
        if(text.isBlank()) return
        translator?.translate(text)?.addOnSuccessListener { onResult(it) }
            ?.addOnFailureListener { onResult("[Translate failed]") }
    }
}
