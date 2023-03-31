package hallym.capstone.findcertificateapplication.mainfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentSearchBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import java.util.Locale.filter

class SearchFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
    lateinit var dataMutableList:MutableList<Certification>
    lateinit var adapter:ArrayAdapter<Certification>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentSearchBinding.inflate(inflater, container, false)

        dataMutableList= mutableListOf()
        selectAll()
        Log.d("kkang", dataMutableList.toString())

        var adapter =
            activity?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, dataMutableList) }
        binding.searchRecycler.adapter = adapter

        binding.searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
        android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return binding.root
    }
    private fun selectAll(){
        val list= mutableListOf<Certification>()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val cert=Certification(
                        ds.child("type").value.toString(),
                        ds.child("title").value.toString(),
                        ds.child("from").value.toString(),
                        ds.child("id").value as Long,
                        ds.child("category").value.toString()
                    )
                    list.add(cert)
                }
                dataMutableList=list
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    error.toException()
                }catch (_:java.lang.Exception){ }
            }
        })
        Log.d("kkang", list.toString())
    }
    private fun setupListView(binding: FragmentSearchBinding){
        adapter =
            activity?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, dataMutableList) }!!
        binding.searchRecycler.adapter = adapter
    }
    private fun setupSearchView(binding: FragmentSearchBinding) {
        binding.searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                for(data in dataMutableList){
                    if(data.title == p0) {
                        val dataList= mutableListOf<Certification>()
                        dataList.add(data)
                        val isMatchFound = dataList
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })
    }

}

