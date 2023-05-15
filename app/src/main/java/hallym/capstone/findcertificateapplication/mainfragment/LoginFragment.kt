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
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증 객체
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("loginTest")// 실시간 데이터베이스 loginTest 객체


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        //Login Fragment 바인딩
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        // 회원가입 버튼 클릭 시
        binding.btnSignin.setOnClickListener {
            // 회원가입 액티비티로 이동
            val intent = Intent(context, SignIn::class.java)
            startActivity(intent)
        }


        // 로그인 버튼 클릭 시
        binding.btnLogin.setOnClickListener{

            // 입력한 이메일과 비밀번호 변수에 저장
            var strEmail = binding.etEmail.text.toString()
            var strPwd = binding.etPwd.text.toString()

            Log.d("cclo", mFirebaseAuth.currentUser?.email.toString())

            // 이메일 혹은 비밀번호가 비어있는 경우 ==> 로그인 불가
            if(strEmail.isNullOrEmpty() || strPwd.isNullOrEmpty()){
                // 토스트 메시지 출력
                Toast.makeText(context, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

            }
            else{

                // 로그인 시도
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)
                    .addOnCompleteListener { task ->
                        //로그인 성공
                        if (task.isSuccessful){

                            // 로그 출력
                            Log.d("cclo", strEmail+ "계정 로그인 완료")

                            // bundle 데이터를 읽어와 다음에 이동할 화면 선택
                            // bundle 데이터 받아오기
                            val data = arguments?.getString("type")
                            Log.d("cclo", "bundle : " + data)

                            // main에서 클릭한 것에 따라 community / MyPage로 이동
                            if(data == "community"){
                                // 이메일 인증이 완료된 경우에만 커뮤니티 이용 가능
                                if(mFirebaseAuth.currentUser!!.isEmailVerified == true){
                                    // community를 선택해서 로그인하게 된 경우 ==> 로그인 완료 후 community 화면으로 이동
                                    fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                    fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, CommunityFragment())?.commit()
                                    Toast.makeText(context, "로그인 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                                }else{
                                    // 이메일 인증이 완료되지 않은 경우 mypage로 이동 ==> 이메일 인증 요청
                                    Toast.makeText(context, "이메일 인증 후 이용 가능합니다.\nMYPAGE에서 이메일 인증을 진행해주세요.", Toast.LENGTH_SHORT).show()
                                    fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                    fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, MyPageFragment())?.commit()
                                }

                            } else if(data == "mypage"){
                                // mypage를 선택해서 로그인하게 된 경우 ==> 로그인 완료 후 mypage 화면으로 이동
                                fragmentManager?.beginTransaction()?.remove(this)?.commit()
                                fragmentManager?.beginTransaction()?.replace(R.id.fragment_position, MyPageFragment())?.commit()
                                Toast.makeText(context, "로그인 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                            }

                        }else{
                            // 로그인 실패
                            // 토스트 메시지 및 로그 출력
                            Toast.makeText(context, "로그인 실패. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                            Log.d("cclo", strEmail+ "계정 로그인 실패")
                        }
                    }
            }

        }

        // 아이디 찾기 버튼 클릭 시
        binding.findID.setOnClickListener {
            // editText 생성
            val editText = EditText(context)

            // 다이얼로그 생성
            context?.let {
                // R.style.AlertDialogTheme를 가지는 다이얼로그 빌더 생성
                val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
                // 다이얼로그의 제목 설정
                builder.setTitle("현재 닉네임을 입력해주세요")
                    // 위에서 생성한 editText 포함
                    .setView(editText)
                    // 다이얼로그 바깥 영역 클릭 시 취소되도록 설정
                    .setCancelable(true)
                    // "찾기"라는 이름 가지는 positiveButton 생성
                    .setPositiveButton("찾기") { dialog, which ->
                        // editText를 빈칸으로 하고 찾기 버튼 클릭 시
                        if(editText.text.isEmpty()) {
                            // 다이얼로그는 계속 보여줌
                            builder.show()
                            // 토스트 메시지 출력
                            Toast.makeText(context, "닉네임을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // editText가 빈칸이 아닌 경우 닉네임으로 아이디 찾기 진행
                            var inputString = editText.text.toString()
                            // realtime database loginTest -> UserAccount에서 이메일 찾기
                            mDatabaseRef.child("UserAccount").addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var findEmail = "null"
                                    // UserAccount의 children에 대해 for문으로 검사 시작
                                    for(d in snapshot.children){
                                        // UserAccount의 children의 child인 displayName(닉네임)이 입력한 닉네임과 동일하면 이메일 발견!
                                        if(d.child("displayName").value.toString() == inputString){
                                            // 해당 이메일을 findEmail 변수에 저장
                                            findEmail = d.child("emailId").value.toString()
                                            // 토스트 메시지를 오래 출력하여 닉네임으로 찾은 이메일을 보여줌
                                            Toast.makeText(context, "아이디는 \"" + findEmail + "\"입니다.", Toast.LENGTH_LONG).show()
                                            // 종료
                                            break
                                        }
                                    }
                                    // 찾은 이메일이 없는 경우 ==> 해당 닉네임 가지는 이메일 없음
                                    if(findEmail == "null"){
                                        // 토스트 메시지 출력
                                        Toast.makeText(context, "해당 닉네임을 가진 아이디는 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                // datasnapshot 구문 오류 발생 시 로그 출력
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("cclo", "아이디 찾기 데이터스냅샷 오류")
                                }
                            })

                        }
                    }
                    // 다이얼로그 출력
                    .show()
            }
        }

        // 비밀번호 찾기 버튼 클릭 시
        binding.findPW.setOnClickListener {
            // editText 생성
            val editText = EditText(context)

            // 다이얼로그 생성
            context?.let {
                // R.style.AlertDialogTheme 가지는 다이얼로그 빌더 생성
                val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
                // 다이얼로그 제목 생성
                builder.setTitle("현재 이메일을 입력해주세요")
                    // editText 출력
                    .setView(editText)
                    // 다이얼로그 바깥 영역 클륵 시 취소 가능
                    .setCancelable(true)
                    // "찾기"라는 이름 가지는 positive button 생성
                    .setPositiveButton("찾기") { dialog, which ->
                        // editText가 공백인데 버튼 클릭 시
                        if(editText.text.isEmpty()) {
                            // 다이얼로그는 여전히 출력
                            builder.show()
                            // 토스트 메시지 출력
                            Toast.makeText(context, "이메일을 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // editText가 공백이 없을 경우
                            var inputString = editText.text.toString()
                            // realtime database loginTest -> UserAccount에서 비밀번호 찾기
                            mDatabaseRef.child("UserAccount").addValueEventListener(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var findPass = "null"
                                    // UserAccount의 children에 대해 for문으로 검사 시작
                                    for(d in snapshot.children){
                                        // UserAccount의 children의 child인 emailId(이메일)이 입력한 이메일과 동일하면 비밀번호 발견!
                                        if(d.child("emailId").value.toString() == inputString){
                                            // 해당 이메일을 findPass 변수에 저장
                                            findPass = d.child("password").value.toString()
                                            // 토스트 메시지를 오래 출력
                                            Toast.makeText(context, "비밀번호는 \"" + findPass + "\"입니다.", Toast.LENGTH_LONG).show()
                                            // 종료
                                            break
                                        }
                                    }
                                    // 찾은 비밀번호가 없는 경우
                                    if(findPass == "null"){
                                        // 토스트 메시지 출력
                                        Toast.makeText(context, "해당 이메일을 가진 아이디는 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                // datasnapshot 구문 오류 발생 시 로그 출력
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("cclo", "비밀번호 찾기 데이터스냅샷 오류")
                                }
                            })

                        }
                    }
                    // 다이얼로그 출력
                    .show()
            }
        }

        return binding.root
    }

}