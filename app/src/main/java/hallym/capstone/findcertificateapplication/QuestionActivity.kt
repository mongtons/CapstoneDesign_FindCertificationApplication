package hallym.capstone.findcertificateapplication

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.ActivityQuestionBinding
import hallym.capstone.findcertificateapplication.databinding.QuestionItemBinding
import hallym.capstone.findcertificateapplication.datatype.Questiondata

class QuestionActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityQuestionBinding.inflate(layoutInflater)
    }
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Question")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {
                    var OneText = ""
                    var TwoText = ""
                    var ThreeText = ""
                    if(ds.key.toString() == intent.getStringExtra("title")) {
                        for (aaa in ds.children) {
                            if (aaa.key == "One") {
                                binding.no1.text=aaa.child("0").value.toString()
                                OneText += aaa.child("1").value.toString() + "\n"
                                OneText += aaa.child("2").value.toString() + "\n"
                                OneText += aaa.child("3").value.toString() + "\n"
                                OneText += aaa.child("4").value.toString()
                                binding.One.text = OneText
                                //ONE일떄 정답
                                if(aaa.child("Answer").value.toString() == "1"){
                                    binding.answer11.setOnClickListener {
                                        Toast.makeText(binding.answer11.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer11.setOnClickListener {//1번문제의 1번버튼
                                        Toast.makeText(binding.answer11.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "2"){
                                    binding.answer12.setOnClickListener {
                                        Toast.makeText(binding.answer12.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer12.setOnClickListener {
                                        Toast.makeText(binding.answer12.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "3"){
                                    binding.answer13.setOnClickListener {
                                        Toast.makeText(binding.answer13.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer13.setOnClickListener {
                                        Toast.makeText(binding.answer13.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString()=="4"){
                                    binding.answer14.setOnClickListener {
                                        Toast.makeText(binding.answer14.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer14.setOnClickListener {
                                        Toast.makeText(binding.answer14.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else if (aaa.key == "Two") {
                                binding.no2.text= aaa.child("0").value.toString()
                                TwoText += aaa.child("1").value.toString() + "\n"
                                TwoText += aaa.child("2").value.toString() + "\n"
                                TwoText += aaa.child("3").value.toString() + "\n"
                                TwoText += aaa.child("4").value.toString()
                                binding.Two.text = TwoText

                                if(aaa.child("Answer").value.toString() == "1"){
                                    binding.answer21.setOnClickListener {
                                        Toast.makeText(binding.answer21.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer21.setOnClickListener {//1번문제의 1번버튼
                                        Toast.makeText(binding.answer21.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "2"){
                                    binding.answer22.setOnClickListener {
                                        Toast.makeText(binding.answer22.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer22.setOnClickListener {
                                        Toast.makeText(binding.answer22.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "3"){
                                    binding.answer23.setOnClickListener {
                                        Toast.makeText(binding.answer23.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer23.setOnClickListener {
                                        Toast.makeText(binding.answer23.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString()=="4"){
                                    binding.answer24.setOnClickListener {
                                        Toast.makeText(binding.answer24.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer24.setOnClickListener {
                                        Toast.makeText(binding.answer24.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                binding.no3.text=aaa.child("0").value.toString()
                                ThreeText += aaa.child("1").value.toString() + "\n"
                                ThreeText += aaa.child("2").value.toString() + "\n"
                                ThreeText += aaa.child("3").value.toString() + "\n"
                                ThreeText += aaa.child("4").value.toString()
                                binding.Three.text = ThreeText

                                if(aaa.child("Answer").value.toString() == "1"){
                                    binding.answer31.setOnClickListener {
                                        Toast.makeText(binding.answer31.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer31.setOnClickListener {//1번문제의 1번버튼
                                        Toast.makeText(binding.answer31.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "2"){
                                    binding.answer32.setOnClickListener {
                                        Toast.makeText(binding.answer32.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer32.setOnClickListener {
                                        Toast.makeText(binding.answer32.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString() == "3"){
                                    binding.answer33.setOnClickListener {
                                        Toast.makeText(binding.answer33.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    binding.answer33.setOnClickListener {
                                        Toast.makeText(binding.answer33.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                if(aaa.child("Answer").value.toString()=="4"){
                                    binding.answer34.setOnClickListener {
                                        Toast.makeText(binding.answer34.context, "정답입니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }else {
                                    binding.answer34.setOnClickListener {
                                        Toast.makeText(binding.answer34.context, "답이 틀렸습니다 다시 풀어보세요!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }





                override fun onCancelled(error: DatabaseError) {
                    try {
                        error.toException()
                    } catch (_: java.lang.Exception) {
                    }
                }
            })
          }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}