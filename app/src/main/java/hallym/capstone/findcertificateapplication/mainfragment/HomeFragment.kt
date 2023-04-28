package hallym.capstone.findcertificateapplication.mainfragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import hallym.capstone.findcertificateapplication.*
import hallym.capstone.findcertificateapplication.categoryfragment.*
import hallym.capstone.findcertificateapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentHomeBinding.inflate(inflater, container, false)

        val tab=binding.tab
        val viewPager=binding.categoryView
        viewPager.adapter= activity?.let { CategoryFragmentAdapter(it) }

        TabLayoutMediator(tab, viewPager){ tab, position ->
            val categoryList= mutableListOf<String>(
                "전체", "정보기술", "정보통신", "통신", "프로그래밍", "데이터베이스", "네트워크", "데이터 분석", "기타"
            )
            tab.text=categoryList[position]
        }.attach()

        return binding.root
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