package com.example.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.os.Build
import android.os.Handler
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity()  {

    private val PERMISSIONS_REQUEST_CODE = 100

    var imageID :Long = 0
    var imageIDFirst :Long =0
    var count = 0

    //Timer
    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                button1.setEnabled(false)
                button2.setEnabled(false)
                button3.setEnabled(false)

            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

        button1.setOnClickListener {
            if(button1.text == "再生")
            {
                button1.text="停止"
                button2.setEnabled(false)
                button3.setEnabled(false)
                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 0.1
                            mHandler.post {
                                //timer.text = String.format("%.1f", mTimerSec)
                                if (imageID > 0) {
                                    imageID = imageID + 1
                                }
                                if(imageID == imageIDFirst+count)
                                {
                                    imageID=imageIDFirst
                                }
                                val imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    imageID
                                )
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
                }
            }else{
                button1.text="再生"
                button2.setEnabled(true)
                button3.setEnabled(true)
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }

        button2.setOnClickListener {
            Log.d("進む","次のピクチャーを表示")

            //if (cursor!!.moveToLast())
            //{
            //    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageIDFirst)
            //    imageView.setImageURI(imageUri)
            //}else {

                if (imageID > 0) {
                    imageID = imageID + 1
                }
                if(imageID == imageIDFirst+count)
                {
                    imageID=imageIDFirst
                }
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageID
                )
                imageView.setImageURI(imageUri)
            //}

        }

        button3.setOnClickListener {
            Log.d("戻る","前のピクチャーを表示")



            if(imageID == imageIDFirst)
            {
                imageID=imageIDFirst+count-1
            }else
            {
                if(imageID > 0){
                    imageID = imageID-1
                }
            }

            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageID)
            imageView.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        if (cursor!!.moveToFirst()) {
            count = cursor.count
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageID = id;
            imageIDFirst = id;
            imageView.setImageURI(imageUri)
        }
        cursor.close()
    }

}
