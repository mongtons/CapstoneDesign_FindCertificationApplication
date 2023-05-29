package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Intent
import android.net.Uri
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
        //카테고리에 해당하는 자격증의 목록을 보여주기 위해 Fragment 사용
        //옆으로 미는 동작에 화면이 넘어갈 수 있게 설정하기 위해 ViewPager를 이용하고, 해당 adapter에는 자격증 목록 Fragment Adapter를 기입
        val viewPager=binding.categoryView
        viewPager.adapter= activity?.let { CategoryFragmentAdapter(it) }

        //자격증을 카테고리로 나누기 위해 TabLayout을 사용한다.
        //TabLayout의 item을 지정해주기 위해 TabLayoutMediator를 사용하여 문자열 리스트의 item을 Tab의 item으로 지정
        TabLayoutMediator(tab, viewPager){ tab, position ->
            val categoryList= mutableListOf<String>(
                "전체", "정보기술", "정보통신", "통신", "프로그래밍", "데이터베이스", "네트워크", "데이터 분석", "기타"
            )
            tab.text=categoryList[position]
        }.attach()

        var intent: Intent
        //qnet, kca, kcci 등 이미지 버튼을 이용하여 터치하면 해당 링크의 웹 사이트를 보여주는 intent 구현
        binding.qnet.setOnClickListener {
            intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://www.q-net.or.kr/man001.do?imYn=Y&gSite=Q"))
            startActivity(intent)
        }
        binding.kca.setOnClickListener {
            intent= Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cq.or.kr/main.do"))
            startActivity(intent)
        }
        binding.kcci.setOnClickListener {
            intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://license.korcham.net/indexmain.jsp"))
            startActivity(intent)
        }
        binding.kdata.setOnClickListener {
            intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dataq.or.kr/www/main.do"))
            startActivity(intent)
        }
        binding.tta.setOnClickListener {
            intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://edu.tta.or.kr/"))
            startActivity(intent)
        }

        return binding.root
    }
}
//자격증 목록 Fragment들의 Adapter
//Fragment들을 초기화해주는 역할이라고 볼 수 있다. Fragment 데이터를 받아와 출력 전 단계라고 볼 수 있다.
class CategoryFragmentAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
    val fragments:List<Fragment>
    init {
        fragments= listOf(AllFragment(), InfoTechFragment(), InfoComFragment(), ComFragment(), ProgFragment(), DBFragment(), NetworkFragment(),
        DataFragment(), EtcFragment())
    }
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}