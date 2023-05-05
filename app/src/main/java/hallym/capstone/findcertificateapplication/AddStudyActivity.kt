package hallym.capstone.findcertificateapplication

import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityAddStudyBinding
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
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

        if(intent.getBooleanExtra("type", false)){
            binding.studySpinner.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.study_people_count,
                android.R.layout.simple_spinner_item
            )
        }else {
            binding.counting.visibility=View.GONE
            binding.lineView.visibility=View.GONE
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
                            intent.getBooleanExtra("type", true), null, firebaseAuth.currentUser?.uid.toString()))
                }else{
                    ref.child(key)
                        .setValue(StudyBoard(key, title, user, time, comment, body, 0,
                            intent.getBooleanExtra("type", true), null, firebaseAuth.currentUser?.uid.toString()))
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