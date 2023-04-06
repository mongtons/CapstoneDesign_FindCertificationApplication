package hallym.capstone.findcertificateapplication

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityBoardBinding
import hallym.capstone.findcertificateapplication.databinding.BenefitCompanyItemBinding
import hallym.capstone.findcertificateapplication.databinding.CommentItemBinding
import hallym.capstone.findcertificateapplication.datatype.Comment

class BoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityBoardBinding.inflate(layoutInflater)
    }
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val freeBoardRef: DatabaseReference =database.getReference("Free_Board")
//    val studyBoardRef: DatabaseReference =database.getReference("Study_Board")
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val authRef: DatabaseReference = database.getReference("loginTest")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.title.text=intent.getStringExtra("title")
        binding.user.text=intent.getStringExtra("user")
        binding.time.text=intent.getStringExtra("time")

        freeBoardRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(board in snapshot.children){
                    if(intent.getStringExtra("id")==board.key.toString()){
                        binding.summary.text=board.child("body").value.toString()
                        if(board.child("comment").hasChildren()) {
                            val commentList= mutableListOf<Comment>()
                            for (comment in board.child("comment").children){
                                val data = Comment(
                                    comment.child("user").value.toString(),
                                    comment.child("letter").value.toString()
                                )
                                commentList.add(data)
                            }
                            val layoutManager= LinearLayoutManager(this@BoardActivity)
                            layoutManager.orientation= LinearLayoutManager.VERTICAL

                            binding.comment.layoutManager=layoutManager
                            binding.comment.adapter=CommentAdapter(commentList)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: java.lang.Exception) { }
            }
        })
        binding.commentButton.setOnClickListener {
            val letter=binding.commentText.text.toString()
            val user=firebaseAuth.currentUser?.email.toString()
            val comment=Comment(user, letter)

            val key=freeBoardRef.push().key.toString()
            freeBoardRef
                .child(intent.getStringExtra("id").toString())
                .child("comment")
                .child(key)
                .setValue(comment)

            binding.commentText.text.clear()
            hideKeyboard(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun hideKeyboard(activity: Activity){
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view=activity?.currentFocus
        if(view==null){
            view=View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
class CommentViewHolder(val binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root)
class CommentAdapter(val contents:MutableList<Comment>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CommentViewHolder(CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as CommentViewHolder).binding
        binding.itemComment.text=contents[position].letter
        binding.itemUser.text=contents[position].user
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}