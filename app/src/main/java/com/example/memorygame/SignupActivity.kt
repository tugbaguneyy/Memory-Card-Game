package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.memorygame.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var  firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.signup.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmpass = binding.passwordConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmpass.isNotEmpty()) {
                if (password == confirmpass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity((intent))
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Parola eşleşmedi", Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(this, "Boş bırakılamaz alanlar!", Toast.LENGTH_SHORT).show()
            }

        }
        binding.loginRedirectText.setOnClickListener{
            val loginIntent=Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)
        }


    }
    }
