package com.industri.fleettrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.industri.fleettrack.ui.manifest.ManifestActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ManifestActivity::class.java)
        startActivity(intent)
        finish()
    }
}
