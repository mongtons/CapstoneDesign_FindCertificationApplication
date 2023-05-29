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
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityCommunityBinding
import hallym.capstone.findcertificateapplication.databinding.FreeCommunityItemBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import java.text.SimpleDateFormat
import java.util.*

class FreeCommunityActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCommunityBinding.inflate(layoutInflater)
    }
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val freeBoardRef: DatabaseReference =database.getReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        freeBoardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val boardList = mutableListOf<FreeBoard>()
                //전체 래퍼런스에서 Free_Board 객체를 사용
                val freeBoardList=snapshot.child("Free_Board")
                for (board in freeBoardList.children) {
                    val commentList = mutableListOf<Comment>()
                    for (comment in board.child("comment").children) {
                        commentList.add(
                            Comment(
                                comment.child("id").value.toString(),
                                comment.child("user").value.toString(),
                                comment.child("letter").value.toString()
                            )
                        )
                    }
                    val data = FreeBoard(
                        board.key!!,
                        board.child("title").value.toString(),
                        board.child("user").value.toString(),
                        board.child("date").value as Long,
                        commentList,
                        board.child("body").value.toString()
                    )
                    boardList.add(0, data)
                }
                val layoutManager = LinearLayoutManager(this@FreeCommunityActivity)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.freeCommunityText.layoutManager = layoutManager
                binding.freeCommunityText.adapter =
                    FreeBoardAdapter(boardList, this@FreeCommunityActivity)
            }
            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: java.lang.Exception) { }
            }
        })
        binding.addBoard.shrink()
        binding.addBoard.setOnClickListener {
            when(binding.addBoard.isExtended){
                true ->{
                    binding.addBoard.shrink()//버튼 축소
                    val intent=Intent(this, AddBoardActivity::class.java)
                    startActivity(intent)
                }
                false -> binding.addBoard.extend() //버튼 확장
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //현재 Activity 뒤로가기(나가기)
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu) //상단 메뉴를 원하는 메뉴(toolbar_menu.xml)로 구성하기 위해 사용
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
        binding.boardTime.text=timeFormat.format(contents[position].date)
        binding.itemRoot.setOnClickListener {
            val intent= Intent(context, BoardActivity::class.java)
            intent.putExtra("title", binding.boardTitle.text)
            intent.putExtra("user", binding.boardUser.text)
            intent.putExtra("time", binding.boardTime.text)
            intent.putExtra("id", contents[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}