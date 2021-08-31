package no.ntnu.fant_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.login.*

//have to use your own ip (ipconfig IPv4) because: android...
const val API_URL: String = "http://192.168.0.249:8080/api/"

class MainActivity : AppCompatActivity() {
    var isUserIDBad: Boolean = false
    var isPasswordBad: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        userid_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isUserIDBad = badUserIDText(s.toString())
                disableLogin(isUserIDBad, isPasswordBad)
            }
        })

        password_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPasswordBad = badPasswordText(s.toString())
                disableLogin(isUserIDBad, isPasswordBad)
            }
        })

        login_button.setOnClickListener {
            //TODO: make login call, if success go to next activity
            //val response = Connection.getProducts()
            //println(response.status)

            /*val queue = Volley.newRequestQueue(this)

            val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, API_URL + "fant", null,
                { response ->
                    println(response.toString())
                    text.text = "Good"
                },
                { error ->
                    println(error.toString())
                    text.text = "Shit"
                }
            )
            queue.add(jsonObjectRequest)*/

            val intent: Intent = Intent(this, BrowseActivity::class.java).apply {
                //pass message to the new activity
                putExtra(EXTRA_MESSAGE, "test")
            }
            startActivity(intent)
        }
    }

    private fun disableLogin(userBad: Boolean, passwordBad: Boolean) {
        login_button.isEnabled = userBad && passwordBad
    }

    private fun badUserIDText(userid_text: String): Boolean {
        if (userid_text.contains(" ")) text.setText("Space cannot be use for UserID") else text.setText("")
        return !userid_text.contains(" ") && !userid_text.isEmpty();
    }

    private fun badPasswordText(password_text: String): Boolean {
        return !password_text.contains(" ") && !password_text.isEmpty();
    }
}