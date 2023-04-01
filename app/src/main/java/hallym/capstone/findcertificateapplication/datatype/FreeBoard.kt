package hallym.capstone.findcertificateapplication.datatype

import java.util.Date

data class FreeBoard(
    val id:Long,
    val title:String,
    val user:String,
    val time:Date
)
