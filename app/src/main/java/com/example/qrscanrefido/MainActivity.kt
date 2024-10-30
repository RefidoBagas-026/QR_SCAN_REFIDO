package com.example.qrscanrefido

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.util.EnumMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val _btnScan = findViewById<Button>(R.id.btnScan)
        _btnScan.setOnClickListener{
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setBeepEnabled(true)
            integrator.setPrompt("QR Code")
            integrator.setOrientationLocked(true)
            integrator.initiateScan()
        }
        val _btnGenerate = findViewById<Button>(R.id.btnGenerate)
        val _imgHasil = findViewById<ImageView>(R.id.imgHasil)

        _btnGenerate.setOnClickListener{
            val _generate_QR = findViewById<EditText>(R.id.generateQR)
            var _hasilGenerate = generateQR(_generate_QR.text.toString())
            _imgHasil.setImageBitmap(_hasilGenerate)
        }
    }

    fun generateQR(isiData : String) : Bitmap? {
        val bitMatrix : BitMatrix = try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            MultiFormatWriter().encode(
                isiData,
                BarcodeFormat.QR_CODE,
                600, 600,
                hints
            )
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }

        val qrCodeWidth = bitMatrix.width
        val qrCodeHeight = bitMatrix.height
        val datapixels = IntArray(qrCodeWidth * qrCodeHeight)

        for( y in 0 until qrCodeHeight){
            val offset = y * qrCodeWidth
            for (x in 0 until qrCodeWidth){
                datapixels[offset + x] = if (bitMatrix[x,y]){
                    resources.getColor(R.color.custom_black, theme)
                }else{
                    resources.getColor(R.color.custom_white, theme)
                }
            }
        }
        val bitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, Bitmap.Config.RGB_565)
        bitmap.setPixels(datapixels, 0,qrCodeWidth,0,0,qrCodeWidth,qrCodeHeight)
        return bitmap
    }

    private val scanResults = mutableListOf<String>()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            // Tambahkan hasil scan ke dalam list
            scanResults.add(result.contents)

            // Update tampilan dengan hasil scan terbaru
            val hasilScan = findViewById<TextView>(R.id.hasilScan)
            hasilScan.text = scanResults.joinToString(separator = "\n")  // Menampilkan hasil scan di TextView, masing-masing di baris baru
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}