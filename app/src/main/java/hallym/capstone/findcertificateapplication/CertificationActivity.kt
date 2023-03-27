package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityCertificationBinding
import hallym.capstone.findcertificateapplication.databinding.BenefitCompanyItemBinding

class CertificationActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCertificationBinding.inflate(layoutInflater)
    }
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.certificationToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.certificationTitle.text = intent.getStringExtra("Title")
        binding.certificationType.text = intent.getStringExtra("Type")
        binding.certificationCategory.text = intent.getStringExtra("Category")
        binding.certificationSubtitle.text = intent.getStringExtra("Subtitle")

        binding.examdayButton.setOnClickListener{
            val intent = Intent(this, Examday::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
class CompanyViewHolder(val binding: BenefitCompanyItemBinding): RecyclerView.ViewHolder(binding.root)
class CompanyAdapter(val contents:List<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CompanyViewHolder(BenefitCompanyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as CompanyViewHolder).binding
        binding.companyName.text=contents[position]
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}