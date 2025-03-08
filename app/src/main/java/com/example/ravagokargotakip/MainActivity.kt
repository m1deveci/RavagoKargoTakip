package com.ravago.kargotakip

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraPhotoPath: Uri? = null
    private lateinit var webView: WebView // WebView'ı sınıf seviyesinde tanımla
    private lateinit var webChromeClient: WebChromeClient // WebChromeClient'ı sınıf seviyesinde tanımla

    // Kamera intent'i için launcher
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Kamera ile çekilen fotoğrafı filePathCallback'e gönder
            if (cameraPhotoPath != null) {
                filePathCallback?.onReceiveValue(arrayOf(cameraPhotoPath!!))
            } else {
                filePathCallback?.onReceiveValue(null)
            }
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    // Runtime izinleri için launcher
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
            // İzinler verildi
        } else {
            Toast.makeText(this, "Kamera ve depolama izinleri gereklidir.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // İzinleri kontrol et ve iste
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        // WebView'ı başlat
        webView = findViewById(R.id.webView)
        val webSettings = webView.settings

        // WebView ayarları
        webSettings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            setSupportMultipleWindows(true) // Yeni sekme açmayı destekle
        }

        // WebViewClient ayarla
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }

        // WebChromeClient'ı başlat ve WebView'a ata
        webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback?.onReceiveValue(null) // Önceki callback'i iptal et
                this@MainActivity.filePathCallback = filePathCallback

                // Kamera intent'i oluştur
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(packageManager) != null) {
                    // Geçici bir fotoğraf dosyası oluştur
                    val photoFile = createImageFile()
                    if (photoFile != null) {
                        cameraPhotoPath = FileProvider.getUriForFile(
                            this@MainActivity,
                            "${applicationContext.packageName}.fileprovider",
                            photoFile
                        )
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoPath)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Cihazınızda kamera uygulaması bulunamadı.", Toast.LENGTH_SHORT).show()
                }

                // Dosya seçimi intent'i oluştur
                val fileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }

                // Seçenekleri birleştir (Kamera ve Dosya Seçimi)
                val chooserIntent = Intent.createChooser(fileIntent, "Dosya Seç veya Fotoğraf Çek")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

                // Intent'i başlat
                cameraLauncher.launch(chooserIntent)
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                // Yeni bir WebView oluştur
                val newWebView = WebView(this@MainActivity).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = this@MainActivity.webChromeClient // WebChromeClient'ı yeni WebView'a ata
                }

                // Yeni WebView'ı bir dialog veya yeni aktivite olarak aç
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                return true
            }
        }

        // WebChromeClient'ı WebView'a ata
        webView.webChromeClient = webChromeClient

        // Web sayfasını yükle
        webView.loadUrl("https://kargotakip.ittoolbox.com.tr")
    }

    // Geçici bir fotoğraf dosyası oluştur
    private fun createImageFile(): File? {
        return try {
            val storageDir = getExternalFilesDir("Pictures")
            File.createTempFile(
                "JPEG_${System.currentTimeMillis()}_",
                ".jpg",
                storageDir
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}