package no.ntnu.fant_app

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fant_browse.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.InetAddress

//have to use your own ip (ipconfig IPv4) because: android...
//const val API_URL: String = "http://192.168.0.249:8080/api/"
//or this works
const val API_URL: String = "http://10.0.2.2:8080/api/"

class BrowseActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fant_browse)

        productAdapter = ProductAdapter(mutableListOf())

        product_view.adapter = productAdapter
        product_view.layoutManager = LinearLayoutManager(this)

        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, API_URL + "fant", null,
            { response ->
                addProductsToView(response, productAdapter)
            },
            { error ->
                println(error.toString())
            }
        )
        queue.add(jsonArrayRequest)

        //val address = InetAddress.getLocalHost().hostAddress

        checkUserStatus(goto_login_button)

        goto_login_button.setOnClickListener {
            //if logged in = logout-page, else = login-page
            val intent: Intent = if (User.isLoggedIn) Intent(this, LogoutActivity::class.java) else Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

private fun checkUserStatus(button: Button) {
    println(User.isLoggedIn)
    //check if user is logged in, and set name in corner-button
    button.text = if(User.isLoggedIn) User.uid else "Login"
}

private fun addProductsToView(jsonArray: JSONArray, adapter: ProductAdapter) {
    //early return if empty
    if (jsonArray.length() == 0) return
    //back to bizz
    for (i in 0 until jsonArray.length()) {
        val current = jsonArray.getJSONObject(i)
        val product = Product(
            current["title"].toString(),
            current["description"].toString(),
            current["price"].toString().toInt(),
            getSellerName(current["seller"]),
            getPhotos(current["photos"])
            )
        adapter.addProduct(product)
    }
}

private fun getPhotos(jsonPhotos: Any): MutableList<String> {
    val photos: MutableList<String> = mutableListOf<String>()
    val listOfPhotos = JSONArray(jsonPhotos.toString())
    //early return if empty
    if (listOfPhotos.length() == 0) return photos
    //add subpaths to mutableList
    for (i in 0 until listOfPhotos.length()) {
        val photo: JSONObject = listOfPhotos.getJSONObject(i)
        photos.add(i, photo["subpath"].toString())
    }
    //return subpaths
    return photos
}

private fun getSellerName(sellerObject: Any): String {
    return JSONObject(sellerObject.toString()).get("userid").toString()
}