package no.ntnu.fant_app

object User {
    init { }
    var isLoggedIn: Boolean = false
    var uid: String = ""
    var authToken: String = ""

    fun login(uid: String, authToken: String) {
        isLoggedIn = true
        this.uid = uid
        this.authToken = authToken
    }

    fun logout() {
        isLoggedIn = false
        uid = ""
        authToken = ""
    }
}