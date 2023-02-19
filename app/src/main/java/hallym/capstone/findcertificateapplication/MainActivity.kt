package hallym.capstone.findcertificateapplication

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import hallym.capstone.findcertificateapplication.categoryfragment.AllFragment
import hallym.capstone.findcertificateapplication.databinding.ActivityMainBinding
import hallym.capstone.findcertificateapplication.databinding.CategoryItemBinding
import hallym.capstone.findcertificateapplication.databinding.FragmentAllItemBinding
import hallym.capstone.findcertificateapplication.databinding.PopularItemBinding
import hallym.capstone.findcertificateapplication.datatype.Certification
import hallym.capstone.findcertificateapplication.datatype.Popular

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categoryTitle= mutableListOf<String>("전체", "네트워크", "데이터베이스", "데이터분석", "보안", "기타")
        val popularList= mutableListOf<Popular>(
            Popular(R.mipmap.ic_launcher, "정보처리기사", "한국산업인력공단"),
            Popular(R.mipmap.ic_launcher_round, "컴퓨터활용능력 1급", "대한상공회의소"),
            Popular(R.mipmap.ic_launcher, "정보보안기사", "한국정보통신전파진흥원")
        )

        var layoutManager=LinearLayoutManager(this)
        layoutManager.orientation=LinearLayoutManager.HORIZONTAL
        binding.categoryRecycler.layoutManager=layoutManager
        binding.categoryRecycler.adapter=CategoryAdapter(categoryTitle)

        val viewPager=binding.viewpager
        viewPager.adapter=CategoryFragmentAdapter(this)

        layoutManager=LinearLayoutManager(this)
        layoutManager.orientation=LinearLayoutManager.HORIZONTAL
        binding.popularRecycler.layoutManager=layoutManager
        binding.popularRecycler.adapter=PopularAdapter(popularList)
        binding.popularRecycler.addItemDecoration(PopularDecoration(this))
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menu1:MenuItem? = menu?.add(0,0,0,"menu1")
        val menu2:MenuItem? = menu?.add(0,1,0,"menu2")
        return super.onCreateOptionsMenu(menu)
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

class PopularViewHolder(val itemBinding: PopularItemBinding):RecyclerView.ViewHolder(itemBinding.root)
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
class PopularDecoration(val context:Context):RecyclerView.ItemDecoration(){
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