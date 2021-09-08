package no.ntnu.fant_app.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.login.*
import no.ntnu.fant_app.R
import no.ntnu.fant_app.User


class LoginActivity : AppCompatActivity() {
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
                isUserIDBad = badUserIDText(s.toString(), text)
                disableLogin(isUserIDBad, isPasswordBad, login_button)
            }
        })

        password_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPasswordBad = badPasswordText(s.toString())
                disableLogin(isUserIDBad, isPasswordBad, login_button)
            }
        })

        login_button.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(Request.Method.GET, API_URL + "auth/login?uid=" + userid_text.text.toString() + "&pwd=" + password_text.text.toString(),
                { response ->
                    //set token of global user object
                    User.login(userid_text.text.toString(), response)
                    //goto browse
                    val intent: Intent = Intent(this, BrowseActivity::class.java)
                    startActivity(intent)
                },
                { error ->
                    println(error)
                    text.text = "Could not login"
                }
            )
            //wait for magic to happen
            queue.add(stringRequest)
        }

        goto_signin_button.setOnClickListener {
            val intent: Intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun disableLogin(userBad: Boolean, passwordBad: Boolean, button: Button) {
        button.isEnabled = userBad && passwordBad
    }

    private fun badUserIDText(userid_text: String, text: TextView): Boolean {
        if (userid_text.contains(" ")) text.setText("Space cannot be use for UserID") else text.setText("")
        return !userid_text.contains(" ") && !userid_text.isEmpty();
    }

    private fun badPasswordText(password_text: String): Boolean {
        return !password_text.contains(" ") && !password_text.isEmpty();
    }
}