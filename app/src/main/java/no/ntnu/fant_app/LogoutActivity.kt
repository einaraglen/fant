package no.ntnu.fant_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.logout.*

class LogoutActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logout)

        logout_button.setOnClickListener {
            //logout
            User.logout()
            //then go back
            val intent: Intent = Intent(this, BrowseActivity::class.java)
            startActivity(intent)
        }

    }
}