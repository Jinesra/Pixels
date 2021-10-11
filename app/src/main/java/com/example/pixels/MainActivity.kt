package com.example.pixels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ProgressBar
import android.os.Handler
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var value = 0
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the bitmap from assets
        val bitmap = assetsToBitmap("a1.jpeg")

        bitmap?.apply {
            // set original bitmap to first image view
            imageView.setImageBitmap(this)

        }

        val button: Button = findViewById(R.id.button2)
        button.setOnClickListener {
            val progressBar = findViewById<View>(R.id.progressBar2) as ProgressBar
            progressBar.visibility = ProgressBar.VISIBLE
             fun buttonClicked() = runBlocking{

                val progress = launch {

                    while (value <= 100) {
                        value += 1
                        delay(100L)
                        handler.post {
                            progressBar.progress = value
                        }
                    }
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
                 progress.join()
                 bitmap?.apply {
                     // set inverted colors bitmap to image view
                     invertColors()?.apply {
                         imageView.setImageBitmap(this)
                     }
                 }
                 textView.text = "Inverted Pixels Bitmap"
            }
            GlobalScope.async {
                buttonClicked()
            }
        }
        val button2: Button = findViewById(R.id.button)
        button2.setOnClickListener {
            value = 0
            val bitmap = assetsToBitmap("a1.jpeg")

            bitmap?.apply {
                // set original bitmap to first image view
                imageView.setImageBitmap(this)

            }
            textView.text = "Original Bitmap"
            val progressBar = findViewById<View>(R.id.progressBar2) as ProgressBar
            progressBar.visibility = ProgressBar.VISIBLE
            Thread(Runnable {
                    while (value <= 100) {
                        value += 1
                        Thread.sleep(100L)
                        handler.post {
                            progressBar.progress = value
                        }
                    }
                bitmap?.apply {
                    // set inverted colors bitmap to image view
                    invertColors()?.apply {
                        imageView.setImageBitmap(this)
                    }
                }
                textView.text = "Inverted Pixels Bitmap"

            }).start()


        }
    }
}


// extension function to get a bitmap from assets
fun Context.assetsToBitmap(fileName:String):Bitmap?{
    return try {
        val stream = assets.open(fileName)
        BitmapFactory.decodeStream(stream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


// extension function to invert bitmap colors
fun Bitmap.invertColors(): Bitmap? {
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val matrixInvert = ColorMatrix().apply {
        set(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
    }

    val paint = Paint()
    ColorMatrixColorFilter(matrixInvert).apply {
        paint.colorFilter = this
    }

    Canvas(bitmap).drawBitmap(this, 0f, 0f, paint)
    return bitmap
}