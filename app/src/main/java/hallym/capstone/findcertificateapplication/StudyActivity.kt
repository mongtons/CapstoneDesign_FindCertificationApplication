package hallym.capstone.findcertificateapplication

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityStudyBinding
import hallym.capstone.findcertificateapplication.databinding.StudyBoardItemBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import hallym.capstone.findcertificateapplication.datatype.StudyBoard
import java.text.SimpleDateFormat
import java.util.*

class StudyActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityStudyBinding.inflate(layoutInflater)
    }
    var fabOpen=false
    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    val sBRef:DatabaseReference = database.getReference("Study_Board")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.typeSpinner.adapter=ArrayAdapter.createFromResource(this, R.array.study_board_list, android.R.layout.simple_spinner_item)

        sBRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list= mutableListOf<StudyBoard>()
                for (data in snapshot.children){
                    val commentList = mutableListOf<Comment>()
                    for (comment in data.child("comment").children) {
                        commentList.add(
                            Comment(
                                comment.child("id").value.toString(),
                                comment.child("user").value.toString(),
                                comment.child("letter").value.toString()
                            )
                        )
                    }
                    val board = StudyBoard(
                        data.key!!,
                        data.child("title").value.toString(),
                        data.child("user").value.toString(),
                        data.child("time").value as Long,
                        commentList,
                        data.child("body").value.toString(),
                        Integer.parseInt(data.child("userCount").value.toString()),
                        data.child("type").value as Boolean
                    )
                    list.add(0, board)
                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                    binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            when(position){
                                0->{
                                    list.clear()
                                    for(data in snapshot.children) {
                                        val commentList = mutableListOf<Comment>()
                                        for (comment in data.child("comment").children) {
                                            commentList.add(
                                                Comment(
                                                    comment.child("id").value.toString(),
                                                    comment.child("user").value.toString(),
                                                    comment.child("letter").value.toString()
                                                )
                                            )
                                        }
                                        val board = StudyBoard(
                                            data.key!!,
                                            data.child("title").value.toString(),
                                            data.child("user").value.toString(),
                                            data.child("time").value as Long,
                                            commentList,
                                            data.child("body").value.toString(),
                                            Integer.parseInt(data.child("userCount").value.toString()),
                                            data.child("type").value as Boolean
                                        )
                                        list.add(0, board)
                                    }
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                                1->{
                                    list.clear()
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                                2->{
                                    list.clear()
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }
                val layoutManager=LinearLayoutManager(this@StudyActivity)
                layoutManager.orientation=LinearLayoutManager.VERTICAL
                binding.studyBoardList.layoutManager=layoutManager
                binding.studyBoardList.adapter=StudyBoardAdapter(list, this@StudyActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                } catch (_: java.lang.Exception) { }
            }
        })

        binding.addBoard.setOnClickListener {
            if(fabOpen){
                ObjectAnimator.ofFloat(binding.fabStudy, "translationY", 0f).apply {
                    start()
                }
                ObjectAnimator.ofFloat(binding.fabQuestion, "translationY", 0f).apply {
                    start()
                }
            }else{
                ObjectAnimator.ofFloat(binding.fabStudy, "translationY", -250f).apply {
                    start()
                }
                ObjectAnimator.ofFloat(binding.fabQuestion, "translationY", -450f).apply {
                    start()
                }
            }
            fabOpen=!fabOpen
        }
        binding.fabStudy.setOnClickListener {
            val intent=Intent(this, AddStudyActivity::class.java)
            intent.putExtra("update", false)
            intent.putExtra("type", true)
            startActivity(intent)
        }
        binding.fabQuestion.setOnClickListener {

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
class StudyBoardViewHolder(val binding: StudyBoardItemBinding):RecyclerView.ViewHolder(binding.root)
class StudyBoardAdapter(val contents:MutableList<StudyBoard>, val context:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = StudyBoardViewHolder(StudyBoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as StudyBoardViewHolder).binding
        binding.boardTitle.text=contents[position].title
        binding.boardUser.text=contents[position].user
        val timeFormat= SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        binding.boardTime.text=timeFormat.format(contents[position].time)
        binding.boardType.text= if (contents[position].type){
            "스터딩"
        }else{
            "질문"
        }
        binding.itemRoot.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}