package hallym.capstone.findcertificateapplication.categoryfragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import hallym.capstone.findcertificateapplication.databinding.FragmentInfoComBinding
import hallym.capstone.findcertificateapplication.datatype.Certification

class InfoComFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentInfoComBinding.inflate(inflater, container, false)

        val categoryItem= mutableListOf<Certification>(
            Certification("국가자격", "정보통신기사", "한국산업인력공단"),
            Certification("국가자격", "정보통신산업기사", "한국산업인력공단")
        )

        val layoutManager= LinearLayoutManager(activity)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        binding.infoComRecyclerView.layoutManager=layoutManager
        binding.infoComRecyclerView.adapter= context?.let { AllCategoryAdapter(categoryItem, it) }
        binding.infoComRecyclerView.addItemDecoration(AllCategoryDecoration(activity as Context))

        return binding.root
    }
}