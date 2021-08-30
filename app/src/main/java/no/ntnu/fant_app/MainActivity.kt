package no.ntnu.fant_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var isUserIDBad: Boolean = false
    var isPasswordBad: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            //TODO do login call
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