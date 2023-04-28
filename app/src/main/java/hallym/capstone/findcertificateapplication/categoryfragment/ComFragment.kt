package hallym.capstone.findcertificateapplication.categoryfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentAllBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentComBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentInfoComBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class ComFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ComRef: DatabaseReference =database.getReference("Certification")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentComBinding.inflate(inflater, container, false)
        var dataMutableList:MutableList<Certification> = mutableListOf()
        ComRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    if (ds.child("category").value == "통신") {
                        val cert=Certification(
                            ds.child("type").value.toString(),
                            ds.child("title").value.toString(),
                            ds.child("from").value.toString(),
                            ds.child("id").value as Long,
                            ds.child("category").value.toString())
                        dataMutableList.add(cert)

                        val layoutManager= LinearLayoutManager(activity)
                        layoutManager.orientation= LinearLayoutManager.VERTICAL
                        binding.comRecyclerView.layoutManager=layoutManager
                        binding.comRecyclerView.adapter= context?.let {
                            AllCategoryAdapter(dataMutableList, it)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){ }
            }
        })
        return binding.root
    }
}