package hallym.capstone.findcertificateapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import hallym.capstone.findcertificateapplication.categoryfragment.AllFragment
import hallym.capstone.findcertificateapplication.databinding.ActivityMainBinding
import hallym.capstone.findcertificateapplication.databinding.CategoryItemBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentAllItemBinding
import hallym.capstone.findcertificateapplication.databinding.PopularItemBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import hallym.capstone.findcertificateapplication.datatype.Popular
import hallym.capstone.findcertificateapplication.mainfragment.*

class MainActivity : AppCompatActivity() {
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
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, CommunityFragment()).commit()
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
}