package hallym.capstone.findcertificateapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
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
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
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

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if(ds.child("title").value == intent.getStringExtra("title")){
                        if (ds.child("period").hasChildren()){
                            val dataChild=ds.child("period")
                            var periodText="필기: "
                            periodText+=dataChild.child("note").value.toString()
                            periodText+=", 실기: "
                            periodText+=dataChild.child("practice").value.toString()
                            binding.period.text=periodText
                        }else{
                            binding.period.append(ds.child("period").value.toString())
                        }
                        if(ds.child("condition").hasChildren()){
                            var conditionText=""
                            if(ds.child("condition").hasChild("1st")) {
                                for ((i, condition) in ds.child("condition").children.withIndex()) {
                                    conditionText += "${i + 1}차 시험: ${condition.value.toString()}\n"
                                }
                            }else{
                                if(ds.child("condition").hasChild("note")){
                                    conditionText+="필기: "
                                    if(ds.child("condition").child("note").hasChildren()) {
                                        conditionText+="\n"
                                        for ((i, condition) in ds.child("condition").child("note").children.withIndex()) {
                                            conditionText += "  ${i + 1}. ${condition.value.toString()}\n"
                                        }
                                    }else{
                                        conditionText+=ds.child("condition").child("note").value.toString()
                                    }
                                    conditionText+="\n실기: "
                                    if(ds.child("condition").child("practice").hasChildren()) {
                                        conditionText+="\n"
                                        for ((i, condition) in ds.child("condition").child("practice").children.withIndex()) {
                                            conditionText += "  ${i + 1}. ${condition.value.toString()}\n"
                                        }
                                    }else{
                                        conditionText+=ds.child("condition").child("practice").value.toString()
                                    }
                                }else {
                                    for ((i, condition) in ds.child("condition").children.withIndex()) {
                                        conditionText += "${i + 1}. ${condition.value.toString()}\n"
                                    }
                                }
                            }
                            binding.condition.text=conditionText
                        }else{
                            binding.condition.text="제한 없음"
                        }
                        if(ds.child("testMethod").hasChildren()){
                            val dataChild=ds.child("testMethod")
                            var testMethodText=""
                            if(dataChild.hasChild("1st")){
                                testMethodText="1차: "
                                testMethodText+=dataChild.child("1st").value.toString()
                                testMethodText += "\n2차: "
                                if(dataChild.child("2st").hasChildren()){
                                    testMethodText+="\n  필기: ${dataChild.child("2st").child("note").value.toString()}"
                                    testMethodText+="\n  실기: ${dataChild.child("2st").child("practice").value.toString()}"
                                }else {
                                    testMethodText += dataChild.child("2st").value.toString()
                                }
                            }else {
                                testMethodText = "필기: "
                                testMethodText += dataChild.child("note").value.toString()
                                testMethodText += "\n\n실기: "
                                testMethodText += dataChild.child("practice").value.toString()
                            }
                            binding.testMethod.text=testMethodText
                        }else{
                            binding.testMethod.append(ds.child("testMethod").value.toString())
                        }
                        var subjectText=""
                        if(ds.child("subject").child("note").hasChildren()){
                            val dataChild=ds.child("subject")

                            subjectText += "필기\n"
                            if (dataChild.child("note").hasChildren()) {
                                var i = 1
                                for (data in dataChild.child("note").children) {
                                    subjectText += (" ${i++}과목: ") + data.value.toString() + "\n"
                                }
                            } else {
                                subjectText += " " + dataChild.child("note").value.toString()
                            }
                            subjectText += "\n실기\n"
                            if (dataChild.child("practice").hasChildren()) {
                                var i = 1
                                for (data in dataChild.child("practice").children) {
                                    subjectText += (" ${i++}과목: ") + data.value.toString() + "\n"
                                }
                            } else {
                                subjectText += " " + dataChild.child("practice").value.toString()
                            }
                        }else{
                            val dataChild=ds.child("subject")
                            if(dataChild.hasChild("1st")){
                                subjectText="1차: "
                                if(dataChild.child("1st").hasChildren()){
                                    subjectText+="\n"
                                    for((i, data) in dataChild.child("1st").children.withIndex()){
                                        subjectText+="  ${i+1}과목: ${data.value.toString()}\n"
                                    }
                                }else {
                                    subjectText += dataChild.child("1st").value.toString()+"\n"
                                }
                                subjectText += "\n2차: "
                                if(dataChild.child("2st").hasChildren()){
                                    subjectText+="\n"
                                    for((i, data) in dataChild.child("2st").children.withIndex()){
                                        subjectText+="  ${i+1}과목: ${data.value.toString()}\n"
                                    }
                                }else {
                                    subjectText += dataChild.child("2st").value.toString()
                                }
                            }else {
                                for ((i, subject) in dataChild.children.withIndex()) {
                                    subjectText += "${i + 1}과목: ${subject.value.toString()}\n"
                                }
                            }
                        }
                        binding.subject.text=subjectText
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_:java.lang.Exception){ }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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