package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.*
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.categoryfragment.AllCategoryAdapter
import hallym.capstone.findcertificateapplication.databinding.FragmentCommunityBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentLoginBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentMyPageBinding
import hallym.capstone.findcertificateapplication.databinding.StudyBoardItemBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.lang.Exception
import java.util.Objects

class MyPageFragment : Fragment() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스
    val comRef:DatabaseReference=FirebaseDatabase.getInstance().getReference()
    lateinit var refpw:DataSnapshot
    lateinit var refem:DataSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMyPageBinding.inflate(inflater, container, false)

        binding.userId.text = mFirebaseAuth.currentUser?.displayName.toString()

        binding.changeId.setOnClickListener {
            changeDiaglog("이메일")
        }

        binding.changePassword.setOnClickListener {
            changeDiaglog("비밀번호")
        }

        binding.changeNickname.setOnClickListener {
            //changeDiaglog("닉네임")
        }

        binding.logout.setOnClickListener {

            Log.d("cclo", mFirebaseAuth.currentUser!!.email.toString()+ "로그아웃 시도")
            mFirebaseAuth.signOut()
            Toast.makeText(context, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cclo", "로그아웃 완료")
            binding.userId.text = "로그인하세요."

            val mActivity = activity as MainActivity
            mActivity.setDataAtFragment(LoginFragment(), "mypage") // MainActivty에서 Login으로 이동하도록 함
        }

        binding.signout.setOnClickListener {
            Log.d("cclo", mFirebaseAuth.currentUser!!.email.toString() + " 회원탈퇴 진행")
            mDatabaseRef.child("UserAccount").child(mFirebaseAuth.currentUser!!.uid).removeValue() //Realtime database에서 해당 항목 제거
            mFirebaseAuth.currentUser?.delete()     // Authentication에서 해당 항목 제거
            Toast.makeText(context, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cclo", "회원탈퇴 완료")
            binding.userId.text = "로그인하세요."
        }

        comRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataMutableList= mutableListOf<FreeBoard>()
                val freeBoard=snapshot.child("Free_Board")
                val studyBoard=snapshot.child("Study_Board")
                for(data in freeBoard.children){
                    if(data.child("user").value.toString()==mFirebaseAuth.currentUser!!.displayName.toString()) {
                        dataMutableList.add(
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
                    if(data.child("user").value.toString()==mFirebaseAuth.currentUser!!.displayName.toString()){
//                        dataMutableList.add()
                    }
                }
                val layoutManager = LinearLayoutManager(context)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.boardRecycler.layoutManager = layoutManager
                binding.boardRecycler.adapter = context?.let {
                    FreeBoardAdapter(dataMutableList, it)
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
            AlertDialog.Builder(it)
                .setTitle("변경할 " + diaType + "을 입력해주세요")
                .setView(editText)
                .setPositiveButton("변경") { _ , _ ->
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
                                                    mDatabaseRef.child("UserAccount").child(mFirebaseAuth.currentUser!!.uid).child("emailId").setValue(editText.text.toString())
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
                                                    mDatabaseRef.child("UserAccount").child(mFirebaseAuth.currentUser!!.uid).child("password").setValue(editText.text.toString())
                                                    Toast.makeText(context, "비밀번호 변경 성공", Toast.LENGTH_LONG).show()

                                                } else{
                                                    // 비밀번호 변경 실패 시
                                                    Toast.makeText(context, "비밀번호 변경 실패", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    }
                                    else if(diaType == "닉네임"){
                                        user?.updateProfile(userProfileChangeRequest {

                                        })

                                    }

                                } else {
                                    // 로그인 실패 시
                                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_LONG).show()
                                }
                            }

                    }
                }
                .setCancelable(false)
                .show()
        }


    }
}
class BoardItemAdapter(val content: MutableList<*>, val context: Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    =StudyBoardViewHolder(StudyBoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as StudyBoardViewHolder).binding
        if(content[position] is FreeBoard){
//            binding.boardTitle.text=content[position].user.toString()
            binding.boardUser.text=content[position].toString()
        }
    }

    override fun getItemCount(): Int {
        return content.size
    }
}
