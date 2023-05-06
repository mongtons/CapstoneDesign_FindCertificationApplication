package hallym.capstone.findcertificateapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    var userSize:Int=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.commentButton.isEnabled=false
        binding.commentText.isEnabled=false

        userSize=intent.getIntExtra("userSize",1)
        binding.title.text=intent.getStringExtra("title")
        binding.user.text=intent.getStringExtra("user")
        binding.type.text=intent.getStringExtra("type")
        binding.time.text=intent.getStringExtra("time")
        binding.userCount.text="${userSize+1}/${intent.getIntExtra("userCount",1)}"

        studyBoardRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(board in snapshot.children) {
                        if (intent.getStringExtra("id").toString() == board.key) {
                            val userList = mutableListOf<String>()
                            for (user in board.child("otherUser").children) {
                                userList.add(user.value.toString())
                            }
                            if (firebaseAuth.currentUser?.uid.toString() == intent.getStringExtra("userId") ||
                                userList.contains(firebaseAuth.currentUser?.uid.toString())) {
                                binding.commentButton.isEnabled = true
                                binding.commentText.isEnabled = true
                            }
                            if (firebaseAuth.currentUser?.uid.toString() != intent.getStringExtra("userId") &&
                                !userList.contains(firebaseAuth.currentUser?.uid.toString())) {
                                val builder = AlertDialog.Builder(this@StudyBoardActivity, R.style.AppTheme_AlertDialogTheme)
                                builder.setTitle("해당 스터디 게시글에 참가하시겠습니까?")
                                    .setMessage("")
                                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                                        studyBoardRef.child(intent.getStringExtra("id").toString())
                                            .child("otherUser")
                                            .push()
                                            .setValue(firebaseAuth.currentUser?.uid.toString())
                                        val intent = getIntent()
                                        finish()
                                        overridePendingTransition(0, 0)
                                        startActivity(intent)
                                        overridePendingTransition(0, 0)
                                    })
                                    .setNegativeButton("나가기", DialogInterface.OnClickListener { dialog, which ->
                                        dialog.cancel()
                                        onBackPressed()
                                    })
                                    .setCancelable(false)
                                builder.show()
                            }
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
        menuInflater.inflate(R.menu.board_menu, menu)
        val item1= menu?.findItem(R.id.update)
        val item2= menu?.findItem(R.id.delete)
        val item3= menu?.findItem(R.id.out)
        if(binding.user.text==firebaseAuth.currentUser?.displayName.toString()) {
            return super.onCreateOptionsMenu(menu)
        } else {
            item1?.isVisible = false
            item2?.isVisible = false
            item3?.isVisible = true
            return super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.title) {
            "수정하기" -> {
                finish()
                val intent = Intent(this@StudyBoardActivity, AddStudyActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("id", this.intent.getStringExtra("id"))
                intent.putExtra("type", true)
                startActivity(intent)
                true
            }
            "삭제하기" ->{
                finish()
                studyBoardRef.child(this.intent.getStringExtra("id").toString()).removeValue()
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                true
            }
            "나가기" ->{
                finish()
                studyBoardRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(board in snapshot.children){
                            for(user in board.child("otherUser").children){
                                if(user.value.toString()==firebaseAuth.currentUser?.uid){
                                    user.ref.removeValue()
                                    break
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}