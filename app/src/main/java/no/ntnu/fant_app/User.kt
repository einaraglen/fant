package no.ntnu.fant_app

import no.ntnu.fant_app.activities.EventManager

object User {
    init { }
    var events = EventManager("login", "logout")
    var isLoggedIn: Boolean = false
    var uid: String = ""
    var authToken: String = ""

    fun login(uid: String, authToken: String) {
        isLoggedIn = true
        this.uid = uid
        this.authToken = authToken
        events.notify("login")
    }

    fun logout() {
        isLoggedIn = false
        uid = ""
        authToken = ""
        events.notify("logout")
    }
}