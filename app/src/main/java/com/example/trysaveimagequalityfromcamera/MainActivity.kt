package com.example.trysaveimagequalityfromcamera

import android.R.attr
import android.R.attr.bitmap
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath:String
    private lateinit var imageURI:Uri
    private lateinit var imageView:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById<ImageView>(R.id.imageView)
        /*findViewById<Button>(R.id.button).setOnClickListener {
            var fileName = "photo"
            var storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                var imageFile = File.createTempFile(fileName,".jpg",storageDirectory)
                currentPhotoPath = imageFile.absolutePath
                 imageURI = FileProvider.getUriForFile(this,"com.example.trysaveimagequalityfromcamera.fileprovider",imageFile)
                Log.d(TAG, "onCreate: imageURI: $imageURI")
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI)
                startActivityForResult(intent,1)
            }catch (ex:IOException){
                Log.d(TAG, "onCreate: at createTempFile")
            }


        }*/
        findViewById<Button>(R.id.button).setOnClickListener {
            dispatchTakePictureIntent()
        }
    }



  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && resultCode == RESULT_OK){
            var bitmap = BitmapFactory.decodeFile(currentPhotoPath)

//            imageView.setImageBitmap(bitmap)
            Glide.with(this).load(imageURI).into(imageView)

        }
    }*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val imageBitmap = data?.extras?.get("data") as Bitmap
          var imageBitmap:Bitmap = if (data?.data != null) {
              data.extras?.get("data") as Bitmap
          } else {
              MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)
          }
            imageView.setImageBitmap(imageBitmap)
          var uri = saveImage(imageView,"Image")
          Log.d(TAG, "dispatchTakePictureIntent: uri: $uri")
        }
    }
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    imageURI = photoURI

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
                Log.d(TAG, "dispatchTakePictureIntent: imageURI: $imageURI")
                Glide.with(this).load(imageURI).into(imageView)
            }
        }
    }
    private fun saveImage(drawableImage:ImageView, title:String):Uri{
        // Get the image from drawable resource as drawable object
        Log.d(TAG, "saveImage: drawableImage.drawable 1: ${drawableImage.drawable}")
        val drawable = drawableImage.drawable as BitmapDrawable
        Log.d(TAG, "saveImage: drawableImage.drawable 2: ${drawableImage.drawable}")

        // Get the bitmap from drawable object
        val bitmap = drawable.bitmap

        // Save image to gallery
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            title,
            "Image of $title"
        )

        // Parse the gallery image url to uri
        return Uri.parse(savedImageURL)
    }
    /*private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }*/
    /*private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }*/
}