package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import hallym.capstone.findcertificateapplication.calendar.AdapterMonth
import hallym.capstone.findcertificateapplication.databinding.ActivityCalendarBinding

class CalendarActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCalendarBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        binding.exam.setOnClickListener{
//            intent.putExtra("title", this.intent.getStringExtra("Title"))
//            startActivity(intent)
//        }

        val monthListManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val monthListAdapter = AdapterMonth()

        binding.calendarCustom.apply {
            layoutManager=monthListManager
            adapter=monthListAdapter
            scrollToPosition(Int.MAX_VALUE/2)
        }


        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarCustom)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }
}