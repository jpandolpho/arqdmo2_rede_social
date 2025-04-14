package br.edu.ifsp.redesocial.ui.post

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.redesocial.databinding.ActivityPostBinding
import br.edu.ifsp.redesocial.ui.home.HomeActivity
import br.edu.ifsp.redesocial.ui.util.Base64Converter
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlin.random.Random

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var galeria: ActivityResultLauncher<PickVisualMediaRequest>
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGallery()
        setupListener()
    }

    private fun setupGallery() {
        galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                binding.picture.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
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
                val descricao = binding.textDescription.text.toString()
                val fotoString = Base64Converter.drawableToString(binding.picture.
                drawable)
                val db = Firebase.firestore
                val dados = hashMapOf(
                    "descricao" to descricao,
                    "fotoPost" to fotoString
                )
                db.collection("posts").document(Random.nextInt().toString())
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
            }
        }
    }
}