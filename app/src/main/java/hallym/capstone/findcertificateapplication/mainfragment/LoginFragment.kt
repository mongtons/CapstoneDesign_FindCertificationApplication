package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.FreeCommunityActivity
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.SignIn
import hallym.capstone.findcertificateapplication.databinding.ActivityLoginBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnSignin.setOnClickListener { //회원가입 화면으로 이동
           val intent = Intent(context, SignIn::class.java)
            startActivity(intent)
        }


        binding.btnLogin.setOnClickListener{ // 로그인 버튼 클릭 시

            var strEmail = binding.etEmail.text.toString()
            var strPwd = binding.etPwd.text.toString()

            if(strEmail.isNullOrEmpty() || strPwd.isNullOrEmpty()){   // 이메일 혹은 비밀번호가 비어있는 경우

                Toast.makeText(context, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

            }else{

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)  // 로그인 시도
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){ //로그인 성공

                            Toast.makeText(context, "로그인 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("cclo", strEmail+ "계정 로그인 완료")

                            val data = arguments?.getString("type") // bundle 데이터 받아오기
                            Log.d("cclo", "bundle : " + data)
                            
                            // main에서 클릭한 것에 따라 community / MyPage로 이동
                            if(data == "community"){
                                fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, CommunityFragment())?.commit()

                            } else if(data == "mypage"){
                                fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, MyPageFragment())?.commit()

                            }

                            /**
                             * 로그인도 finish() 기능을 넣어야 함!!!
                             */

                        }else{
                            Toast.makeText(context, "로그인 실패. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                            Log.d("cclo", strEmail+ "계정 로그인 실패")
                        }
                    }
            }

        }
        return binding.root
    }
}