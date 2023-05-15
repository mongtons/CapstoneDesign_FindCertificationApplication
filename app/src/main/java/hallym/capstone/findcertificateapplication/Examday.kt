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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityExamdayBinding
import hallym.capstone.findcertificateapplication.databinding.ExamdayItemBinding
import hallym.capstone.findcertificateapplication.databinding.FreeCommunityItemBinding
import hallym.capstone.findcertificateapplication.datatype.Examdaydata
import hallym.capstone.findcertificateapplication.datatype.FreeBoard
import hallym.capstone.findcertificateapplication.examdayfragment.PassFragment
import hallym.capstone.findcertificateapplication.examdayfragment.PeriodFragment
import hallym.capstone.findcertificateapplication.examdayfragment.TestFragment
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

//        val examdayList = mutableListOf<Examdaydata>(
//            Examdaydata("응시일정", "123",),
//            Examdaydata("응시일정", "456",)
//        )
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        binding.examdayRecycler.layoutManager = layoutManager
//        binding.examdayRecycler.adapter = ExamdaydataAdapter(examdayList)
//        binding.examdayRecycler.addItemDecoration(ExamdaydataDecoration(this))

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val examdayList= mutableListOf<String>()
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

                        if(ds.child("testDay").hasChildren()){
                            val title=ds.child("title").value.toString()
                            val viewpager=binding.examdayView
                            viewpager.adapter=ExamdayFragmentAdapter(this@Examday, title)
                            val testDay=ds.child("testDay")
                            if(testDay.hasChild("period")){
                                examdayList.add("시험 접수일")
                            }
                            if(testDay.hasChild("test")){
                                examdayList.add("시험일")
                            }
                            if(testDay.hasChild("pass")){
                                examdayList.add("합격자 발표일")
                            }
                            TabLayoutMediator(binding.tab, viewpager){ tab, position ->
                                tab.text=examdayList[position]
                            }.attach()
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
class ExamdayFragmentAdapter(activity: FragmentActivity, title:String): FragmentStateAdapter(activity){
    val fragments: List<Fragment>
    init {
        fragments= listOf(PeriodFragment(title), TestFragment(title), PassFragment(title))
    }
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}