package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.text.Selection.selectAll
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.CertificationActivity
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentAllBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentAllItemBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class AllFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val allRef: DatabaseReference =database.getReference("Certification")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentAllBinding.inflate(inflater, container, false)

        val dataMutableList:MutableList<Certification> = mutableListOf()

        allRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val cert=Certification(
                        ds.child("type").value.toString(),
                        ds.child("title").value.toString(),
                        ds.child("from").value.toString(),
                        ds.child("id").value as Long,
                        ds.child("category").value.toString()
                    )
                    dataMutableList.add(cert)

                    val layoutManager= LinearLayoutManager(activity)
                    layoutManager.orientation= LinearLayoutManager.VERTICAL
                    binding.allRecyclerView.layoutManager=layoutManager
                    binding.allRecyclerView.adapter= context?.let {
                        AllCategoryAdapter(dataMutableList, it)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (e:java.lang.Exception){
                    Log.d("kkang", e.toString())
                }
            }
        })

        return binding.root
    }

}
class AllCategoryViewHolder(val itemBinding: FragmentAllItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class AllCategoryAdapter(val contents: MutableList<Certification>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AllCategoryViewHolder(FragmentAllItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as AllCategoryViewHolder).itemBinding
        binding.itmeType.text=contents[position].type
        binding.itemTitle.text=contents[position].title
        binding.itemFrom.text=contents[position].from
        binding.itemRoot.setOnClickListener {
            val intent= Intent(context, CertificationActivity::class.java)
            intent.putExtra("Title", binding.itemTitle.text)
            intent.putExtra("Type", binding.itmeType.text)
            intent.putExtra("Subtitle", binding.itemFrom.text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}