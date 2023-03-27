package hallym.capstone.findcertificateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        binding.certificationTitle.text=intent.getStringExtra("Title")
        binding.certificationType.text=intent.getStringExtra("Type")
        binding.certificationCategory.text=intent.getStringExtra("Category")
        binding.certificationSubtitle.text=intent.getStringExtra("Subtitle")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if (ds.child("title").value == intent.getStringExtra("Title")) {
                        var testDay:String=""
                        if(ds.child("testDay").hasChildren()) {
                            val dataChild:DataSnapshot=ds.child("testDay")
                            testDay = "정기 기사 1회 필기 원서 접수 기간\n"
                            testDay += dataChild.child("book")
                                .child("1st")
                                .child("note")
                                .value
                            testDay += "\n정기 기사 1회 필기 시험 기간\n"
                            testDay += dataChild.child("test")
                                .child("1st")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 1회 필기 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("1st")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 1회 실기 원서 접수 기간\n"
                            testDay+=dataChild.child("book")
                                .child("1st")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 1회 실기 시험 기간\n"
                            testDay+=dataChild.child("test")
                                .child("1st")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 1회 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("1st")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 2회 필기 원서 접수 기간\n"
                            testDay+=dataChild.child("book")
                                .child("2nd")
                                .child("note")
                                .value
                            testDay += "\n정기 기사 2회 필기 시험 기간\n"
                            testDay += dataChild.child("test")
                                .child("2nd")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 2회 필기 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("2nd")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 2회 실기 원서 접수 기간\n"
                            testDay+=dataChild.child("book")
                                .child("2nd")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 2회 실기 시험 기간\n"
                            testDay+=dataChild.child("test")
                                .child("2nd")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 2회 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("2nd")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 3회 필기 원서 접수 기간\n"
                            testDay+=dataChild.child("book")
                                .child("3rd")
                                .child("note")
                                .value
                            testDay += "\n정기 기사 3회 필기 시험 기간\n"
                            testDay += dataChild.child("test")
                                .child("3rd")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 3회 필기 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("3rd")
                                .child("note")
                                .value
                            testDay+="\n정기 기사 3회 실기 원서 접수 기간\n"
                            testDay+=dataChild.child("book")
                                .child("3rd")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 3회 실기 시험 기간\n"
                            testDay+=dataChild.child("test")
                                .child("3rd")
                                .child("practice")
                                .value
                            testDay+="\n정기 기사 3회 합격자 발표일\n"
                            testDay+=dataChild.child("passer")
                                .child("3rd")
                                .child("practice")
                                .value
                        }
                        if(ds.child("benefit").hasChildren()){
                            val publicCompany:String =ds.child("benefit").child("public").value.toString()
                            val publicCompanyList:List<String> = publicCompany.split(", ")

                            val company:String = ds.child("benefit").child("company").value.toString()
                            val companyList:List<String> = company.split(", ")

                            val layoutManager1=LinearLayoutManager(this@CertificationActivity)
                            val layoutManager2=LinearLayoutManager(this@CertificationActivity)
                            layoutManager1.orientation=LinearLayoutManager.VERTICAL
                            layoutManager2.orientation=LinearLayoutManager.VERTICAL

                            binding.publicCompany.layoutManager=layoutManager1
                            binding.publicCompany.adapter=CompanyAdapter(publicCompanyList)

                            binding.company.layoutManager=layoutManager2
                            binding.company.adapter=CompanyAdapter(companyList)
                        }
                        binding.testDate.text=testDay
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){

                }
            }
        })
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