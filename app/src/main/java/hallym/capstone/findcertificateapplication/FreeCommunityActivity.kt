package hallym.capstone.findcertificateapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hallym.capstone.findcertificateapplication.databinding.ActivityFreeCommunityBinding
import hallym.capstone.findcertificateapplication.databinding.FreeCommunityItemBinding
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.text.SimpleDateFormat
import java.util.*

class FreeCommunityActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityFreeCommunityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val boardList= mutableListOf<FreeBoard>(
            FreeBoard("제목 1", "가나다", Date(System.currentTimeMillis())),
            FreeBoard("제목 2", "김김김", Date(System.currentTimeMillis()))
        )
        val layoutManager=LinearLayoutManager(this)
        layoutManager.orientation=LinearLayoutManager.VERTICAL
        binding.freeCommunityText.layoutManager=layoutManager
        binding.freeCommunityText.adapter=FreeBoardAdapter(boardList, this)
        binding.freeCommunityText.addItemDecoration(FreeBoardDecoration(this))

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
class FreeBoardViewHolder(val itemBinding: FreeCommunityItemBinding):RecyclerView.ViewHolder(itemBinding.root)
class FreeBoardAdapter(val contents:MutableList<FreeBoard>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    =FreeBoardViewHolder(FreeCommunityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as FreeBoardViewHolder).itemBinding
        binding.boardTitle.text=contents[position].title
        binding.boardUser.text=contents[position].user
        val timeFormat=SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        binding.boardTime.text=timeFormat.format(contents[position].time)
        binding.itemRoot.setOnClickListener {
            val intent= Intent(context, BoardActivity::class.java)
            intent.putExtra("title", binding.boardTitle.text)
            intent.putExtra("user", binding.boardUser.text)
            intent.putExtra("time", binding.boardTime.text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}
class FreeBoardDecoration(val context: Context): RecyclerView.ItemDecoration(){
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