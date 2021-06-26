package com.dengetelekom.telsiz.nfc

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class WritableTag @Throws(FormatException::class) constructor(tag: Tag) {
    private val NDEF = Ndef::class.java.canonicalName
    private val NDEF_FORMATABLE = NdefFormatable::class.java.canonicalName

    private val ndef: Ndef?
    private val ndefFormatable: NdefFormatable?

    val tagId: String?
        get() {
            if (ndef != null) {
                return bytesToHexString(ndef.tag.id)
            } else if (ndefFormatable != null) {
                return bytesToHexString(ndefFormatable.tag.id)
            }
            return null
        }

    init {
        val technologies = tag.techList
        val tagTechs = listOf(*technologies)
        when {
            tagTechs.contains(NDEF) -> {
                Log.i("WritableTag", "contains ndef")
                ndef = Ndef.get(tag)
                ndefFormatable = null
            }
            tagTechs.contains(NDEF_FORMATABLE) -> {
                Log.i("WritableTag", "contains ndef_formatable")
                ndefFormatable = NdefFormatable.get(tag)
                ndef = null
            }
            else -> {
                throw FormatException("Tag doesn't support ndef")
            }
        }
    }

    @Throws(IOException::class, FormatException::class)
    fun writeData(tagId: String,
                  message: String): Boolean {

        val ndfMessage = createNdefMessage(message)
        if (tagId != tagId) {
            return false
        }
        if (ndef != null) {
            if (!ndef.isConnected) ndef.connect()
            if (ndef.isConnected) {
                ndef.writeNdefMessage(ndfMessage)
                return true
            }
        } else if (ndefFormatable != null) {
            ndefFormatable.connect()
            if (ndefFormatable.isConnected) {
                ndefFormatable.format(ndfMessage)
                return true
            }
        }
        return false
    }

    private fun createNdefMessage(message: String): NdefMessage {
        val ndefMessage: NdefMessage
        val records = arrayOfNulls<NdefRecord>(1)
        records[0] = getTextRecord(message, Locale.ENGLISH, true)
        ndefMessage = NdefMessage(records)
        return ndefMessage
    }

    private fun getTextRecord(message: String, locale: Locale, encodeInUtf8: Boolean): NdefRecord {
        val langBytes = locale.language.toByteArray(Charset.forName("US-ASCII"))
        val utfEncoding = if (encodeInUtf8) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val textBytes = message.toByteArray(utfEncoding)
        val utfBit: Int = if (encodeInUtf8) 0 else 1 shl 7
        val status = (utfBit + langBytes.size).toChar()
        val data = ByteArray(1 + langBytes.size + textBytes.size)
        data[0] = status.toByte()
        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), data)
    }

    @Throws(IOException::class)
    private fun close() {
        ndef?.close() ?: ndefFormatable?.close()
    }

    companion object {
        fun bytesToHexString(src: ByteArray): String? {
            if (src.isEmpty()) {
                return null
            }
            val sb = StringBuilder()
            for (b in src) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }
    }
}