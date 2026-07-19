
package com.realvoicemask
import android.Manifest
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private val engine = RealVoiceEngine()
    private var running = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        val btn = Button(this).apply { text = "Start Girl Voice" }
        (findViewById(android.R.id.content) as android.view.ViewGroup).addView(btn)
        btn.setOnClickListener {
            if (!running) { engine.start(); btn.text = "Stop"; running = true }
            else { engine.stop(); btn.text = "Start Girl Voice"; running = false }
        }
    }
    override fun onDestroy() { engine.stop(); super.onDestroy() }
}
