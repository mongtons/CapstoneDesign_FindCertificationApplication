package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityMainBinding
import hallym.capstone.findcertificateapplication.mainfragment.*

class MainActivity : AppCompatActivity() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Account")// 실시간 데이터베이스
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, HomeFragment()).commit()

        binding.bottomBar.setOnItemSelectedListener{
            when(it.title){
                "HOME" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, HomeFragment()).commit()
                }
                "SEARCH" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, SearchFragment()).commit()
                }
                "A.I." ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, AiFragment()).commit()
                }
                "COMMUNITY" ->{
                    if(mFirebaseAuth.currentUser != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, CommunityFragment()).commit()
                    }else {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, LoginFragment()).commit()
                    }
                }
                "MY PAGE" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, MyPageFragment()).commit()
                }
            }
            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        return when(item.title){
            "일정" -> {
                intent=Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                true
            }
            "설정" -> {
                true
            }
            "도움말" -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}