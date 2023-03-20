package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.FragmentInfoComBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import java.util.Objects

class InfoComFragment : Fragment() {
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val infoComRef:DatabaseReference=database.getReference("Certification")
    var dataMutableList:MutableList<Certification> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentInfoComBinding.inflate(inflater, container, false)
        infoComRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    Log.d("kkang", ds.value.toString())
                    if (ds != null) {
                        val cert=Certification(
                            ds.child("type").value.toString(),
                            ds.child("title").value.toString(),
                            ds.child("from").value.toString(),
                            ds.child("id").value as Long,
                            ds.child("category").value.toString())
                        dataMutableList.add(cert)
                    }
                }
                Log.d("kkang", "data list => ${dataMutableList.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){

                }
            }
        })

//        val categoryItem= mutableListOf<Certification>(
//            Certification("국가자격", "정보통신기사", "한국산업인력공단", 1, "정보통신"),
//            Certification("국가자격", "정보통신산업기사", "한국산업인력공단", 2, "정보통신")
//        )

        val layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        binding.infoComRecyclerView.layoutManager=layoutManager
        binding.infoComRecyclerView.adapter= context?.let {
            AllCategoryAdapter(dataMutableList, it)
        }
//        binding.infoComRecyclerView.addItemDecoration(AllCategoryDecoration(activity as Context))

        return binding.root
    }
}
