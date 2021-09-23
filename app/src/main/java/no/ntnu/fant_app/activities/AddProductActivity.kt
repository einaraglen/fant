package no.ntnu.fant_app.activities

<<<<<<< Updated upstream
=======
import android.content.Context
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
import androidx.core.content.FileProvider
=======
import java.net.URL
>>>>>>> Stashed changes
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.Header
import no.ntnu.fant_app.User
import java.io.*
<<<<<<< Updated upstream
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RelativeLayout
import cz.msebera.android.httpclient.entity.mime.content.FileBody
=======
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import java.text.SimpleDateFormat
import java.util.*
>>>>>>> Stashed changes


class AddProductActivity: AppCompatActivity() {
    var isTitleBad: Boolean = false
    var isPriceBad: Boolean = false

    private val REQUEST_IMAGE_CAPTURE = 1
    val FILEPROVIDER = "no.ntnu.fant_app.fileprovider"
    var currentPhoto: File? = null

    var files: MutableList<File> = mutableListOf()
    private val client = AsyncHttpClient()
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        add_image_button.setOnClickListener { onCameraClick() }

        add_button.setOnClickListener {
            val params = RequestParams()

            params.put("title", title_field.text.toString())
            params.put("description", description_field.text.toString())
            params.put("price", price_field.text.toString())
            //send each file in its own key value pair of "files"
            println(files.size)
            files.forEach {
                println("UPLOADING")
                params.put("files", it)
            }

            //formdata headers added
            params.setForceMultipartEntityContentType(true);
            //load loginpage for if we get an auth failure
            val intent: Intent = Intent(this, LoginActivity::class.java)
            //auth headers added
            client.addHeader("Authorization", "Bearer " + User.authToken)
            client.post(
                API_URL + "fant/create", params,
                object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        if (statusCode === 200) {
                            //go to browse
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

<<<<<<< Updated upstream
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
=======
    private fun createImageFile(): File? {
        var result: File? = null

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            result = File.createTempFile(imageFileName, ".jpg", storageDir)
            println("File is $result")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getPickedImage(path: String) : Bitmap? {
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
>>>>>>> Stashed changes
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