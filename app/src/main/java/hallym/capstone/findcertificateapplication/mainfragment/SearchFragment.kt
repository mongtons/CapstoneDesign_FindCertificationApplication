package hallym.capstone.findcertificateapplication.mainfragment

import android.os.Bundle
import android.text.Selection.selectAll
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
}

