package com.example.synomprojectkotlin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler().postDelayed({
            val i = Intent(
                this@LoadingActivity,
                LoginActivity::class.java
            )
            startActivity(i)
            finish()
        }, 2000)
    }
}