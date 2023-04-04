package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivitySignInBinding
import hallym.capstone.findcertificateapplication.datatype.UserAccount

class SignIn : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터베이스

    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main2)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("loginTest")

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignin.setOnClickListener(View.OnClickListener {
            // 회원가입 처리 시작
            var strEmail = binding.etEmail.text.toString()
            var strPwd = binding.etPwd.text.toString()

            
            //FirebaseAuth 진행
            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                .addOnCompleteListener { task ->
                    
                    if (task.isSuccessful) {
                        
                        // 사용자 모델 생성
                        var firebaseUser = mFirebaseAuth.currentUser
                        var account = UserAccount()
                        
                        account.idToken = firebaseUser!!.uid
                        account.emailId = firebaseUser!!.email
                        account.password = strPwd

                        //SetValue : database insert (삽입) 행위
                        mDatabaseRef.child("UserAccount").child(firebaseUser.uid).setValue(account)
                        Toast.makeText(this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", "회원가입 완료")

                        intent = Intent(this, Login::class.java) //로그인으로 변경
                        startActivity(intent)
                        Log.d("cclo", "로그인 화면으로 전환")

                    } else if(task.exception?.message.isNullOrEmpty()){ // 입력이 제대로 안됐을 경우

                        Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", "회원가입 실패")

                    } else {

                        Toast.makeText(this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show()
                        intent = Intent(this, Login::class.java) // 로그인으로 변경
                        startActivity(intent)
                        Log.d("cclo", "이미 존재하는 계정 => 로그인 화면으로 전환")

                    }
                }
        })
    }
}