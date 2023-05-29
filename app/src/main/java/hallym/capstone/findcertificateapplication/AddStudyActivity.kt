package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityAddStudyBinding
import hallym.capstone.findcertificateapplication.datatype.StudyBoard

class AddStudyActivity : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val binding by lazy {
        ActivityAddStudyBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //게시글의 타입을 보고 질문 게시글 or 스터딩 게시글 인지 확인
        if(intent.getBooleanExtra("type", false)){
            //스터딩 게시글
            binding.counting2.visibility=View.GONE
            binding.studySpinner.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.study_people_count,
                android.R.layout.simple_spinner_item
            )
        }else {
            //질문 게시글
            binding.textView.text="자격증 명"
            binding.counting2.visibility=View.VISIBLE
            //자격증 종류
            binding.studySpinner.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.certification_title,
                android.R.layout.simple_spinner_item
            )
            //해당 자격증의 문제 번호
            binding.numberSpinner.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.question_number,
                android.R.layout.simple_spinner_item
            )
        }
        if (intent.getBooleanExtra("update", false)) {
            binding.toolbar.title = "수정하기"
        }
        binding.cancel.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.addBoard.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("Study_Board")

            val title = binding.boardTitle.text.toString()
            val body = binding.boardBody.text.toString()

            if(intent.getBooleanExtra("update", false)){
                if(intent.getBooleanExtra("type", true)){
                    //스터딩은 인원 수+내용, 제목 수정
                    binding.counting2.visibility=View.GONE

                    ref.child(intent.getStringExtra("id").toString())
                        .child("userCount").setValue(Integer.parseInt(binding.studySpinner.selectedItem.toString()))
                }else{
                    //질문은 자격증 종류, 문제 번호+내용, 제목 수정
                    binding.textView.text="자격증 명"
                    binding.counting2.visibility=View.VISIBLE

                    val cert=binding.studySpinner.selectedItem.toString()
                    val qnumber=binding.numberSpinner.selectedItem.toString()
                    ref.child(intent.getStringExtra("id").toString())
                        .child("certification").setValue(cert)
                    ref.child(intent.getStringExtra("id").toString())
                        .child("qnumber").setValue(Integer.parseInt(qnumber))
                }
                ref.child(intent.getStringExtra("id").toString())
                    .child("title").setValue(title)
                ref.child(intent.getStringExtra("id").toString())
                    .child("body").setValue(body)
                Toast.makeText(this, "게시글을 수정했습니다.", Toast.LENGTH_SHORT).show()
            }else {
                val key = ref.push().key.toString()
                val user = firebaseAuth.currentUser?.displayName.toString()
                val time = System.currentTimeMillis()
                val comment = null

                if(intent.getBooleanExtra("type", true)) {
                    val count = binding.studySpinner.selectedItem.toString()
                    ref.child(key)
                        .setValue(StudyBoard(key, title, user, time, comment, body, Integer.parseInt(count),
                            intent.getBooleanExtra("type", true), null, firebaseAuth.currentUser?.uid.toString(),
                            null, null
                        ))
                }else{
                    val certTitle=binding.studySpinner.selectedItem.toString()
                    val certNumber=binding.numberSpinner.selectedItem.toString()
                    ref.child(key)
                        .setValue(StudyBoard(key, title, user, time, comment, body, 0,
                            intent.getBooleanExtra("type", true), null, firebaseAuth.currentUser?.uid.toString(),
                            certTitle, certNumber.toLong()
                        ))
                }
                Toast.makeText(this, "게시글을 업로드했습니다.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}