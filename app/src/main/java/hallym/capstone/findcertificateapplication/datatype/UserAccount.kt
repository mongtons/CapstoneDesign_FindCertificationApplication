package hallym.capstone.findcertificateapplication.datatype

/**
 * 사용자 계정 정보 모델 클래스
 */
class UserAccount {
    var emailId: String? = null
    var password: String? = null
    var idToken: String? = null // Firebase Uid(고유 토큰 정보)
}