package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityQuestionBoardBinding
import hallym.capstone.findcertificateapplication.datatype.Comment

class QuestionBoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityQuestionBoardBinding.inflate(layoutInflater)
    }
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val studyBoardRef: DatabaseReference =database.getReference("Study_Board")
    val questionRef: DatabaseReference=database.getReference("Question")
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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

        questionRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val qN=when(intent.getLongExtra("qNum",0L)){
                    1L -> "One"
                    2L -> "Two"
                    3L -> "Three"
                    else -> "One"
                }
                val cert=snapshot.child(intent.getStringExtra("cert_title").toString()).child(qN)
                binding.no1.text=cert.child("0").value.toString()
                var numText= ""
                for(i in 1..4){
                    if(i==4)
                        numText += cert.child(i.toString()).value.toString()
                    else
                        numText += cert.child(i.toString()).value.toString()+"\n"
                }
                binding.One.text=numText
                if(cert.child("Answer").value.toString() == "1"){
                    binding.answer11.setOnClickListener {
                        Toast.makeText(binding.answer11.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    binding.answer11.setOnClickListener {//1번문제의 1번버튼
                        Toast.makeText(binding.answer11.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                    }
                }
                if(cert.child("Answer").value.toString() == "2"){
                    binding.answer12.setOnClickListener {
                        Toast.makeText(binding.answer12.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    binding.answer12.setOnClickListener {
                        Toast.makeText(binding.answer12.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                    }
                }
                if(cert.child("Answer").value.toString() == "3"){
                    binding.answer13.setOnClickListener {
                        Toast.makeText(binding.answer13.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    binding.answer13.setOnClickListener {
                        Toast.makeText(binding.answer13.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                    }
                }
                if(cert.child("Answer").value.toString()=="4"){
                    binding.answer14.setOnClickListener {
                        Toast.makeText(binding.answer14.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    binding.answer14.setOnClickListener {
                        Toast.makeText(binding.answer14.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
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
                            val layoutManager= LinearLayoutManager(this@QuestionBoardActivity)
                            layoutManager.orientation= LinearLayoutManager.VERTICAL

                            binding.comment.layoutManager=layoutManager
                            binding.comment.adapter=CommentAdapter(
                                commentList, this@QuestionBoardActivity,
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
            hidekeyboard.hideKeyboard(this@QuestionBoardActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.title) {
            "수정하기" ->{
                finish()
                val intent = Intent(this@QuestionBoardActivity, AddStudyActivity::class.java)
                intent.putExtra("update", true)
                intent.putExtra("id", this.intent.getStringExtra("id"))
                intent.putExtra("type", false)
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}