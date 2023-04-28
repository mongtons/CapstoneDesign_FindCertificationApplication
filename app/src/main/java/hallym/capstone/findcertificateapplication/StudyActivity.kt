package hallym.capstone.findcertificateapplication

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hallym.capstone.findcertificateapplication.databinding.ActivityStudyBinding

class StudyActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityStudyBinding.inflate(layoutInflater)
    }
    var fabOpen=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.addBoard.setOnClickListener {
            if(fabOpen){
                ObjectAnimator.ofFloat(binding.fabStudy, "translationY", 0f).apply {
                    start()
                }
                ObjectAnimator.ofFloat(binding.fabQuestion, "translationY", 0f).apply {
                    start()
                }
            }else{
                ObjectAnimator.ofFloat(binding.fabStudy, "translationY", -250f).apply {
                    start()
                }
                ObjectAnimator.ofFloat(binding.fabQuestion, "translationY", -450f).apply {
                    start()
                }
            }
            fabOpen=!fabOpen
        }
    }
}