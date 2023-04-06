package hallym.capstone.findcertificateapplication.mainfragment

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Selection.selectAll
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.categoryfragment.AllCategoryAdapter
import hallym.capstone.findcertificateapplication.databinding.FragmentSearchBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class SearchFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchBtn.setOnClickListener {
            val searchText=binding.searchText.text.toString()

            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val certificationList= mutableListOf<Certification>()
                    for(data in snapshot.children){
                        if(data.child("title").value.toString().contains(searchText)) {
                            val certification=Certification(
                                data.child("type").value.toString(),
                                data.child("title").value.toString(),
                                data.child("from").value.toString(),
                                data.child("id").value as Long,
                                data.child("category").value.toString()
                            )
                            certificationList.add(certification)
                        }
                    }
                    val layoutManager= LinearLayoutManager(activity)
                    layoutManager.orientation= LinearLayoutManager.VERTICAL
                    binding.searchRecycler.layoutManager=layoutManager
                    binding.searchRecycler.adapter=
                        context?.let { it1 -> AllCategoryAdapter(certificationList, it1) }
                    binding.searchRecycler.addItemDecoration(SearchDecoration(activity as Context))
                }

                override fun onCancelled(error: DatabaseError) {
                    try {
                        error.toException()
                    }catch (e:java.lang.Exception){
                        Log.d("kkang", e.toString())
                    }
                }
            })
            hideKeyboard(requireContext(), this.requireView())
            binding.searchText.text.clear()
        }

        return binding.root
    }
    private fun hideKeyboard(context: Context, view: View){
        val imm:InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
class SearchDecoration(val context: Context): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(0, 10, 0, 10)
    }
}


