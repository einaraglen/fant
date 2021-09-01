package no.ntnu.fant_app

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.signin.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.signin.password_text
import kotlinx.android.synthetic.main.signin.text
import kotlinx.android.synthetic.main.signin.userid_text


class SignInActivity : AppCompatActivity()  {
    var isUserIDBad: Boolean = false
    var isEmailBad: Boolean = false
    var isPasswordBad: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        userid_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isUserIDBad = badUserIDText(s.toString(), text)
                disableSignIn(isUserIDBad, isEmailBad, isPasswordBad, signin_button)
            }
        })

        email_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isEmailBad = badEmailText(s.toString(), text)
                disableSignIn(isUserIDBad, isEmailBad, isPasswordBad, signin_button)
            }
        })

        password_text.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPasswordBad = badPasswordText(s.toString())
                disableSignIn(isUserIDBad, isEmailBad, isPasswordBad, signin_button)
            }
        })

        signin_button.setOnClickListener {
            val queue = Volley.newRequestQueue(this)

            val jsonObjRequest: StringRequest = object : StringRequest(
                Method.POST, API_URL + "auth/create",
                Response.Listener { response ->
                    println(response)
                    doLogin(userid_text.text.toString(), password_text.text.toString())
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    text.text = "Could not sign in"
                }) {
                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded; charset=UTF-8"
                }

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["uid"] = userid_text.text.toString()
                    params["pwd"] = password_text.text.toString()
                    return params
                }
            }

            queue.add(jsonObjRequest)
        }
    }

    private fun doLogin(uid: String, pwd: String) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, API_URL + "auth/login?uid=" + uid + "&pwd=" + pwd,
            { response ->
                //set token of global user object
                User.login(uid, response)
                //goto browse
                val intent: Intent = Intent(this, BrowseActivity::class.java)
                startActivity(intent)
            },
            { error ->
                println(error)
                text.text = "Could not sign in"
            }
        )
        //wait for magic to happen
        queue.add(stringRequest)
    }

    //https://gist.github.com/ironic-name/f8e8479c76e80d470cacd91001e7b45b
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun disableSignIn(userBad: Boolean, emailBad: Boolean, passwordBad: Boolean, button: Button) {
        button.isEnabled = userBad && emailBad && passwordBad
    }

    private fun badUserIDText(userid_text: String, text: TextView): Boolean {
        if (userid_text.contains(" ")) text.setText("Space cannot be use for UserID") else text.setText("")
        return !userid_text.contains(" ") && !userid_text.isEmpty();
    }

    private fun badEmailText(email_text: String, text: TextView): Boolean {
        if (!isEmailValid(email_text)) text.setText("Email is not valid") else text.setText("")
        return !email_text.contains(" ") && !email_text.isEmpty() && isEmailValid(email_text);
    }

    private fun badPasswordText(password_text: String): Boolean {
        return !password_text.contains(" ") && !password_text.isEmpty();
    }
}