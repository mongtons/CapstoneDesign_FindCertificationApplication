package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.*
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.*
import hallym.capstone.findcertificateapplication.datatype.Favorite
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import hallym.capstone.findcertificateapplication.datatype.StudyBoard
import java.lang.Exception

class MyPageFragment : Fragment() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스
    val comRef:DatabaseReference=FirebaseDatabase.getInstance().getReference()
    var favoriteRef = FirebaseDatabase.getInstance().getReference("Favorite")
    lateinit var refpw:DataSnapshot
    lateinit var refem:DataSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMyPageBinding.inflate(inflater, container, false)


        mDatabaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //로그인한 현재 유저의 닉네임과 이메일 주소를 화면에 TextView에 출력
                binding.userId.text = mFirebaseAuth.currentUser?.displayName.toString()
                binding.userNickname.text = mFirebaseAuth.currentUser?.email.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_: Exception){ }
            }
        })


        binding.changeId.setOnClickListener {
            changeDiaglog("이메일")
        }

        binding.changePassword.setOnClickListener {
            changeDiaglog("비밀번호")
        }

        // 이메일 인증 메일 전송
        binding.EmailVerify.setOnClickListener {
            // 이메일 인증 메일 보내기
            mFirebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        // 인증 메일 전송 완료 시 확인 요청 토스트 메시지
                        Toast.makeText(context, "이메일 인증 메일이 전송되었습니다.\n인증을 진행해주세요.", Toast.LENGTH_SHORT).show()

                        // 인증 메일 전송 후 로그아웃 ( 로그아웃 안하면 isEmailVerified 변경된 값을 아예 못 받아옴)
                        Toast.makeText(context, "다시 로그인하세요.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", mFirebaseAuth.currentUser!!.email.toString()+ "로그아웃 시도")
                        mFirebaseAuth.signOut()
                        Log.d("cclo", "로그아웃 완료")
                        binding.userId.text = "로그인하세요."

                        val mActivity = activity as MainActivity
                        mActivity.setDataAtFragment(LoginFragment(), "mypage") // MainActivty에서 Login으로 이동하도록 함
                    }else{
                        // 인증 메일 전송 실패 시 토스트 메시지
                        Toast.makeText(context, "이메일 인증 메일 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }


        }


        // 로그아웃
        binding.logout.setOnClickListener {

            Log.d("cclo", mFirebaseAuth.currentUser?.email.toString()+ "로그아웃 시도")
            mFirebaseAuth.signOut()
            Toast.makeText(context, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cclo", "로그아웃 완료")
            binding.userId.text = "로그인하세요."

            val mActivity = activity as MainActivity
            mActivity.setDataAtFragment(LoginFragment(), "mypage") // MainActivty에서 Login으로 이동하도록 함
        }

        // 회원탈퇴
        binding.signout.setOnClickListener {
            Log.d("cclo", mFirebaseAuth.currentUser?.email.toString() + " 회원탈퇴 진행")
            mFirebaseAuth.currentUser?.uid?.let { it1 -> mDatabaseRef.child("UserAccount").child(it1).removeValue() } //Realtime database에서 해당 항목 제거
            mFirebaseAuth.currentUser?.delete()     // Authentication에서 해당 항목 제거
            Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cclo", "회원탈퇴 완료")
            binding.userId.text = "로그인하세요."

            val mActivity = activity as MainActivity
            mActivity.setDataAtFragment(LoginFragment(), "mypage") // MainActivty에서 Login으로 이동하도록 함
        }

        //자격증 즐겨찾기 데이터 출력
        mFirebaseAuth.currentUser?.uid?.let {
            favoriteRef.child(it).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var favDataMutableList= mutableListOf<Favorite>()

                    for (d in snapshot.children){
                        // 로그인한 사용자와 동일한 uid인지 확인
                        if(d.child("uid").value.toString() == mFirebaseAuth.currentUser?.uid){
                            Log.d("cclo", "자격증 이름 : " + d.child("cerTitle").value.toString())
                            favDataMutableList.add(
                                Favorite(
                                    d.child("uid").value.toString(),
                                    d.child("cerTitle").value.toString(),
                                    d.child("cerType").value.toString(),
                                    d.child("cerCat").value.toString(),
                                    d.child("cerSubT").value.toString(),
                                    d.child("key").value.toString()
                                )
                            )
                        }
                    }

                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.orientation = LinearLayoutManager.VERTICAL
                    binding.favoriteRecycler.layoutManager = layoutManager
                    binding.favoriteRecycler.adapter = context?.let {
                        FavoriteAdapter(favDataMutableList, context!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    try {
                        error.toException()
                    }catch (_: Exception){ }
                }
            })
        }



        comRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val boardMutableList= mutableListOf<FreeBoard>()
                val studyMutableList= mutableListOf<StudyBoard>()
                val freeBoard=snapshot.child("Free_Board")
                val studyBoard=snapshot.child("Study_Board")
                for(data in freeBoard.children){
                    if(data.child("user").value.toString()==mFirebaseAuth.currentUser?.displayName.toString()) {
                        boardMutableList.add(
                            FreeBoard(
                                data.child("id").value.toString(),
                                data.child("title").value.toString(),
                                data.child("user").value.toString(),
                                data.child("date").value as Long,
                                null,
                                data.child("body").value.toString()
                            )
                        )
                    }
                }
                for(data in studyBoard.children){
                    if(data.child("user").value.toString()==mFirebaseAuth.currentUser?.displayName.toString()){
                        val userList = mutableListOf<String>()
                        if (data.child("type").value as Boolean) {
                            for (user in data.child("otherUser").children) {
                                userList.add(user.value.toString())
                            }
                        }
                        studyMutableList.add(
                            StudyBoard(
                                data.child("key").value.toString(),
                                data.child("title").value.toString(),
                                data.child("user").value.toString(),
                                data.child("time").value as Long,
                                null,
                                data.child("body").value.toString(),
                                Integer.parseInt(data.child("userCount").value.toString()),
                                data.child("type").value as Boolean,
                                if(data.child("type").value as Boolean){ userList }else{ null },
                                data.child("userId").value.toString(),
                                data.child("certification").value.toString(),
                                (data.child("qnumber").value?:0L) as Long
                            )
                        )
                    }
                }
                val layoutManager1 = LinearLayoutManager(context)
                layoutManager1.orientation = LinearLayoutManager.VERTICAL
                val layoutManager2 = LinearLayoutManager(context)
                layoutManager2.orientation = LinearLayoutManager.VERTICAL

                binding.boardRecycler.layoutManager = layoutManager1
                binding.boardRecycler.adapter = context?.let {
                    FreeBoardAdapter(boardMutableList, it)
                }
                binding.studyRecycler.layoutManager=layoutManager2
                binding.studyRecycler.adapter= context?.let {
                    StudyBoardAdapter(studyMutableList, it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: java.lang.Exception) { }
            }
        })

        return binding.root
    }

    fun changeDiaglog(diaType:String) {
        val editText = EditText(context)
        var user = mFirebaseAuth.currentUser

        mDatabaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                refpw = snapshot.child("UserAccount").child(mFirebaseAuth.currentUser!!.uid).child("password")
                refem = snapshot.child("UserAccount").child(mFirebaseAuth.currentUser!!.uid).child("emailId")
                Log.d("cclo", refem.getValue().toString() + ", " + refpw.getValue().toString())


            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_: Exception){ }
            }
        })

        context?.let {
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
            builder.setTitle("변경할 " + diaType + "을 입력해주세요")
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton("변경") { dialog, which ->
                    if(editText.text.isEmpty()) {
                        changeDiaglog(diaType)
                        Toast.makeText(context, diaType + "을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        //로그인 가능 여부 확인
                        mFirebaseAuth.signInWithEmailAndPassword(refem.getValue().toString(), refpw.getValue().toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    //로그인 성공 시
                                    if(diaType == "이메일"){
                                        user?.updateEmail(editText.text.toString())
                                            ?.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    // 이메일 변경 성공 시
                                                    mFirebaseAuth.currentUser?.uid?.let { it1 ->
                                                        mDatabaseRef.child("UserAccount").child(
                                                            it1
                                                        ).child("emailId").setValue(editText.text.toString())
                                                    }
                                                    Toast.makeText(context, "이메일 변경 성공", Toast.LENGTH_LONG).show()

                                                } else{
                                                    // 이메일 변경 실패 시
                                                    Toast.makeText(context, "이메일 변경 실패", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    }
                                    else if(diaType == "비밀번호"){
                                        user?.updatePassword(editText.text.toString())
                                            ?.addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    // 비밀번호 변경 성공 시
                                                    mFirebaseAuth.currentUser?.uid?.let { it1 ->
                                                        mDatabaseRef.child("UserAccount").child(
                                                            it1
                                                        ).child("password").setValue(editText.text.toString())
                                                    }
                                                    Toast.makeText(context, "비밀번호 변경 성공", Toast.LENGTH_LONG).show()

                                                } else{
                                                    // 비밀번호 변경 실패 시
                                                    Toast.makeText(context, "비밀번호 변경 실패", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    }
                                } else {
                                    // 로그인 실패 시
                                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_LONG).show()
                                }
                            }

                    }
                }
                .show()
        }


    }


}
class FavoriteViewHolder(val binding: FragmentAllItemBinding):RecyclerView.ViewHolder(binding.root)
class FavoriteAdapter(val contents: MutableList<Favorite>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = FavoriteViewHolder(FragmentAllItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as FavoriteViewHolder).binding
        //자격증 이름 데이터 출력
        binding.itmeType.text = contents[position].cerType
        binding.itemTitle.text = contents[position].cerTitle
        binding.itemFrom.text = contents[position].cerSubT
        // 뷰에 대한 이벤트 ==> 클릭 시 자격증 화면으로 이동
        binding.itemRoot.setOnClickListener {
            val intent= Intent(context, CertificationActivity::class.java)
            intent.putExtra("Title", binding.itemTitle.text)
            intent.putExtra("Type", binding.itmeType.text)
            intent.putExtra("Subtitle", binding.itemFrom.text)
            intent.putExtra("Category", contents[position].cerCat)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}