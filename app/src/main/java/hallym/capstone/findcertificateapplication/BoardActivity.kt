package hallym.capstone.findcertificateapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityBoardBinding
import hallym.capstone.findcertificateapplication.databinding.BenefitCompanyItemBinding
import hallym.capstone.findcertificateapplication.databinding.CommentItemBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import kotlinx.coroutines.NonCancellable.children

val database: FirebaseDatabase = FirebaseDatabase.getInstance()
val freeBoardRef: DatabaseReference =database.getReference("Free_Board")
class BoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityBoardBinding.inflate(layoutInflater)
    }
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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
                                    comment.child("id").value.toString(),
                                    comment.child("user").value.toString(),
                                    comment.child("letter").value.toString()
                                )
                                commentList.add(data)
                            }
                            val layoutManager= LinearLayoutManager(this@BoardActivity)
                            layoutManager.orientation= LinearLayoutManager.VERTICAL

                            binding.comment.layoutManager=layoutManager
                            binding.comment.adapter=CommentAdapter(
                                commentList, this@BoardActivity,
                                intent.getStringExtra("user").toString(),
                                intent.getStringExtra("id").toString()
                            )
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
            val letter = binding.commentText.text.toString()
            val user = firebaseAuth.currentUser?.displayName.toString()
            val key = freeBoardRef.push().key.toString()
            val comment = Comment(key, user, letter)
            freeBoardRef
                .child(intent.getStringExtra("id").toString())
                .child("comment")
                .child(key)
                .setValue(comment)
            binding.commentText.text.clear()
            hideKeyboard(this@BoardActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun hideKeyboard(activity: Activity){
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view= activity.currentFocus
        if(view==null){
            view=View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(binding.user.text == firebaseAuth.currentUser?.displayName.toString()) {
            menuInflater.inflate(R.menu.board_menu, menu)
            return super.onCreateOptionsMenu(menu)
        }else
            return false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.title){
            "수정하기" -> {
                finish()
                val intent1=Intent(this@BoardActivity, AddBoardActivity::class.java)
                intent1.putExtra("update", true)
                intent1.putExtra("id", this.intent.getStringExtra("id"))
                startActivity(intent1)
                true
            }
            "삭제하기" -> {
                finish()
                freeBoardRef.child(this.intent.getStringExtra("id").toString()).removeValue()
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
class CommentViewHolder(val binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root)
class CommentAdapter(val contents:MutableList<Comment>,val activity: Activity, val userName:String, val id: String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CommentViewHolder(CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val binding=(holder as CommentViewHolder).binding
        binding.itemComment.text=contents[position].letter
        binding.itemUser.text=contents[position].user

        if((contents[position].user==firebaseAuth.currentUser?.displayName.toString()) ||
                firebaseAuth.currentUser?.displayName.toString()==userName){
            binding.itemRoot.setOnLongClickListener {
                val popup=PopupMenu(activity, it)
                popup.menuInflater.inflate(R.menu.comment_menu, popup.menu)
                popup.setOnMenuItemClickListener {item ->
                    when (item.title) {
                        "삭제" -> {
                            freeBoardRef.child(id).child("comment").child(contents[position].id).removeValue()
                            Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()

                            activity.finish()
                            activity.overridePendingTransition(0,0)
                            val intent=activity.intent
                            activity.startActivity(intent)
                            activity.overridePendingTransition(0,0)
                        }
                    }
                    true
                }
                popup.show()
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}