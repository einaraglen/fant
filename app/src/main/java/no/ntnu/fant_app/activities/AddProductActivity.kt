package no.ntnu.fant_app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_product.*
import no.ntnu.fant_app.R

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import androidx.activity.result.contract.ActivityResultContracts
import java.io.InputStream
import java.net.URL
import com.android.volley.RequestQueue
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import java.io.File
import java.io.FileNotFoundException
import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.Header
import no.ntnu.fant_app.User


class AddProductActivity: AppCompatActivity() {
    var isTitleBad: Boolean = false
    var isPriceBad: Boolean = false
    lateinit var file: File
    private val client = AsyncHttpClient()

    private var mRequestQueue: RequestQueue? = null

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            image_view.setImageURI(uri)
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        add_image_button.setOnClickListener { selectImageFromGallery() }

        add_button.setOnClickListener {
            val params = RequestParams()

            params.put("title", title_field.text.toString())
            params.put("description", description_field.text.toString())
            params.put("price", price_field.text.toString())
            //TODO: make files a File array and upload all contents
            //params.put("files", file)

            //formdata headers added
            params.setForceMultipartEntityContentType(true);
            //auth headers added
            client.addHeader("Authorization", "Bearer " + User.authToken)
            val intent: Intent = Intent(this, BrowseActivity::class.java)
            client.post(
                API_URL + "fant/create", params,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        if (statusCode === 200) {
                            //reopen browse view, so that new products can be loaded
                            startActivity(intent)
                        }
                        println("Success code: $statusCode")
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        println("Failure code: $statusCode")
                        println(headers.toString())
                        println(error)
                    }
                }
            )
        }

        title_field.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isTitleBad = badTitle(s.toString())
                disableAdd(isTitleBad, isPriceBad, add_button)
            }
        })

        price_field.addTextChangedListener(object : TextWatcher {
            //ignore
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPriceBad = badPrice(s.toString())
                disableAdd(isTitleBad, isPriceBad, add_button)
            }
        })

    }

    private fun getPickedImage(path: String) : Bitmap? {
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        val inputStream: InputStream = URL(path).openConnection().getInputStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun disableAdd(userBad: Boolean, passwordBad: Boolean, button: Button) {
        button.isEnabled = userBad && passwordBad
    }

    private fun badTitle(title: String): Boolean {
        return !title.isEmpty();
    }

    private fun badPrice(price: String): Boolean {
        if (price.isEmpty()) return false;
        //is price 0 or under
        return price.toInt() > 0
    }
}