package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hallym.capstone.findcertificateapplication.databinding.ActivityCertificationBinding

class CertificationActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCertificationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.certificationToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.certificationTitle.text=intent.getStringExtra("Title")
        binding.certificationType.text=intent.getStringExtra("Type")
        binding.certificationCategory.text=intent.getStringExtra("Category")
        binding.certificationSubtitle.text=intent.getStringExtra("Subtitle")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}