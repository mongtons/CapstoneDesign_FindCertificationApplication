package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.databinding.FragmentInfoComBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class InfoComFragment : Fragment() {
    val database:FirebaseDatabase=FirebaseDatabase.getInstance()
    val infoComRef:DatabaseReference=database.getReference("Certification")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentInfoComBinding.inflate(inflater, container, false)
        var dataMutableList:MutableList<Certification> = mutableListOf()
        infoComRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if (ds.child("category").value == "정보통신") {
                        val cert=Certification(
                            ds.child("type").value.toString(),
                            ds.child("title").value.toString(),
                            ds.child("from").value.toString(),
                            ds.child("id").value as Long,
                            ds.child("category").value.toString())
                        dataMutableList.add(cert)

                        val layoutManager= LinearLayoutManager(activity)
                        layoutManager.orientation= LinearLayoutManager.VERTICAL
                        binding.infoComRecyclerView.layoutManager=layoutManager
                        binding.infoComRecyclerView.adapter= context?.let {
                            AllCategoryAdapter(dataMutableList, it)
                        }
                        binding.infoComRecyclerView.addItemDecoration(AllCategoryDecoration(activity as Context))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){

                }
            }
        })
        return binding.root
    }
}