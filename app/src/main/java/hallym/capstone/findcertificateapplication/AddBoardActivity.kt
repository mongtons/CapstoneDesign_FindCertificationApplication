package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityAddBoardBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.util.*

var id=1
class AddBoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityAddBoardBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.cancel.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.addBoard.setOnClickListener {
            val database=FirebaseDatabase.getInstance()
            val ref=database.getReference("Free_Board")
            val key=ref.push().key.toString()

            val title=binding.boardTitle.text.toString()
            val user="김김김"
            val body=binding.boardBody.text.toString()
            val time= System.currentTimeMillis()
            val comment=null

            ref.child(key)
                .setValue(FreeBoard(key, title, user, time, comment))

            onBackPressed()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}