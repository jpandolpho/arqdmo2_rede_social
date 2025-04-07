package br.edu.ifsp.redesocial.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.redesocial.databinding.ActivitySignUpBinding
import br.edu.ifsp.redesocial.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
    }

    private fun setupListener() {
        binding.buttonSignup.setOnClickListener {
            if (checkFields()) {
                val email = binding.textEmail.text.toString()
                val senha = binding.textSenha.text.toString()
                firebaseAuth
                    .createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }

            }
        }
    }

    private fun checkFields(): Boolean {
        return !binding.textEmail.text.isNullOrBlank()
                && !binding.textSenha.text.isNullOrBlank()
                && !binding.textConfirm.text.isNullOrBlank()
                && (binding.textSenha.text.toString() == binding.textConfirm.text.toString())
    }
}