package hallym.capstone.findcertificateapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityCertificationBinding
import hallym.capstone.findcertificateapplication.databinding.BenefitCompanyItemBinding
import hallym.capstone.findcertificateapplication.databinding.FavoriteItemBinding
import hallym.capstone.findcertificateapplication.datatype.Favorite
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class CertificationActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCertificationBinding.inflate(layoutInflater)
    }
    var clicked = false
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
    var favoriteRef = database.getReference("Favorite")

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.scrollView.scrollTo(0, binding.certificationTitle.top)

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
                        if(ds.child("book").hasChildren()){//교재
                            val nbook = ds.child("book").child("note")
                            val pbook = ds.child("book").child("practice")
//
                            var notebook = "책 이름 : "
                            notebook+= nbook.child("name").value.toString()+"\n"
                            notebook+= "책 가격 : "
                            notebook+= nbook.child("bookprice").value.toString()+"\n"
                            notebook += "책 저자 : "
                            notebook+= nbook.child("person").value.toString()+"\n"
                            notebook += "책 출판사 : "
                            notebook+= nbook.child("publish").value.toString()+"\n"

                            var practicebook = "책 이름 : "
                            practicebook+= pbook.child("name").value.toString()+"\n"
                            practicebook += "책 가격 : "
                            practicebook+= pbook.child("bookprice").value.toString()+"\n"
                            practicebook += "책 저자 : "
                            practicebook+= pbook.child("person").value.toString()+"\n"
                            practicebook += "책 출판사 : "
                            practicebook+= pbook.child("publish").value.toString()+"\n"


                            binding.bookNoteButton.setOnClickListener{
                                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(nbook.child("Link").value.toString()))
                                startActivity(intent)
                            }
                            binding.bookPractice.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(pbook.child("Link").value.toString()))
                                startActivity(intent)
                            }
                            binding.bookNote.text = notebook
                            binding.bookPractice.text = practicebook

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
                            binding.publicCompany.addItemDecoration(CompanyDecoration(this@CertificationActivity))

                            binding.company.layoutManager=layoutManager2
                            binding.company.adapter=CompanyAdapter(companyList)
                            binding.company.addItemDecoration(CompanyDecoration(this@CertificationActivity))
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_: Exception){ }
            }
        })

        //db 검사
        favoriteRef.child(mFirebaseAuth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (d in snapshot.children){
                    // DB에 이미 해당 자격증 이름 존재한다면 click = true로 함수 실행
                    if(d.child("cerTitle").value.toString() == binding.certificationTitle.text.toString()){
                        clicked = true
                        Log.d("cclo", "in for loop : " + clicked.toString())
                        break
                    }else{
                        //DB에 해당 자격증 없는 경우 click = false로 함수 실행
                        Log.d("cclo", d.child("cerTitle").value.toString())
                        Log.d("cclo", "in for loop no cer : " + clicked.toString())
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_: Exception){ }
            }
        })

        binding.favoriteCertification.setOnClickListener{

            Log.d("cclo", "clicked : " + clicked)
            // 로그인 확인
            if(mFirebaseAuth.currentUser == null){
                // 로그인되지 않았을 경우 이용 불가
                Toast.makeText(this, "로그인 시 사용 가능합니다.", Toast.LENGTH_SHORT).show()
            }else{
                // DB에 해당 자격증 이름이 없는 경우 ==> 찜하기 실행
                if(clicked == false){
                    clicked = true

                    // 찜하기 버튼 색상 변경
                    binding.favoriteCertification.setBackgroundColor(Color.parseColor("#EB6440"))


                    // key == 자격증 구분 위한 랜덤 key값
                    var key = favoriteRef.push().key.toString()
                    var fUid = mFirebaseAuth.currentUser!!.uid
                    var fTitle = binding.certificationTitle.text.toString()
                    var fType = binding.certificationType.text.toString()
                    var fCat = binding.certificationCategory.text.toString()
                    var fSubT = binding.certificationSubtitle.text.toString()

                    // DB에 저장할 Favorite 객체 생성
                    var favorite = Favorite(fUid, fTitle, fType, fCat, fSubT)

                    // DB에 데이터 추가
                    favoriteRef
                        .child(mFirebaseAuth.currentUser!!.uid)
                        .child(key)
                        .setValue(favorite)

                    // 즐겨찾기 추가 완료 시 Toast message 생성
                    Toast.makeText(this, binding.certificationTitle.text.toString() + "가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else{
                    clicked = false
                    favoriteRef.child(mFirebaseAuth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            lateinit var gKey : String
                            for (d in snapshot.children){
                                if(d.child("cerTitle").value.toString() == binding.certificationTitle.text.toString()){
                                    d.ref.removeValue()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            try {
                                error.toException()
                            }catch (_: Exception){ }
                        }
                    })

                    //favoriteRef.child(mFirebaseAuth.currentUser!!.uid).child(gKey).removeValue()
                    binding.favoriteCertification.setBackgroundColor(Color.parseColor("#83838D"))
                    Toast.makeText(this, binding.certificationTitle.text.toString() + "가 즐겨찾기에서 해제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.exam.setOnClickListener{
            val intent = Intent(this@CertificationActivity, QuestionActivity::class.java)
            intent.putExtra("title", this.intent.getStringExtra("Title"))
            startActivity(intent)
        }
        binding.examButton.setOnClickListener{
            val intent = Intent(this, Examday::class.java)
            intent.putExtra("title", this.intent.getStringExtra("Title"))
            startActivity(intent)
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
class CompanyDecoration(val context: Context): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(30, 10, 0, 10)
    }
}


