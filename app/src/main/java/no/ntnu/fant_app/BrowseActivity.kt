package no.ntnu.fant_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.fant_browse.*
import org.json.JSONArray
import org.json.JSONObject

private lateinit var productAdapter: ProductAdapter

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
                //TODO: parse jsonArray and add products to RecyclerView
                addProductsToView(response, productAdapter)
                //println(response.toString())
            },
            { error ->
                //TODO: let user know request did not work
                println(error.toString())
            }
        )
        queue.add(jsonArrayRequest)
    }
}

private fun addProductsToView(jsonArray: JSONArray, adapter: ProductAdapter) {
    //early return if empty
    if (jsonArray.length() == 0) return
    //back to bizz
    for (i in 0 until jsonArray.length()) {
        val current = jsonArray.getJSONObject(i)
        println(current["photos"].toString())
        val product = Product(
            current["title"].toString(),
            current["description"].toString(),
            current["price"].toString().toInt(),
            getSellerName(current["seller"])
            )
        adapter.addProduct(product)
    }
}

private fun getSellerName(sellerObject: Any): String {
    return JSONObject(sellerObject.toString()).get("userid").toString()
}