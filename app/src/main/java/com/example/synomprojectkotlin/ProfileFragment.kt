package com.example.synomprojectkotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var _username: TextView
    private lateinit var _email: TextView
    private lateinit var _btnToFrgtPassword: Button
    private lateinit var _btnExit: Button
    private lateinit var _synonymRecord: TextView
    private lateinit var _definitionRecord: TextView

    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _username = view.findViewById(R.id.username_tv)
        _email = view.findViewById(R.id.email_tv)
        _btnToFrgtPassword = view.findViewById(R.id.btn_edit)
        _btnExit = view.findViewById(R.id.btn_exit)
        _synonymRecord = view.findViewById(R.id.synonym_record_tv)
        _definitionRecord = view.findViewById(R.id.definition_record_tv)

        dataBaseHelper = DataBaseHelper(requireContext())

        val sharedPref = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val username = sharedPref.getString("USERNAME", "Unknown")
        val email = sharedPref.getString("EMAIL", "Unknown")
        val password = sharedPref.getString("PASSWORD", "Unknown")

        _username.text = username
        _email.text = email

        email?.let {
            val user = dataBaseHelper.readUserForScore(email, password.toString())

            if (user != null) {
                _synonymRecord.text = user.maxSynonymScore.toString()
                _definitionRecord.text = user.maxDefinitionScore.toString()
            }
        }

        _btnToFrgtPassword.setOnClickListener{
            startActivity(Intent(requireContext(), ForgotPasswordActivity::class.java))
        }

        _btnExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val sharedPref = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                apply()
            }

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}