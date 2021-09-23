package no.ntnu.fant_app.activities

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
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.Header
import no.ntnu.fant_app.User
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RelativeLayout
import cz.msebera.android.httpclient.HttpEntity
import cz.msebera.android.httpclient.HttpHeaders
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.entity.mime.content.FileBody
import cz.msebera.android.httpclient.entity.mime.content.StringBody
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient
import cz.msebera.android.httpclient.impl.client.HttpClients
import cz.msebera.android.httpclient.util.EntityUtils


class AddProductActivity: AppCompatActivity() {
    var isTitleBad: Boolean = false
    var isPriceBad: Boolean = false

    private val REQUEST_IMAGE_CAPTURE = 1
    val FILEPROVIDER = "no.ntnu.fant_app.fileprovider"
    var currentPhoto: File? = null

    var files: MutableList<File> = mutableListOf()
    private val context = this

    //tried everything else, this is only way...
    private class Requester(private val activity: AppCompatActivity, private val title: String,
                            private val description: String, private val price: String,
                            private val photos: MutableList<File>, private val addURL: String): Thread() {
        override fun run() {
            super.run()
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val httpPost: HttpPost = HttpPost(addURL)
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + User.authToken)
            val titleBody: StringBody = StringBody(title, ContentType.TEXT_PLAIN)
            val descriptionBody: StringBody = StringBody(description, ContentType.TEXT_PLAIN)
            val priceBody: StringBody = StringBody(price, ContentType.TEXT_PLAIN)

            val builder: MultipartEntityBuilder = MultipartEntityBuilder.create()

            builder.addPart("title", titleBody)
            builder.addPart("description", descriptionBody)
            builder.addPart("price", priceBody)

            if (photos.size != 0) {
                photos.forEach {
                    val bin: FileBody = FileBody(it)
                    builder.addPart("files", bin)
                }
            }

            val httpEntity: HttpEntity = builder.build()

            httpPost.entity = httpEntity

            try {
                val response: CloseableHttpResponse = httpClient.execute(httpPost)
                val resEntity = response.entity

                if (resEntity != null) {
                    println("Response content length " + resEntity.contentLength)
                }
                EntityUtils.consume(resEntity)
                activity.finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        add_image_button.setOnClickListener { onCameraClick() }

        add_button.setOnClickListener {
            val requester: Requester = Requester(this, title_field.text.toString(), description_field.text.toString(),
                price_field.text.toString(), files, API_URL + "fant/create")
            requester.start()
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

    fun onCameraClick() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            currentPhoto = createImageFile()
            if (currentPhoto != null) {
                val photoURI: Uri = FileProvider.getUriForFile(this, FILEPROVIDER, currentPhoto!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {
        var result: File? = null

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            result = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(currentPhoto != null) {
                try {
                    val localCurrentPhoto = currentPhoto!!
                    val options = BitmapFactory.Options()
                    options.outWidth = 80
                    options.outHeight = 80
                    var bitmap: Bitmap = BitmapFactory.decodeFile((currentPhoto!!.absolutePath), options)
                    bitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                    val current = ImageView(this)
                    current.setImageBitmap(bitmap)
                    current.setPadding(0, 0, 0, 10)
                    current.setOnLongClickListener {
                        //works for me
                        AlertDialog.Builder(context)
                            .setTitle("Remove Image")
                            .setMessage("Would you like to remove this Image?")
                            .setPositiveButton(
                                "Yes"
                            ) { dialog, which ->
                                image_flex.removeView(current)
                                files.remove(localCurrentPhoto)
                            }
                            .setNegativeButton("No", null)
                            .setIcon(android.R.drawable.ic_delete)
                            .show()

                        false
                    }
                    image_flex.addView(current)
                    files.add(currentPhoto!!)
                    currentPhoto = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
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