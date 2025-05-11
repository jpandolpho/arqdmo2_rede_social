package br.edu.ifsp.redesocial.ui.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.edu.ifsp.redesocial.databinding.ActivityPostBinding
import br.edu.ifsp.redesocial.ui.home.HomeActivity
import br.edu.ifsp.redesocial.ui.util.Base64Converter
import br.edu.ifsp.redesocial.util.LocalizacaoHelper
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlin.random.Random

class PostActivity : AppCompatActivity(), LocalizacaoHelper.Callback {
    private lateinit var binding: ActivityPostBinding
    private lateinit var galeria: ActivityResultLauncher<PickVisualMediaRequest>
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        solicitarLocalizacao()
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
                var city = ""
                var state = ""
                val db = Firebase.firestore
                val owner = firebaseAuth.currentUser!!.email.toString()
                val data = Timestamp.now()
                if(binding.switchLocation.isChecked){
                    city = binding.cityLocation.text.toString()
                    state = binding.stateLocation.text.toString()
                }
                val dados = hashMapOf(
                    "descricao" to descricao,
                    "fotoPost" to fotoString,
                    "city" to city,
                    "state" to state,
                    "owner" to owner,
                    "timestamp" to data
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

    private fun solicitarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }else {
            val localizacaoHelper = LocalizacaoHelper(applicationContext)
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
        runOnUiThread {
            val city = endereco.subAdminArea
            val state = endereco.adminArea
            binding.cityLocation.text = city
            binding.stateLocation.text = state
        }
    }

    override fun onErro(mensagem: String) {
        System.out.println(mensagem)
    }
}