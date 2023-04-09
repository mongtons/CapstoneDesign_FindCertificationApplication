package hallym.capstone.findcertificateapplication.mainfragment

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class chatGPT{
    fun chatRun() {
        //OpenAI API에 접근하기 위한 인증키와, API에 요청할 내용을 정의하기 위한 변수들을 선언
        val openAIKey = "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"
        val prompt = "Hello, how are you?"
        val maxTokens = 50
        val temperature = 0.5
        val model = "text-davinci-002"
        //API 요청시 요청 바디에 포함될 JSON 형식의 문자열을 requestBody 변수에 저장
        //requestBody 변수에 포함되는 내용은 API 호출시 넘겨줄 prompt, max_tokens, temperature, model 등이 포함
        val requestBody = """
        {
            "prompt": "$prompt",
            "max_tokens": $maxTokens,
            "temperature": $temperature,
            "model": "$model"
        }
        """.trimIndent()

        //OkHttp3 라이브러리를 사용하여 HTTP 클라이언트를 생성
        val client = OkHttpClient()

        //HTTP 요청 시 요청 바디에 포함될 내용의 타입을 지정
        val mediaType = "application/json; charset=utf-8".toMediaType()

        //API 요청을 생성합니다.
        val request = Request.Builder()
            //요청 URL
            .url("https://api.openai.com/v1/engines/davinci-codex/completions")
            //Authorization 헤더에 인증키를 포함
            //HTTP 요청 방식은 POST 방식으로 지정
            .addHeader("Authorization", "Bearer $openAIKey")
            //요청 바디에는 앞서 정의한 requestBody 변수를 추가
            .post(requestBody.toRequestBody(mediaType))
            .build()

        //생성된 요청을 클라이언트로 보내고, API로부터의 응답
        //응답으로 받은 내용을 responseBody 변수에 저장하고, 콘솔에 출력
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        println(responseBody)
    }
}
