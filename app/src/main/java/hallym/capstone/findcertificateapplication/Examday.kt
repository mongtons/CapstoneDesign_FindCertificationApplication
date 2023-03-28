package hallym.capstone.findcertificateapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hallym.capstone.findcertificateapplication.databinding.ActivityExamdayBinding
import hallym.capstone.findcertificateapplication.databinding.ExamdayItemBinding
import hallym.capstone.findcertificateapplication.databinding.FreeCommunityItemBinding
import hallym.capstone.findcertificateapplication.datatype.Examdaydata
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.text.SimpleDateFormat
import java.util.*

class Examday : AppCompatActivity() {
    val binding by lazy {
        ActivityExamdayBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val examdayList = mutableListOf<Examdaydata>(
            Examdaydata("응시일정", "123",),
            Examdaydata("응시일정", "456",)
        )
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.examdayRecycler.layoutManager = layoutManager
        binding.examdayRecycler.adapter = ExamdaydataAdapter(examdayList)
        binding.examdayRecycler.addItemDecoration(ExamdaydataDecoration(this))
    }
}
class ExamdaydataViewHolder(val itemBinding: ExamdayItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class ExamdaydataAdapter(val contents:MutableList<Examdaydata>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    =ExamdaydataViewHolder(ExamdayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as ExamdaydataViewHolder).itemBinding
        binding.examdayTitle.text=contents[position].title
        binding.examdayText.text=contents[position].text
    }
    override fun getItemCount(): Int {
        return contents.size
    }
}
class ExamdaydataDecoration(val context: Context): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(10, 10, 10, 10)
    }
}