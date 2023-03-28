package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
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

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if(ds.child("title").value == intent.getStringExtra("Title")){
                        if(ds.child("cost").hasChildren()){
                            val dsCost=ds.child("cost")
                            var costVar="필기: "
                            costVar+=dsCost.child("note").value.toString()+", "
                            costVar+="실기: "
                            costVar+=dsCost.child("practice").value
                            binding.examCost.text=costVar
                        }else{
                            var costVar="응시료: "
                            costVar+=ds.child("cost").value
                            binding.examCost.text=costVar
                        }

                        if(ds.child("benefit").hasChildren()){
                            val publicCompany:String =ds.child("benefit").child("public").value.toString()
                            val publicCompanyList:List<String> = publicCompany.split(", ")

                            val company:String = ds.child("benefit").child("company").value.toString()
                            val companyList:List<String> = company.split(", ")

                            val layoutManager1= LinearLayoutManager(this@CertificationActivity)
                            val layoutManager2=LinearLayoutManager(this@CertificationActivity)
                            layoutManager1.orientation=LinearLayoutManager.VERTICAL
                            layoutManager2.orientation=LinearLayoutManager.VERTICAL

                            binding.publicCompany.layoutManager=layoutManager1
                            binding.publicCompany.adapter=CompanyAdapter(publicCompanyList)

                            binding.company.layoutManager=layoutManager2
                            binding.company.adapter=CompanyAdapter(companyList)
                        }


                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){ }
            }
        })

        binding.examButton.setOnClickListener{
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