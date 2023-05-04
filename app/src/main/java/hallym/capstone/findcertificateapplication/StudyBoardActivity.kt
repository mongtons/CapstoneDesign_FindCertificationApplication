package hallym.capstone.findcertificateapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityStudyBoardBinding
import hallym.capstone.findcertificateapplication.datatype.Comment

class StudyBoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityStudyBoardBinding.inflate(layoutInflater)
    }
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val studyBoardRef:DatabaseReference=database.getReference("Study_Board")
    val firebaseAuth:FirebaseAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.title.text=intent.getStringExtra("title")
        binding.user.text=intent.getStringExtra("user")
        binding.type.text=intent.getStringExtra("type")
        binding.time.text=intent.getStringExtra("time")
        binding.userCount.text="${intent.getStringExtra("userCount")}"

        if(firebaseAuth.currentUser?.uid.toString()!=intent.getStringExtra("id")){
            val builder=AlertDialog.Builder(this)
            builder.setTitle("해당 스터디 게시글에 참가하시겠습니까?")
                .setMessage("")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->

                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

                })
            builder.show()
        }

        studyBoardRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(board in snapshot.children) {
                    if (intent.getStringExtra("id") ==board.key.toString()){
                        binding.summary.text=board.child("body").value.toString()
                        if(board.child("comment").hasChildren()){
                            val commentList= mutableListOf<Comment>()
                            for (comment in board.child("comment").children){
                                val data = Comment(
                                    comment.child("id").value.toString(),
                                    comment.child("user").value.toString(),
                                    comment.child("letter").value.toString()
                                )
                                commentList.add(data)
                            }
                            val layoutManager=LinearLayoutManager(this@StudyBoardActivity)
                            layoutManager.orientation=LinearLayoutManager.VERTICAL

                            binding.comment.layoutManager=layoutManager
                            binding.comment.adapter=CommentAdapter(
                                commentList, this@StudyBoardActivity,
                                intent.getStringExtra("user").toString(),
                                intent.getStringExtra("id").toString(),
                                false
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
            val key = studyBoardRef.push().key.toString()
            val comment = Comment(key, user, letter)
            studyBoardRef
                .child(intent.getStringExtra("id").toString())
                .child("comment")
                .child(key)
                .setValue(comment)
            binding.commentText.text.clear()
            val hidekeyboard:BoardActivity=BoardActivity()
            hidekeyboard.hideKeyboard(this@StudyBoardActivity)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(binding.user.text==firebaseAuth.currentUser?.displayName.toString()) {
            menuInflater.inflate(R.menu.board_menu, menu)
            return super.onCreateOptionsMenu(menu)
        }else
            return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.title) {
            "수정하기" -> {
                finish()
                val intent = Intent(this@StudyBoardActivity, AddStudyActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("id", this.intent.getStringExtra("id"))
                startActivity(intent)
                true
            }
            "삭제하기" ->{
                finish()
                studyBoardRef.child(this.intent.getStringExtra("id").toString()).removeValue()
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}