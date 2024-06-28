package com.example.synomprojectkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.synomprojectkotlin.databinding.ActivitySignupBinding
import com.example.synomprojectkotlin.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    val passwordRegex = "^(?=.*[A-Z])(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$".toRegex()
    private lateinit var binding: ActivitySignupBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        dataBaseHelper = DataBaseHelper(this)
        binding.registrButton.setOnClickListener {
            val signupUsername = binding.usernameInput.text.toString()
            val signupEmail = binding.emailInput.text.toString()
            val signupPassword = binding.passwordInput.text.toString()
            val signupRepeatPassword = binding.passwordRepeatInput.text.toString()

            if (signupPassword != signupRepeatPassword) {
                Toast.makeText(this, "Пароли не совпадают, попробуйте еще раз", Toast.LENGTH_SHORT).show()
            } else {
                if (!passwordRegex.matches(signupPassword)) {
                    showPasswordRequirementsDialog()
                } else {
                    val user = Users(0, signupUsername, signupEmail, signupPassword, 2)
                    signupDatabase(user)
                }
            }
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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

    private fun signupDatabase(users: Users) {
        auth.createUserWithEmailAndPassword(users.email, users.password)
            .addOnSuccessListener {
                val insertedRowId = dataBaseHelper.addUser(users, this)
                if (insertedRowId != -1L) {
                    Toast.makeText(this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Регистрация в базе данных не удалась", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                if (e is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "Пользователь с такой почтой уже существует", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}