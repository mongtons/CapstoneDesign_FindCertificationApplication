package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import hallym.capstone.findcertificateapplication.*
import hallym.capstone.findcertificateapplication.categoryfragment.*
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

        val popularList= mutableListOf<Popular>(
            Popular("정보기술", "국가자격", "정보처리기사", "한국산업인력공단"),
            Popular("정보기술", "국가자격", "컴퓨터활용능력 1급", "대한상공회의소"),
            Popular("보안", "국가자격", "정보보안기사", "한국정보통신전파진흥원")
        )

        val tab=binding.tab
        val viewPager=binding.categoryView
        viewPager.adapter= activity?.let { CategoryFragmentAdapter(it) }

        TabLayoutMediator(tab, viewPager){ tab, position ->
            val categoryList= mutableListOf<String>(
                "전체", "정보기술", "정보통신", "통신", "프로그래밍", "데이터베이스", "클라우드", "네트워크", "데이터 분석", "기타"
            )
            tab.text=categoryList[position]
        }.attach()

        val layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.HORIZONTAL
        binding.popularRecycler.layoutManager=layoutManager
        binding.popularRecycler.adapter= activity?.let { PopularAdapter(popularList, it) }
        activity?.let { PopularDecoration(it) }
            ?.let { binding.popularRecycler.addItemDecoration(it) }

        return binding.root
    }
}
class PopularViewHolder(val itemBinding: PopularItemBinding): RecyclerView.ViewHolder(itemBinding.root)
class PopularAdapter(val contents:MutableList<Popular>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            =PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as PopularViewHolder).itemBinding
        binding.popularCategory.text=contents[position].category
        binding.popularType.text=contents[position].type
        binding.popularTitle.text=contents[position].title
        binding.popularText.text=contents[position].text
        binding.popularRoot.setOnClickListener {
            val intent= Intent(context, CertificationActivity::class.java)
            intent.putExtra("Title", binding.popularTitle.text)
            intent.putExtra("Type", binding.popularType.text)
            intent.putExtra("Category", binding.popularCategory.text)
            intent.putExtra("Subtitle", binding.popularText.text)
            context.startActivity(intent)
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
        fragments= listOf(AllFragment(), InfoTechFragment(), InfoComFragment(), ComFragment(), ProgFragment(), DBFragment(), NetworkFragment(),
        DataFragment(), EtcFragment())
    }
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}