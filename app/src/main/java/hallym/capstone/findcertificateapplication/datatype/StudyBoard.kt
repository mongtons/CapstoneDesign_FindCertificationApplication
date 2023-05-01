package hallym.capstone.findcertificateapplication.datatype

data class StudyBoard(
    val key: String,
    val title: String,
    val user: String,
    val time:Long,
    val comment: List<Comment>?,
    val body: String,
    val userCount: Int,
    val type: Boolean,
    val otherUser: List<String>?
)
