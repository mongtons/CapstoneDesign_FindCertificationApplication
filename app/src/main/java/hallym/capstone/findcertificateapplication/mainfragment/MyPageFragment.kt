package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.FreeBoardAdapter
import hallym.capstone.findcertificateapplication.FreeCommunityActivity
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.categoryfragment.AllCategoryAdapter
import hallym.capstone.findcertificateapplication.databinding.FragmentCommunityBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentLoginBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentMyPageBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard

class MyPageFragment : Fragment() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스
    val comRef:DatabaseReference=FirebaseDatabase.getInstance().getReference("Free_Board")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMyPageBinding.inflate(inflater, container, false)

        binding.userId.text = mFirebaseAuth.currentUser?.displayName.toString()

        binding.logout.setOnClickListener {
            Log.d("cclo", mFirebaseAuth.currentUser!!.email.toString()+ "로그아웃 시도")
            mFirebaseAuth.signOut()
            Toast.makeText(context, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cclo", "로그아웃 완료")
            binding.userId.text = "로그인하세요."
            fragmentManager?.beginTransaction()?.remove(this)?.commit()
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, LoginFragment())?.commit()
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
                for(data in snapshot.children){
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
                val layoutManager = LinearLayoutManager(context)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.boardRecycler.layoutManager = layoutManager
                binding.boardRecycler.adapter = FreeBoardAdapter(dataMutableList, context!!)
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: java.lang.Exception) { }
            }
        })

        return binding.root
    }
}