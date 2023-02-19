package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.graphics.Rect
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentAllBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentAllItemBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class AllFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding= FragmentAllBinding.inflate(inflater, container, false)

        val categoryItem= mutableListOf<Certification>(
            Certification(R.mipmap.ic_launcher, "정보처리기사", "한국산업인력공단"),
            Certification(R.mipmap.ic_launcher_round, "빅데이터분석기사", "한국데이터산업진흥원"),
            Certification(R.mipmap.ic_launcher, "네트워크관리사 2급", "한국정보통신자격협회")
        )
        val layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
        binding.allRecyclerView.layoutManager=layoutManager
        binding.allRecyclerView.adapter= AllCategoryAdapter(categoryItem)
        binding.allRecyclerView.addItemDecoration(AllCategoryDecoration(activity as Context))

        return binding.root
    }

}
class AllCategoryViewHolder(val itemBinding: FragmentAllItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class AllCategoryAdapter(val contents: MutableList<Certification>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AllCategoryViewHolder(FragmentAllItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as AllCategoryViewHolder).itemBinding
        binding.itemImg.setImageResource(contents[position].img)
        binding.itemTitle.text=contents[position].title
        binding.itemFrom.text=contents[position].from
        binding.itemRoot.setOnClickListener {
            Log.d("Kim", "All Category $position item Click")
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}
class AllCategoryDecoration(val context: Context): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(20, 20, 20, 20)
    }
}