package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import hallym.capstone.findcertificateapplication.*
import hallym.capstone.findcertificateapplication.categoryfragment.AllFragment
import hallym.capstone.findcertificateapplication.databinding.CategoryItemBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentHomeBinding
import hallym.capstone.findcertificateapplication.databinding.PopularItemBinding
import hallym.capstone.findcertificateapplication.datatype.Popular

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentHomeBinding.inflate(inflater, container, false)

        val categoryTitle= mutableListOf<String>("전체", "네트워크", "데이터베이스", "데이터분석", "보안", "기타")
        val popularList= mutableListOf<Popular>(
            Popular(R.mipmap.ic_launcher, "정보처리기사", "한국산업인력공단"),
            Popular(R.mipmap.ic_launcher_round, "컴퓨터활용능력 1급", "대한상공회의소"),
            Popular(R.mipmap.ic_launcher, "정보보안기사", "한국정보통신전파진흥원")
        )

        var layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
        binding.categoryRecycler.layoutManager=layoutManager
        binding.categoryRecycler.adapter= CategoryAdapter(categoryTitle)

        val viewPager=binding.categoryView
        viewPager.adapter= activity?.let { CategoryFragmentAdapter(it) }

        layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
        binding.popularRecycler.layoutManager=layoutManager
        binding.popularRecycler.adapter= PopularAdapter(popularList)
        activity?.let { PopularDecoration(it) }
            ?.let { binding.popularRecycler.addItemDecoration(it) }

        return binding.root
    }
}
class CategoryViewHolder(val itemBinding: CategoryItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class CategoryAdapter(val contents: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            =CategoryViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int {
        return contents.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as CategoryViewHolder).itemBinding
        binding.categoryItem.text=contents[position]
        binding.categoryRoot.setOnClickListener {

        }
    }
}

class PopularViewHolder(val itemBinding: PopularItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class PopularAdapter(val contents:MutableList<Popular>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            =PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as PopularViewHolder).itemBinding
        binding.popularImg.setImageResource(contents[position].img)
        binding.popularTitle.text=contents[position].title
        binding.popularText.text=contents[position].text
        binding.popularRoot.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}
class PopularDecoration(val context: Context): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(20, 0, 20, 20)
    }
}
class CategoryFragmentAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
    val fragments:List<Fragment>
    init {
        fragments= listOf(AllFragment())
    }
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}