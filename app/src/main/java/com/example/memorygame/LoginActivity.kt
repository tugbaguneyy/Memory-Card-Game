package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.memorygame.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var  binding:ActivityLoginBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        binding.singin.setOnClickListener{
            val email=binding.email.text.toString()
            val password=binding.password.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val intent= Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this,"Boş bırakılamaz alanlar!",Toast.LENGTH_SHORT).show()

            }
        }
        binding.signupRedirectText.setOnClickListener{
            val signupIntent=Intent(this,SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}