package no.ntnu.fant_app.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.synnapps.carouselview.ImageListener
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fant_browse.*
import kotlinx.android.synthetic.main.product.*
import no.ntnu.fant_app.Product
import no.ntnu.fant_app.R
import no.ntnu.fant_app.User
import java.io.InputStream
import java.net.URL

class ProductActivity: AppCompatActivity() {
    private val gson = Gson()
    private val client = AsyncHttpClient()
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product)

        //here we get the data from the clicked product
        val product = gson.fromJson<Product>(intent.getStringExtra("product"), Product::class.java)

        //carousel found at:
        //https://lobothijau.medium.com/create-carousel-easily-in-android-app-with-carouselview-6cbf5ef500a9
        val imageListener: ImageListener = object : ImageListener {
            override fun setImageForPosition(position: Int, imageView: ImageView) {
                // You can use Glide or Picasso here
                imageView.setImageBitmap(getImageOf(product.photos, position))
            }
        }

        //init carousel
        carousel.setPageCount(product.photos.size);
        carousel.setImageListener(imageListener);

        //current_image.setImageBitmap(getImageOf(product.photos, 0))
        title_text.text = product.title
        price_text.text = product.price.toString() + "kr"
        description_text.text = product.description
        //load loginpage for if we get an auth failure
        val intent: Intent = Intent(this, LoginActivity::class.java)

        buy_button.setOnClickListener {
            client.addHeader("Authorization", "Bearer " + User.authToken)
            client.put(
                API_URL + "fant/buy/" + product.id,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        if (statusCode === 200) {
                            //send id of purchased product, so we can remove it from the browse view
                            val intent = Intent()
                            intent.putExtra("id", product.id)
                            setResult(200, intent)
                            finish()
                        }
                        println("Success code: $statusCode")
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        println("Failure code: $statusCode")
                        AlertDialog.Builder(context)
                            .setTitle("Not logged in")
                            .setMessage("Proceed to Login page?")
                            .setPositiveButton(
                                "Yes"
                            ) { dialog, which ->
                                startActivity(intent)
                            }
                            .setNegativeButton("No", null)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show()
                    }
                }
            )
        }
    }
}


private fun getImageOf(photos: MutableList<String>, index: Int): Bitmap? {
    if (Build.VERSION.SDK_INT > 9) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
    //check that we don't do anything stupid
    val imageURL: String = if (photos.isEmpty() || photos.size - 1 < index) {
        //stock photo
        "https://www.kenyons.com/wp-content/uploads/2017/04/default-image-620x600.jpg"
    } else {
        API_URL + "fant/photo/" + photos[index]
    }
    val inputStream: InputStream = URL(imageURL).openConnection().getInputStream()
    return BitmapFactory.decodeStream(inputStream)
}