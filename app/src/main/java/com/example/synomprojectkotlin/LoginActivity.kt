package com.example.synomprojectkotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.synomprojectkotlin.databinding.ActivityLoginBinding
import com.example.synomprojectkotlin.models.Users
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var dataBaseHelper: DataBaseHelper
    lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)

        auth = Firebase.auth
        var currentEmail = auth.currentUser?.email

        if (auth.currentUser != null && dataBaseHelper.readUserFirebase(currentEmail.toString()) != null) {
            val currentUserAdmin = auth.currentUser!!.email
            if (currentUserAdmin == "mirzoev.nidzhat@mail.ru") {
                val user = dataBaseHelper.readUserFirebase(currentUserAdmin)
                val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("USERNAME", user!!.username)
                    putString("EMAIL", user.email)
                    putString("PASSWORD", user.password)
                    apply()
                }
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            }
            else{
                val currentUser = auth.currentUser!!.email
                val user = dataBaseHelper.readUserFirebase(currentUser!!)
                val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("USERNAME", user!!.username)
                    putString("EMAIL", user.email)
                    putString("PASSWORD", user.password)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.loginButton.setOnClickListener {
            val loginEmail = binding.emailLoginInput.text.toString()
            val loginPassword = binding.passwordLoginInput.text.toString()

            loginDatabase(loginEmail, loginPassword)

        }

        binding.regLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.forgotPasLink.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginDatabase(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = dataBaseHelper.readUser(email, password)

                if (user != null) {
                    if (user.roleId == 1) {
                        Toast.makeText(this, "Вход как администратор прошел успешно!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        Toast.makeText(this, "Вход прошел успешно!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USERNAME", user.username)
                        putString("EMAIL", user.email)
                        putString("PASSWORD", user.password)
                        apply()
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Ошибка входа: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}