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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityStudyBinding
import hallym.capstone.findcertificateapplication.databinding.StudyBoardItemBinding
import hallym.capstone.findcertificateapplication.datatype.Comment
import hallym.capstone.findcertificateapplication.datatype.StudyBoard
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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

        //spinner 목록에 원하는 값(study_board_list.xml)을 넣음
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
                    val userList= mutableListOf<String>()
                    for(user in data.child("otherUser").children){
                        userList.add(user.value.toString())
                    }
                    val board = StudyBoard(
                        data.key!!,
                        data.child("title").value.toString(),
                        data.child("user").value.toString(),
                        data.child("time").value as Long,
                        commentList,
                        data.child("body").value.toString(),
                        Integer.parseInt(data.child("userCount").value.toString()),
                        data.child("type").value as Boolean,
                        userList,
                        data.child("userId").value.toString(),
                        data.child("certification").value.toString(),
                        (data.child("qnumber").value?:0L) as Long
                    )
                    list.add(0, board)
                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                    //spinner에서 item을 고를때 불러지는 리스너
                    //선택하는 item마다 다른 로직 구현 가능
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
                                        val userList= mutableListOf<String>()
                                        for(user in data.child("otherUser").children){
                                            userList.add(user.value.toString())
                                        }
                                        val board = StudyBoard(
                                            data.key!!,
                                            data.child("title").value.toString(),
                                            data.child("user").value.toString(),
                                            data.child("time").value as Long,
                                            commentList,
                                            data.child("body").value.toString(),
                                            Integer.parseInt(data.child("userCount").value.toString()),
                                            data.child("type").value as Boolean,
                                            userList,
                                            data.child("userId").value.toString(),
                                            data.child("certification").value.toString(),
                                            (data.child("qnumber").value?:0L) as Long
                                        )
                                        list.add(0, board)
                                    }
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                                1->{
                                    list.clear()
                                    for(data in snapshot.children) {
                                        if (data.child("type").value as Boolean) {
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
                                            val userList = mutableListOf<String>()
                                            for (user in data.child("otherUser").children) {
                                                userList.add(user.value.toString())
                                            }
                                            val board = StudyBoard(
                                                data.key!!,
                                                data.child("title").value.toString(),
                                                data.child("user").value.toString(),
                                                data.child("time").value as Long,
                                                commentList,
                                                data.child("body").value.toString(),
                                                Integer.parseInt(data.child("userCount").value.toString()),
                                                data.child("type").value as Boolean,
                                                userList,
                                                data.child("userId").value.toString(),
                                                data.child("certification").value.toString(),
                                                (data.child("qnumber").value?:0L) as Long
                                            )
                                            list.add(0, board)
                                        }
                                    }
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                                2->{
                                    list.clear()
                                    for(data in snapshot.children) {
                                        if (!(data.child("type").value as Boolean)){
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
                                                data.child("type").value as Boolean,
                                                null,
                                                data.child("userId").value.toString(),
                                                data.child("certification").value.toString(),
                                                (data.child("qnumber").value?:0L) as Long
                                            )
                                            list.add(0, board)
                                        }
                                    }
                                    binding.studyBoardList?.adapter?.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                } catch (_: Exception) { }
            }
        })

        //메인 버튼을 눌렀을 때 숨겨진 하위 버튼을 움직여서 보여줌
        //addBoard: 메인버튼, fabStudy: 자식 하위 버튼1, fabQuestion: 자식 하위 버튼2
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
        //intent로 알맞은 Activity로 이동 및 게시할 게시글의 형태를 구분하기 위해
        //intent.putExtra("...", ...)를 이용하여 intent에 값을 담아서 같이 보냄
        binding.fabStudy.setOnClickListener {
            val intent=Intent(this, AddStudyActivity::class.java)
            intent.putExtra("update", false)
            intent.putExtra("type", true)
            startActivity(intent)
        }
        binding.fabQuestion.setOnClickListener {
            val intent=Intent(this, AddStudyActivity::class.java)
            intent.putExtra("update", false)
            intent.putExtra("type", false)
            startActivity(intent)
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
        //게시글의 시간을 포맷팅, 포맷한 값으로 게시글에 출력
        val timeFormat= SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        binding.boardTime.text=timeFormat.format(contents[position].time)
        binding.boardType.text= if (contents[position].type){
            "스터딩"
        }else{
            "질문"
        }
        //게시글에 따라 View를 표시(VISIBLE) or 숨김(INVISIBLE)
        if (contents[position].type){
            binding.boardUserCount.visibility=View.VISIBLE
            binding.boardUserCount.text="${(contents[position].otherUser?.size)?.plus(1)} / ${contents[position].userCount}"
        }else{
            binding.boardUserCount.visibility=View.INVISIBLE
        }
        binding.itemRoot.setOnClickListener {
            //게시글: 스터딩 게시글
            if(contents[position].type) {
                studyBoardRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(board in snapshot.children){
                            if (board.child("key").value.toString() == contents[position].key) {
                                var flag1 = false
                                var flag2 = false
                                if (board.child("userId").value.toString() == firebaseAuth.currentUser?.uid)
                                    flag1 = true
                                val userList = mutableListOf<String>()
                                for (user in board.child("otherUser").children) {
                                    userList.add(user.value.toString())
                                }
                                if (userList.contains(firebaseAuth.currentUser?.uid))
                                    flag2 = true
                                //해당 게시글에 게시자이거나 스터디게시판의 참가자이거나 모집인원이 만석이지 않을 때 게시글 열람 가능
                                if (contents[position].userCount != contents[position].otherUser?.size?.plus(1) || flag1 || flag2) {
                                    val intent = Intent(context, StudyBoardActivity::class.java)
                                    intent.putExtra("title", contents[position].title)
                                    intent.putExtra("user", contents[position].user)
                                    intent.putExtra("type", binding.boardType.text)
                                    intent.putExtra("time", binding.boardTime.text)
                                    intent.putExtra("id", contents[position].key)
                                    intent.putExtra("userId", contents[position].userId)
                                    if (contents[position].type) {
                                        intent.putExtra("userCount", contents[position].userCount)
                                        intent.putExtra("userSize", contents[position].otherUser?.size)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "해당 게시글의 인원이 다 찼습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        try {
                            error.toException()
                        } catch (_: Exception) { }
                    }
                })
            }else{
                //게시글: 질문 게시글
                val intent=Intent(context, QuestionBoardActivity::class.java)
                intent.putExtra("title", contents[position].title)
                intent.putExtra("user", contents[position].user)
                intent.putExtra("type", binding.boardType.text)
                intent.putExtra("time", binding.boardTime.text)
                intent.putExtra("id", contents[position].key)
                intent.putExtra("userId", contents[position].userId)
                intent.putExtra("cert_title", contents[position].certification)
                intent.putExtra("qNum", contents[position].qNumber)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}