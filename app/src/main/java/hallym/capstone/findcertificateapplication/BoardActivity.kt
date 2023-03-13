package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hallym.capstone.findcertificateapplication.databinding.ActivityBoardBinding

class BoardActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityBoardBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.title.text=intent.getStringExtra("title")
        binding.user.text=intent.getStringExtra("user")
        binding.time.text=intent.getStringExtra("time")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}