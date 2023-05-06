package hallym.capstone.findcertificateapplication.mainfragment

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.SignIn
import hallym.capstone.findcertificateapplication.databinding.FragmentLoginBinding
import java.lang.Exception

class LoginFragment : Fragment() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
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

        // 닉네임으로 아이디 찾기
        binding.findID.setOnClickListener {
            val editText = EditText(context)

            context?.let {
                val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
                builder.setTitle("현재 닉네임을 입력해주세요")
                    .setView(editText)
                    .setCancelable(true)
                    .setPositiveButton("찾기") { dialog, which ->
                        // 빈칸으로 버튼 클릭 시
                        if(editText.text.isEmpty()) {
                            builder.show()
                            Toast.makeText(context, "닉네임을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 닉네임으로 아이디 찾기
                            var inputString = editText.text.toString()
                            mDatabaseRef.child("UserAccount").addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var findEmail = "null"
                                    for(d in snapshot.children){
                                        if(d.child("displayName").value.toString() == inputString){
                                            findEmail = d.child("emailId").value.toString()
                                            Toast.makeText(context, "아이디는 \"" + findEmail + "\"입니다.", Toast.LENGTH_LONG).show()
                                            break
                                        }
                                    }
                                    if(findEmail == "null"){
                                        Toast.makeText(context, "해당 닉네임을 가진 아이디는 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("cclo", "아이디 찾기 데이터스냅샷 오류")
                                }
                            })

                        }
                    }
                    .show()
            }
        }

        // 이메일로 아이디 찾기
        binding.findPW.setOnClickListener {
            val editText = EditText(context)

            context?.let {
                val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
                builder.setTitle("현재 이메일을 입력해주세요")
                    .setView(editText)
                    .setCancelable(true)
                    .setPositiveButton("찾기") { dialog, which ->
                        // 빈칸으로 버튼 클릭 시
                        if(editText.text.isEmpty()) {
                            builder.show()
                            Toast.makeText(context, "이메일을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 이메일로 비밀번호 찾기
                            var inputString = editText.text.toString()
                            mDatabaseRef.child("UserAccount").addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var findPass = "null"
                                    for(d in snapshot.children){
                                        if(d.child("emailId").value.toString() == inputString){
                                            findPass = d.child("password").value.toString()
                                            Toast.makeText(context, "비밀번호는 \"" + findPass + "\"입니다.", Toast.LENGTH_LONG).show()
                                            break
                                        }
                                    }
                                    if(findPass == "null"){
                                        Toast.makeText(context, "해당 이메일을 가진 아이디는 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("cclo", "비밀번호 찾기 데이터스냅샷 오류")
                                }
                            })

                        }
                    }
                    .show()
            }
        }

        return binding.root
    }

}