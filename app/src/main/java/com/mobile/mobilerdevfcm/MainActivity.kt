package com.mobile.mobilerdevfcm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.mobilerdevfcm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parseIntentExtras(intent.extras)
    }

    private fun parseIntentExtras(bundle: Bundle?) {
        bundle?.let {
            val content = it.getString(NotificationService.PARAM_CUSTOM_MESSAGE)
            content?.let { message ->
                binding.tvMessage.text = message
            }
        }
    }
}