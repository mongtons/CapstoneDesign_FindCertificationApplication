package hallym.capstone.findcertificateapplication.datatype

data class Book(
    val cert_title:String,
    val title:String,
    val cost:String,
    val author: String,
    val publish:String,
    val uri: String,
    val flag:Boolean
)
