package com.example.synomprojectkotlin

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var headerText: TextView
    private lateinit var emailInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var sendNewPasswordBtn: Button
    private lateinit var submitPasswordBtn: Button

    val passwordRegex = "^(?=.*[A-Z])(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$".toRegex()
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        dataBaseHelper = DataBaseHelper(this)

        headerText = findViewById(R.id.headerText)
        emailInput = findViewById(R.id.emailInput)
        newPasswordInput = findViewById(R.id.newPasswordInput)
        sendNewPasswordBtn = findViewById(R.id.sendNewPasswordBtn)
        submitPasswordBtn = findViewById(R.id.submitPasswordBtn)

        sendNewPasswordBtn.setOnClickListener {
            val email: String = emailInput.text.toString().trim {it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity,
                    "Пожалуйста, введите почту",
                    Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgotPasswordActivity,
                                "Вам на почту отправлен пароль!",
                                Toast.LENGTH_SHORT).show()

                            headerText.text = "Введите новый пароль в это поле"
                            emailInput.visibility = View.INVISIBLE
                            sendNewPasswordBtn.visibility = View.INVISIBLE
                            newPasswordInput.visibility = View.VISIBLE
                            submitPasswordBtn.visibility = View.VISIBLE
                        }
                    }
            }

            submitPasswordBtn.setOnClickListener {
                if (newPasswordInput.text.toString() != null || newPasswordInput.text.toString() != "") {
                    if (!passwordRegex.matches(newPasswordInput.text.toString())) {
                        showPasswordRequirementsDialog()
                    } else {
                        dataBaseHelper.updateUser(email, newPasswordInput.text.toString())
                        Toast.makeText(
                            this@ForgotPasswordActivity, "Пароль был успешно изменен!",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
        }
    }

    private fun showPasswordRequirementsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Неверный пароль")
            .setMessage("Пароль должен содержать:\n" +
                    "- минимум 8 символов\n" +
                    "- одну заглавную букву\n" +
                    "- одну цифру")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}