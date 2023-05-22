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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.FirebaseStorage
import hallym.capstone.findcertificateapplication.databinding.ActivityCertificationBinding
import hallym.capstone.findcertificateapplication.databinding.BenefitCompanyItemBinding
import hallym.capstone.findcertificateapplication.databinding.BookItemBinding
import hallym.capstone.findcertificateapplication.datatype.Book
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
    var cnt = 0
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
                            var costVar: String
                            if(dsCost.hasChild("1st")){
                                costVar="1차: "
                                costVar+=dsCost.child("1st").value.toString()+", "
                                costVar+="2차: "
                                costVar+=dsCost.child("2st").value.toString()
                            }else{
                                costVar="필기: "
                                costVar+=dsCost.child("note").value.toString()+", "
                                costVar+="실기: "
                                costVar+=dsCost.child("practice").value
                            }
                            binding.examCost.text=costVar
                        }else{
                            var costVar="응시료: "
                            costVar+=ds.child("cost").value
                            binding.examCost.text=costVar
                        }
                        if(ds.child("book").hasChildren()){//교재
                            val nbook = ds.child("book").child("note")
                            val pbook = ds.child("book").child("practice")

                            val bookList= mutableListOf<Book>()
                            bookList.add(
                                Book(
                                    intent.getStringExtra("Title").toString(),
                                    nbook.child("name").value.toString(),
                                    nbook.child("bookprice").value.toString(),
                                    nbook.child("person").value.toString(),
                                    nbook.child("publish").value.toString(),
                                    nbook.child("link").value.toString(),
                                    true
                                )
                            )
                            if(pbook.exists()){
                                bookList.add(
                                    Book(
                                        intent.getStringExtra("Title").toString(),
                                        pbook.child("name").value.toString(),
                                        pbook.child("bookprice").value.toString(),
                                        pbook.child("person").value.toString(),
                                        pbook.child("publish").value.toString(),
                                        pbook.child("link").value.toString(),
                                        false
                                    )
                                )
                            }
                            val layoutManager=LinearLayoutManager(this@CertificationActivity)
                            layoutManager.orientation=LinearLayoutManager.HORIZONTAL
                            binding.bookList.layoutManager=layoutManager
                            binding.bookList.adapter=BookAdapter(bookList, this@CertificationActivity)
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
        mFirebaseAuth.currentUser?.uid?.let {
            favoriteRef.child(it).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (d in snapshot.children){
                        // DB에 이미 해당 자격증 이름 존재한다면 click = true로 함수 실행
                        if(d.child("cerTitle").value.toString() == binding.certificationTitle.text.toString()){
                            clicked = true
                            binding.favoriteCertification.setColorFilter(Color.parseColor("#EB6440"))
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
        }

        mFirebaseAuth.currentUser?.uid?.let {
            favoriteRef.child(it).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (d in snapshot.children){
                        cnt++
                        Log.d("cclo", cnt.toString())

                    }

                    }

                override fun onCancelled(error: DatabaseError) {
                    try {
                        error.toException()
                    }catch (_: Exception){ }
                }
            })
        }

        binding.favoriteCertification.setOnClickListener{

            Log.d("cclo", "clicked : " + clicked)
            // 로그인 확인
            if(mFirebaseAuth.currentUser == null){
                // 로그인되지 않았을 경우 이용 불가
                Toast.makeText(this, "로그인 시 사용 가능합니다.", Toast.LENGTH_SHORT).show()
            }else{
                // DB에 해당 자격증 이름이 없는 경우 ==> 찜하기 실행
                if(clicked == false){
                    // 찜한 자격증 개수 최대 2개로 제한



                    Log.d("cclo", "데이터 스냅샷 이후 cnt : " + cnt.toString())
                    // 즐겨찾기 2개인 경우 ==> 더 추가 불가
                    if(cnt == 2){
                        Toast.makeText(this@CertificationActivity, "최대 즐겨찾기 개수(1)를 초과하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                    // 즐겨찾기 2개 미만인 경우 ==> 더 추가 가능
                    else if(cnt < 2){
                        clicked = true

                        // 찜하기 버튼 색상 변경
                        binding.favoriteCertification.setColorFilter(Color.parseColor("#EB6440"))

                        // key == 자격증 구분 위한 랜덤 key값
                        var key = favoriteRef.push().key.toString()
                        var fUid = mFirebaseAuth.currentUser!!.uid
                        var fTitle = binding.certificationTitle.text.toString()
                        var fType = binding.certificationType.text.toString()
                        var fCat = binding.certificationCategory.text.toString()
                        var fSubT = binding.certificationSubtitle.text.toString()

                        // DB에 저장할 Favorite 객체 생성
                        var favorite = Favorite(fUid, fTitle, fType, fCat, fSubT, key)

                        // DB에 데이터 추가
                        favoriteRef
                            .child(mFirebaseAuth.currentUser!!.uid)
                            .child(key)
                            .setValue(favorite)

                        // 즐겨찾기 추가 완료 시 Toast message 생성
                        Toast.makeText(this@CertificationActivity, binding.certificationTitle.text.toString() + "가 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()

                    }

                }
                else{
                    // 즐겨찾기 해제
                    clicked = false
                    mFirebaseAuth.currentUser?.uid?.let { it1 ->
                        favoriteRef.child(it1).addValueEventListener(object: ValueEventListener{
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
                    }

                    binding.favoriteCertification.setColorFilter(Color.WHITE)
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
class BookViewHolder(val binding: BookItemBinding): RecyclerView.ViewHolder(binding.root)
class BookAdapter(val contents: MutableList<Book>, val context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val storage=FirebaseStorage.getInstance("gs://findcertificationapplication.appspot.com/")
    val storageRef=storage.reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    =BookViewHolder(BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as BookViewHolder).binding
        var path:String="/images/"
        if(contents[position].cert_title=="정보통신산업기사" && contents[position].flag){
            path+="Industrial_Engineer_Information_Communication_note.jpg"
        }else if(contents[position].cert_title=="정보통신산업기사" && !contents[position].flag){
            path+="Industrial_Engineer_Information_Communication_practice.jpg"
        }else if(contents[position].cert_title=="정보통신기사" && contents[position].flag){
            path+="Engineer_Information_Communication_note.jpg"
        }else if(contents[position].cert_title=="CSTS 일반" && contents[position].flag){
            path+="csts.jpg"
        }else if(contents[position].cert_title=="전자캐드기능사" && contents[position].flag){
            path+="Craftsman_Electronic_CAD_note.jpg"
        }else if(contents[position].cert_title=="전자캐드기능사" && !contents[position].flag){
            path+="Craftsman_Electronic_CAD_practice.jpg"
        }else if(contents[position].cert_title=="전자기사" && contents[position].flag){
            path+=" electronic article writing.jpg"
        }else if(contents[position].cert_title=="전자기사" && !contents[position].flag){
            path+="electronic engineer practice.jpg"
        }else if(contents[position].cert_title=="3D프린터개발산업기사" && contents[position].flag) {
            path+="3D printer development industry engineer handwriting practice.jpg"
        }else if(contents[position].cert_title=="리눅스마스터 1급" && contents[position].flag){
            path+=" Linux Master Level 1.jpg"
        }else if(contents[position].cert_title=="리눅스 마스터 2급" && contents[position].flag){
        path+=" Linux Master Level 2.jpg"
        }else if(contents[position].cert_title=="네트워크관리사 1급" && contents[position].flag){
        path+="Network Administrator Level 1 .jpg"
        }else if(contents[position].cert_title=="네트워크관리사 2급" && contents[position].flag){
            path+="Network Administrator Level 1 .jpg"
        }else if(contents[position].cert_title=="빅데이터 분석기사" && contents[position].flag){
            path+=" Big data analysis article writing.jpg"
        }else if(contents[position].cert_title=="빅데이터 분석기사" && !contents[position].flag){
            path+="Big data analysis article  practice.jpg"
        }else if(contents[position].cert_title=="SQLP" && contents[position].flag){
            path+="SQLP.jpg"
        }else if(contents[position].cert_title=="SQLD" && contents[position].flag){
            path+="SQLD.jpg"
        }else if(contents[position].cert_title=="DAP" && contents[position].flag){
            path+="Data Architecture Specialist.jpg"
        }else if(contents[position].cert_title=="DAsP" && contents[position].flag){
            path+="Data Architecture Associate Specialist.jpg"
        }else if(contents[position].cert_title=="코딩능력마스터 2급" && contents[position].flag){
            path+="Coding Ability Master Level 2.jpg"
        }else if(contents[position].cert_title=="파이썬마스터 3급" && contents[position].flag){
            path+="Python Master Level 3.jpg"
        }else if(contents[position].cert_title=="파이썬마스터 1급" && contents[position].flag){
            path+="Python Master Level 1.jpg"
        }else if(contents[position].cert_title=="게임그래픽전문가" && contents[position].flag){
            path+="game graphics expert write.jpg"
        }else if(contents[position].cert_title=="게임그래픽전문가" && !contents[position].flag){
            path+="game graphics expert practice.jpg"
        }else if(contents[position].cert_title=="SW코딩자격 1급" && contents[position].flag){
            path+="sw coding qualification level 1.jpg"
        }else if(contents[position].cert_title=="정보처리기사" && contents[position].flag){
            path+="information processing engineer write.jpg"
        }else if(contents[position].cert_title=="정보처리기사" && !contents[position].flag){
            path+="information processing engineer practice.jpg"
        }else if(contents[position].cert_title=="컴퓨터활용능력 1급" && contents[position].flag){
            path+=" Computer literacy level 1 write.jpg"
        }else if(contents[position].cert_title=="컴퓨터활용능력 1급" && !contents[position].flag){
            path+=" Computer literacy level 1 practice.jpg"
        }else if(contents[position].cert_title=="정보처리산업기사" && contents[position].flag){
            path+="Information processing industry engineer write.jpg"
        }else if(contents[position].cert_title=="정보처리산업기사" && !contents[position].flag){
            path+="Information processing industry engineer practice.jpg"
        }else if(contents[position].cert_title=="전자계산기기사" && contents[position].flag){
            path+="electronic calculator write.jpg"
        }else if(contents[position].cert_title=="ATC캐드마스터" && contents[position].flag){
            path+="ATC.jpg"
        }else if(contents[position].cert_title=="일반기계기사" && contents[position].flag){
            path+="mechanical engineer write.jpg"
        }else if(contents[position].cert_title=="일반기계기사" && !contents[position].flag){
            path+="mechanical engineer practice.jpg"
        }else if(contents[position].cert_title=="정보보안기사" && contents[position].flag){
            path+=" Information Security Industry Engineer write.jpg"
        }else if(contents[position].cert_title=="정보보안기사" && !contents[position].flag){
            path+=" Information Security Industry Engineer practice.jpg"
        }else if(contents[position].cert_title=="정보보안산업기사" && contents[position].flag){
            path+=" Information Security Industry Engineer write.jpg"
        }else if(contents[position].cert_title=="정보보안산업기사" && !contents[position].flag){
            path+=" Information Security Industry Engineer practice.jpg"
        }else if(contents[position].cert_title=="컴퓨터시스템응용기술사" && contents[position].flag){
            path+="Computer system application engineer.jpg"
        }else if(contents[position].cert_title=="통신기기기능사" && contents[position].flag){
            path+="Communication equipment technician write.jpg"
        }else if(contents[position].cert_title=="통신기기기능사" && !contents[position].flag){
            path+="Communication equipment technician practice.jpg"
        }else if(contents[position].cert_title=="무선설비기사" && contents[position].flag){
            path+="wireless facility engineer write.jpg"
        }else if(contents[position].cert_title=="무선설비기사" && !contents[position].flag){
            path+="wireless facility engineer practice.jpg"
        }else if(contents[position].cert_title=="통신선로산업기사" && contents[position].flag){
            path+="Communication line industry engineer write.jpg"
        }else if(contents[position].cert_title=="통신선로산업기사" && !contents[position].flag){
            path+="Communication line industry engineer practice.jpg"
        }else if(contents[position].cert_title=="임베디드기사" && contents[position].flag){
            path+="Embedded article.jpg"
        }else if(contents[position].cert_title=="정보시스템감리사" && contents[position].flag){
            path+="Information system manager.jpg"
        }else if(contents[position].cert_title=="웹디자인기능사" && contents[position].flag){
            path+="web design technician write.jpg"
        }else if(contents[position].cert_title=="웹디자인기능사" && !contents[position].flag){
            path+="web design technician practice.jpg"
        }else if(contents[position].cert_title=="컴퓨터그래픽스운용기능사" && contents[position].flag){
            path+="Computer Graphics Operation Specialist write.jpg"
        }else if(contents[position].cert_title=="컴퓨터그래픽스운용기능사" && !contents[position].flag){
            path+="Computer Graphics Operation Specialist practice.jpg"
        }else if(contents[position].cert_title=="ADsP" && contents[position].flag){
            path+="ADSP.jpg"
        }else if(contents[position].cert_title=="ADP" && contents[position].flag){
            path+="ADP write.jpg"
        }else if(contents[position].cert_title=="ADP" && !contents[position].flag){
            path+="ADP practice.jpg"
        }else if(contents[position].cert_title=="게임기획전문가" && contents[position].flag){
            path+="Game Planning Specialist write.jpg"
        }else if(contents[position].cert_title=="게임기획전문가" && !contents[position].flag){
            path+="Game Planning Specialist practice.jpg"
        }else if(contents[position].cert_title=="게임프로그래밍전문가" && contents[position].flag){
            path+="game programming expert.jpg"
        }else if(contents[position].cert_title=="전자계산기조직응용기사" && contents[position].flag){
            path+="Electronic Calculator Organization Application Article write.jpg"
        }else if(contents[position].cert_title=="전자계산기조직응용기사" && !contents[position].flag){
            path+="Electronic Calculator Organization Application Article practice.jpg"
        }else if(contents[position].cert_title=="멀티미디어콘텐츠제작전문가" && contents[position].flag){
            path+="Multimedia content production specialist.jpg"
        }else if(contents[position].cert_title=="전파전자통신기사" && contents[position].flag){
            path+="radio electronic communication engineer.jpg"
        }





        val pathRef=storageRef.child(path)
        pathRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.certImg)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(binding.certImg)
        }
        binding.bookTitle.text="제목: ${contents[position].title}"
        binding.bookAuthor.text="저자: ${contents[position].author}"
        binding.bookPublish.text="출판사: ${contents[position].publish}"
        binding.bookCost.text="가격: ${contents[position].cost}"
        binding.itemRoot.setOnClickListener {
            val intent=Intent(Intent.ACTION_VIEW, Uri.parse(contents[position].uri))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}


