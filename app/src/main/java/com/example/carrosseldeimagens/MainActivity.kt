package com.example.carrosseldeimagens

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity.apply
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.carrosseldeimagens.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var imagem: ImageView? = null
    var btn_previou:Button? = null
    var btn_next:Button? = null
    var btn_shake:Switch? = null
    var codigo_request = 2
    var next_or_prev = 0
    var shake_on_off = 1
    var play_foto = arrayListOf<String>()

                override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
      imagem = findViewById(R.id.foto)
      btn_previou = findViewById(R.id.button_prev)
      btn_next = findViewById(R.id.button_next)
      btn_shake = findViewById(R.id.switch1)

                                                                   }
// A funçao abaixo da um efeito de movimentar a imagem
    fun ImageView.shake(onEndAction: () -> Unit = {}){

        val startX = 0f
        val translationX = 120f
        val bounceDuration = 350L

        ObjectAnimator.ofFloat(
            imagem,
            "translationX",
            startX,
            translationX,
            startX
        ).apply {
            interpolator = BounceInterpolator()
            duration = bounceDuration
            start()
        }.doOnEnd { onEndAction() }
        vibrate(bounceDuration)
    }

    // funçao para vibrar o celular
    fun vibrate(duration:Long) {

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val vm =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        duration,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            else -> vibrator?.vibrate(duration)
        }

    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()

        btn_shake?.setOnClickListener(){
          shake_on_off+=1
          if(shake_on_off%2 == 0 ){
              imagem?.shake {  }
          }
            else{}

        }
        btn_previou?.setOnClickListener(){
            if(ActivityCompat.checkSelfPermission
                    (this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            { ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ),codigo_request) }
            else{
                next_or_prev = if(next_or_prev == 0) 0 else next_or_prev-1
                get_fotos()
            }
        }
        btn_next?.setOnClickListener(){
            if(ActivityCompat.checkSelfPermission
                    (this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {  ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ),codigo_request)}
            else{
                next_or_prev+=1
                get_fotos()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun get_fotos(){

        var list_fotos = arrayListOf<String>()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                var columnfotos:Int ?= null

                var absolutePathVideo:String ?= null

                var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                var projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                var orderBy:String = MediaStore.Images.Media.DATE_TAKEN
                var cursor = applicationContext.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    "$orderBy DESC"
                )

                columnfotos = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                while (cursor?.moveToNext()==true){
                    absolutePathVideo = cursor!!.getString(columnfotos!!)
                    list_fotos.add(absolutePathVideo)

                }
            }else{
            }

        }catch (e:Exception){
            Log.e("ERROR", e.message?:"")
        }
        next_or_prev = if(next_or_prev == list_fotos.size) 0 else next_or_prev

        var uri = Uri.parse(list_fotos[next_or_prev])

        imagem?.setImageURI(uri)
        if(shake_on_off%2 ==0) imagem?.shake() else null

    }


}