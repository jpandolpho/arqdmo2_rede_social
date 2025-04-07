package br.edu.ifsp.redesocial.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.redesocial.databinding.ActivityMainBinding
import br.edu.ifsp.redesocial.ui.home.HomeActivity
import br.edu.ifsp.redesocial.ui.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        checkUser()
    }

    private fun checkUser() {
        if(firebaseAuth.currentUser!=null){
            launchHome()
        }
    }

    private fun setupListener() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val senha = binding.textSenha.text.toString()
            firebaseAuth
                .signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        launchHome()
                    }else{
                        Toast.makeText(this, "Erro no Login", Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.buttonSignup.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

    private fun launchHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}