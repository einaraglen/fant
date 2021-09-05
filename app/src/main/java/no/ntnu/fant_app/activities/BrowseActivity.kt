package no.ntnu.fant_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fant_browse.*
import no.ntnu.fant_app.Product
import no.ntnu.fant_app.ProductAdapter
import no.ntnu.fant_app.R
import no.ntnu.fant_app.User
import org.json.JSONArray
import org.json.JSONObject
import android.content.DialogInterface




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

        goto_login_button.setOnClickListener {
            if (!User.isLoggedIn) {
                val intent: Intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val username = User.uid
                AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout $username?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        User.logout()
                        goto_login_button.text = "Login"
                    }
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }

        goto_login_button.text = if (!User.isLoggedIn) "Login" else User.uid

        add_product_button.setOnClickListener {
           val intent: Intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

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
}
