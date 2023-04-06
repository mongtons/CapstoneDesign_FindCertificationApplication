package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityAddBoardBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.util.*

var id=1
class AddBoardActivity : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val binding by lazy {
        ActivityAddBoardBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getBooleanExtra("update", false)) {
            binding.toolbar.title = "수정하기"
        }
        binding.cancel.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.addBoard.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("Free_Board")

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
                val user = firebaseAuth.currentUser?.email.toString()
                val time = System.currentTimeMillis()
                val comment = null

                ref.child(key)
                    .setValue(FreeBoard(key, title, user, time, comment, body))
                Toast.makeText(this, "게시글을 업로드했습니다.", Toast.LENGTH_SHORT).show()
            }
            onBackPressed()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}