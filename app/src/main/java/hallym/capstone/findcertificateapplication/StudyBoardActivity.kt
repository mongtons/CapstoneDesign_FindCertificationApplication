package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hallym.capstone.findcertificateapplication.databinding.ActivityStudyBoardBinding

class StudyBoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityStudyBoardBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}