package hallym.capstone.findcertificateapplication.datatype

import java.util.Date

data class FreeBoard(
    val id:String,
    val title:String,
    val user:String,
    val date:Long,
    val comment: List<Comment>?
)
