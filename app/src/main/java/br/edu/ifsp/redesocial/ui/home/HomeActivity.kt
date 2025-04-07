package br.edu.ifsp.redesocial.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.redesocial.databinding.ActivityHomeBinding
import br.edu.ifsp.redesocial.ui.main.MainActivity
import br.edu.ifsp.redesocial.ui.profile.ProfileActivity
import br.edu.ifsp.redesocial.ui.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupScreen()
        setupListener()
    }

    private fun setupScreen() {
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()
        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if(!document.data.isNullOrEmpty()) {
                        val imageString = document.data!!["fotoPerfil"].toString()
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        binding.profilePicture.setImageBitmap(bitmap)
                        binding.username.text = document.data!!["username"].toString()
                        binding.fullname.text =
                            document.data!!["nomeCompleto"].toString()
                    }
                }
            }
    }

    private fun setupListener() {
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
    }
}