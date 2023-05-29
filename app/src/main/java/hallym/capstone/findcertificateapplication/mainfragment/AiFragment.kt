package hallym.capstone.findcertificateapplication.mainfragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentAiBinding
import hallym.capstone.findcertificateapplication.databinding.MessageItemBinding
import hallym.capstone.findcertificateapplication.datatype.Message
import org.json.JSONObject


class AiFragment : Fragment() {
    lateinit var queryEdt: EditText
    lateinit var binding:FragmentAiBinding
    lateinit var messageList:MutableList<Message>

    var url = "https://api.openai.com/v1/completions" //OpenAI와 통신하기 위한 url
    val apiKey="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" //ChatGPT의 OpenAI를 사용하기 위한 APIkey

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding= FragmentAiBinding.inflate(inflater, container, false)

        // initializing variables on below line.
        queryEdt = binding.idEdtQuery
        messageList = mutableListOf()

        // adding editor action listener for edit text on below line.
        //EditText에 값을 입력하고 나서 처리하는 리스너
        //값을 넣고 '확인' 버튼을 누르면 getResponse() 호출, 넣지 않고 누르면 "Please enter your query.."가 화면에 출력된다.
        queryEdt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // validating text
                if (queryEdt.text.toString().length > 0) {
                    // calling get response to get the response.
                    getResponse(queryEdt.text.toString())
                } else {
                    Toast.makeText(context, "Please enter your query..", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })

        //마찬가지로 EditText에 값을 입력하고 나서 처리하는 리스너이지만, EditTextView 옆 Button을 눌렀을 때 실행되는 리스너
        binding.inputBtn.setOnClickListener {
            val searchText=queryEdt.text.toString()

            if(searchText.length>0){
                getResponse(searchText)
            }else{
                Toast.makeText(context, "Please enter your query..", Toast.LENGTH_SHORT).show()
            }

            val hide: SearchFragment = SearchFragment()
            hide.hideKeyboard(requireContext(), this.requireView()) //소프트키보드를 Button을 누르면 내리게하는 메소드
            queryEdt.text.clear() //EditText의 입력한 값을 지움
        }

        val layoutManager=LinearLayoutManager(context)
        layoutManager.orientation=LinearLayoutManager.VERTICAL
        binding.chatMessage.layoutManager=layoutManager
        binding.chatMessage.adapter=AiAdapter(messageList)

        //채팅형식처럼 새로운 데이터가 발생되면 가장 최근 item, messageList의 가장 마지막 item으로 이동
        binding.chatMessage.scrollToPosition(messageList.size-1)

        return binding.root
    }
    private fun getResponse(query: String) {
        // setting text on for question on below line.
        messageList.add(Message(true, query))
        //RecyclerView에 새로운 데이터(새로운 채팅)가 발생하면 Adpater에게 알려줌
        binding.chatMessage.adapter?.notifyDataSetChanged()
        binding.chatMessage.smoothScrollToPosition(messageList.size-1)
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(context)
        // creating a json object on below line.
        val jsonObject: JSONObject = JSONObject()
        // adding params to json object.
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("prompt", query)
        jsonObject.put("temperature", 0)
        jsonObject.put("max_tokens", 1024)
        jsonObject.put("top_p", 0.9)
        jsonObject.put("frequency_penalty", 0.0)
        jsonObject.put("presence_penalty", 0.0)

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            object : JsonObjectRequest(Method.POST, url, jsonObject,
                Response.Listener { response ->
                    // on below line getting response message and setting it to text view.
                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    messageList.add(Message(false, responseMsg))
                    binding.chatMessage.adapter?.notifyDataSetChanged()
                    binding.chatMessage.smoothScrollToPosition(messageList.size-1)
                },
                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = "Bearer $apiKey"
                    return params
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }
}
class AiViewHolder(val binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root)
class AiAdapter(val contents:MutableList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
        =AiViewHolder(MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as AiViewHolder).binding
        binding.itemMessage.text=contents[position].text
        //사용자와 AI의 문자열 값을 구분하여 item의 위치를 RIGHT, LEFT로 적용
        if(contents[position].id){
            binding.itemRoot.gravity=Gravity.RIGHT
        }else{
            binding.itemRoot.gravity=Gravity.LEFT
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }
}