package no.ntnu.fant_app.activities

import android.content.Intent
import android.os.Bundle
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
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.signin.password_text
import kotlinx.android.synthetic.main.signin.text
import kotlinx.android.synthetic.main.signin.userid_text
import no.ntnu.fant_app.R
import no.ntnu.fant_app.User

class SignInActivity : AppCompatActivity()  {
    private var isUserIDBad: Boolean = false
    private var isEmailBad: Boolean = false
    private var isPasswordBad: Boolean = false
    private val client = AsyncHttpClient()


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

        /*signin_button.setOnClickListener {
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
        }*/

        signin_button.setOnClickListener {
            val params = RequestParams()

            params.put("uid", userid_text.text.toString())
            params.put("pwd", password_text.text.toString())

            //set our media type in headers so we don't get 415
            client.post(
                API_URL + "auth/create", params,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        if (statusCode === 200) {
                            //send credentials to login page so we can get auth token
                            val intent = Intent()
                            intent.putExtra("uid", userid_text.text.toString())
                            intent.putExtra("pwd", password_text.text.toString())
                            setResult(200, intent)
                            finish()
                        }
                        println("Success code: $statusCode")
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        println("Failure code: $statusCode")
                        println(error)
                        println(responseBody)
                        text.text = "Could not sign in"
                    }
                }
            )
        }
    }

    //https://gist.github.com/ironic-name/f8e8479c76e80d470cacd91001e7b45b
    private fun isEmailValid(email: String): Boolean {
        //return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return true
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