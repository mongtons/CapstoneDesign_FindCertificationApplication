package hallym.capstone.findcertificateapplication.mainfragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hallym.capstone.findcertificateapplication.FreeCommunityActivity
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.StudyActivity
import hallym.capstone.findcertificateapplication.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding= FragmentCommunityBinding.inflate(inflater, container, false)
        var intent:Intent

        //freeCommunity, studyCommunity 버튼을 클릭했을 때, 해당 Activity로 넘어가는 intent
        binding.freeCommunity.setOnClickListener {
            intent= Intent(activity, FreeCommunityActivity::class.java)
            startActivity(intent)
        }
        binding.studyCommunity.setOnClickListener {
            intent= Intent(activity, StudyActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}