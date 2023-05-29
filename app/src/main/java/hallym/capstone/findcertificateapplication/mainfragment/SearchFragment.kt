package hallym.capstone.findcertificateapplication.mainfragment

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import hallym.capstone.findcertificateapplication.categoryfragment.AllCategoryAdapter
import hallym.capstone.findcertificateapplication.databinding.FragmentSearchBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class SearchFragment : Fragment() {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference =database.getReference("Certification")
    //FirebaseDatabase의 인스턴스를 가져와 해당 래퍼런스의 데이터를 가져온다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchBtn.setOnClickListener {
            val searchText=binding.searchText.text.toString()

            //가져온 래퍼런스(ref)를 실시간으로 데이터 값이 변할 때마다 값을 가져오기 위해
            //addValueEventListener 사용
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val certificationList= mutableListOf<Certification>()
                    //Firebase Realtime database는 JSON 타입이다.
                    //DataSnapshot 타입은 해당하는 래퍼런스의 JSON 형식을 kotlin에서 사용할 수 있게 변환한 객체이다.
                    //JSON 타입와 유사하므로 children을 이용하여 snapshot 객체의 자식 객체로 for문을 사용한다.
                    for(data in snapshot.children){
                        if(data.child("title").value.toString().contains(searchText)) {
                            //for문 변수인 data는 해당 모든 자식 객체의 각각의 값이 하나씩 도출된다.
                            //해당 data 변수의 자식 데이터를 이용하기 위해 data.child("...")를 사용하고
                            //해당하는 자식 데이터가 객체가 아니라 값이면 data.child("...").value를 사용한다.
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
                    //검색된 자격증 목록 데이터를 나열하는 방향을 설정한다.
                    val layoutManager= LinearLayoutManager(activity)
                    layoutManager.orientation= LinearLayoutManager.VERTICAL
                    binding.searchRecycler.layoutManager=layoutManager
                    //RecyclerView Adapter를 이용하여 화면에 데이터를 출력할 수 있게 데이터베이스로부터 읽어온 데이터 리스트를 넣어준다.
                    binding.searchRecycler.adapter=
                        context?.let { it1 -> AllCategoryAdapter(certificationList, it1) }
                    binding.searchRecycler.addItemDecoration(SearchDecoration(activity as Context)) //RecyclerView의 Item을 꾸밀 수 있게 사용하는 함수이다.
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
            binding.searchText.text.clear() //검색하고 나서 EditText에 입력한 검색어를 지운다.
        }

        return binding.root
    }
    //소프트키보드를 내리기위한 메소드
   fun hideKeyboard(context: Context, view: View){
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
        outRect.set(0, 10, 0, 10) //RecyclerView의 item의 상단과 하단에 10pt씩 margin을 준다.
    }
}


