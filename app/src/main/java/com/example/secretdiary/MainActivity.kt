package com.example.secretdiary

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

const val PREFERENCES_NAME = "PREF_DIARY"

class MainActivity : AppCompatActivity() {
    private lateinit var diary: TextView
    private lateinit var editText: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        diary = findViewById(R.id.tvDiary)
        editText = findViewById(R.id.etNewWriting)
        diary.text = ""
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            if (editText.text.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.blank_input),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                var (dateStr, timeStr) = local.toString().split("T")
                timeStr = timeStr.substring(0, 8)
                val noteText = editText.text
                val newNote = "$dateStr $timeStr\n$noteText"
                var currentText = diary.text
                diary.text = if (currentText.isBlank()) newNote else newNote + "\n\n" + currentText
                editText.text = ""
            }
            findViewById<Button>(R.id.btnUndo).setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Remove last note")
                    .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
                    .setPositiveButton("Yes") { _, _ ->

                        val text = diary.text.toString()
                        if (!text.contains("\n\n")) diary.text = ""
                        else {
                            diary.text = text.substringAfter("\n\n").trim()
                            editor.putString("KEY_DIARY_TEXT", diary.text.toString()).apply()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()

            }
        }
    }
}