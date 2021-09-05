package no.ntnu.fant_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.product_item.view.*
import java.io.InputStream
import java.net.URL
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import no.ntnu.fant_app.activities.API_URL


/**
 * Adapts our data struct to be used in a RecyclerView
 */
class ProductAdapter(private val products: MutableList<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))
    }

    fun addProduct(product: Product) {
        products.add(product)
        notifyItemInserted(products.size - 1)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.itemView.apply {
            product_title.text = currentProduct.title
            product_description.text = currentProduct.description
            product_price.text = currentProduct.price.toString() + " kr"
            product_image.setImageBitmap(firstImage(currentProduct.photos))
        }
    }

    private fun firstImage(photos: MutableList<String>): Bitmap? {
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        //check that there is a subpath in [0]
        val imageURL: String = if (photos.isEmpty()) {
            "https://www.kenyons.com/wp-content/uploads/2017/04/default-image-620x600.jpg"
        } else {
            API_URL + "fant/photo/" + photos[0]
        }
        val inputStream: InputStream = URL(imageURL).openConnection().getInputStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}