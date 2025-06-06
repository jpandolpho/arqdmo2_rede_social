package br.edu.ifsp.redesocial.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.redesocial.adapter.PostAdapter
import br.edu.ifsp.redesocial.databinding.ActivityHomeBinding
import br.edu.ifsp.redesocial.model.Post
import br.edu.ifsp.redesocial.ui.main.MainActivity
import br.edu.ifsp.redesocial.ui.post.PostActivity
import br.edu.ifsp.redesocial.ui.profile.ProfileActivity
import br.edu.ifsp.redesocial.ui.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: PostAdapter
    private lateinit var posts: ArrayList<Post>
    private var lastTimestamp: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
    }

    private fun loadPosts(search: String) {
        val db = Firebase.firestore
        var query = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
        if(search!=""){
            query = query.whereEqualTo("city", search)
        }else{
            query = query.limit(5)
        }
        if(lastTimestamp!=null){
            query = query.startAfter(lastTimestamp)
        }
        query.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if(!document.isEmpty){
                        lastTimestamp = if(document.documents.size==5)
                            document.documents.last().getTimestamp("timestamp")
                        else
                            null
                    }
                    posts = ArrayList<Post>()
                    for (document in document.documents) {
                        val imageString = document.data!!["fotoPost"].toString()
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        val descricao = document.data!!["descricao"].toString()
                        val city = document.data!!["city"].toString()
                        val state = document.data!!["state"].toString()
                        val owner = document.data!!["owner"].toString()
                        posts.add(Post(descricao, bitmap, city, state, owner))
                    }
                    adapter = PostAdapter(posts.toTypedArray())
                    binding.postList.layoutManager = LinearLayoutManager(this)
                    binding.postList.adapter = adapter
                }
            }
    }

    private fun setupListener() {
        binding.buttonLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.buttonProfile.setOnClickListener{
            val mIntent = Intent(this, ProfileActivity::class.java)
            mIntent.putExtra("EDIT",true)
            startActivity(mIntent)
            finish()
        }

        binding.buttonAddPost.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
        }

        binding.buttonLoad.setOnClickListener {
            loadPosts("")
        }

        binding.textlayoutSearch.setEndIconOnClickListener {
            loadPosts(binding.textCity.text.toString())
        }
    }
}