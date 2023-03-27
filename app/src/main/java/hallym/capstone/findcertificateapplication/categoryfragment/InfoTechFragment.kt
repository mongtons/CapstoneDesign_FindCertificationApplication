package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentInfoTechBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class InfoTechFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentInfoTechBinding.inflate(inflater, container, false)

        val categoryItem= mutableListOf<Certification>(
            Certification("국가자격", "정보처리기사", "한국산업인력공단", 3, "정보기술"),
            Certification("국가자격", "컴퓨터활용능력 1급", "대한상공회의소", 6, "정보기술"),
            Certification("국가자격", "정보처리산업기사", "한국산업인력공단", 7, "정보기술"),
            Certification("국가자격", "전자계산기기사", "한국산업인력공단", 8, "기타")
        )

        val layoutManager=LinearLayoutManager(activity)
        layoutManager.orientation=LinearLayoutManager.VERTICAL
        binding.infoTechRecyclerView.layoutManager=layoutManager
        binding.infoTechRecyclerView.adapter= context?.let { AllCategoryAdapter(categoryItem, it) }
        binding.infoTechRecyclerView.addItemDecoration(AllCategoryDecoration(activity as Context))

        return binding.root
    }
}