package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityLoginBinding
import hallym.capstone.findcertificateapplication.mainfragment.CommunityFragment

class Login : AppCompatActivity() {
    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터베이스

    lateinit var binding: ActivityLoginBinding

    override fun onStart() { // 로그인 상태 유지
        super.onStart()
        if(mFirebaseAuth.currentUser != null){
            startActivity(Intent(this, FreeCommunityActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Account")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignin.setOnClickListener { //회원가입 화면으로 이동
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    

        binding.btnLogin.setOnClickListener{ // 로그인 버튼 클릭 시

            var strEmail = binding.etEmail.text.toString()
            var strPwd = binding.etPwd.text.toString()

            if(strEmail.isNullOrEmpty() || strPwd.isNullOrEmpty()){   // 이메일 혹은 비밀번호가 비어있는 경우

                Toast.makeText(this, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

            }else{

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)  // 로그인 시도
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){ //로그인 성공

                            Toast.makeText(this, "로그인 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("cclo", strEmail+ "계정 로그인 완료")

                            val intent = Intent(this, FreeCommunityActivity::class.java)
                            startActivity(intent)
                            Log.d("cclo", "홈 화면으로 이동")
                            finish()

                        }else{

                            Toast.makeText(this, "로그인 실패. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                            Log.d("cclo", strEmail+ "계정 로그인 실패")

                        }
                    }
            }

        }
    }
}