package hallym.capstone.findcertificateapplication.mainfragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.google.android.material.textfield.TextInputEditText
import hallym.capstone.findcertificateapplication.R
import hallym.capstone.findcertificateapplication.databinding.FragmentAiBinding
import hallym.capstone.findcertificateapplication.databinding.MessageItemBinding
import hallym.capstone.findcertificateapplication.datatype.Message
import org.json.JSONObject


class AiFragment : Fragment() {
//    lateinit var responseTV: TextView
    lateinit var queryEdt: TextInputEditText
    lateinit var binding:FragmentAiBinding
    lateinit var messageList:MutableList<Message>

    var url = "https://api.openai.com/v1/completions"
    val apiKey=""

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
        queryEdt.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
//                responseTV.text = "Please wait.."
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
        val layoutManager=LinearLayoutManager(context)
        binding.chatMessage.layoutManager=layoutManager
        binding.chatMessage.adapter=AiAdapter(messageList)

        return binding.root
    }
    private fun getResponse(query: String) {
        // setting text on for question on below line.
        messageList.add(Message(true, query))
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(context)
        // creating a json object on below line.
        val jsonObject: JSONObject = JSONObject()
        // adding params to json object.
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("prompt", query)
        jsonObject.put("temperature", 0)
        jsonObject.put("max_tokens", 100)
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