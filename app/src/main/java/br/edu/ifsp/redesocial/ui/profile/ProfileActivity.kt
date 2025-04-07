package br.edu.ifsp.redesocial.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.redesocial.databinding.ActivityProfileBinding
import br.edu.ifsp.redesocial.ui.home.HomeActivity
import br.edu.ifsp.redesocial.ui.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var galeria: ActivityResultLauncher<PickVisualMediaRequest>
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGallery()
        setupListener()
    }

    private fun setupListener() {
        binding.buttonPicture.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.buttonSave.setOnClickListener {
            if (firebaseAuth.currentUser != null){
                val email = firebaseAuth.currentUser!!.email.toString()
                val username = binding.textUsername.text.toString()
                val nomeCompleto = binding.textFullname.text.toString()
                val fotoPerfilString = Base64Converter.drawableToString(binding.profilePicture.
                drawable)
                val db = Firebase.firestore
                val dados = hashMapOf(
                    "nomeCompleto" to nomeCompleto,
                    "username" to username,
                    "fotoPerfil" to fotoPerfilString
                )
                db.collection("usuarios").document(email)
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            }
        }
    }

    private fun setupGallery() {
        galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                binding.profilePicture.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
    }
}