package com.example.user.recorder_demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_write.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class WriteActivity : AppCompatActivity() {
    val REQUEST_CODE_SELECT_IMAGE: Int = 1004

    //인텐트 받아올놈들
    var user_name : String? = null
    var user_age : String? = null
    var user_gender : String? = null
    var user_alchol : String? = null
    var user_today : String? = null



    private var mImage: MultipartBody.Part? = null
    private var mVoice1: MultipartBody.Part? = null
    private var mVoice2: MultipartBody.Part? = null
    private var mVoice3: MultipartBody.Part? = null

    private var mLongVoice1: MultipartBody.Part? = null
    private var mLongVoice2: MultipartBody.Part? = null
    private var mLongVoice3: MultipartBody.Part? = null


    private var mnull: MultipartBody.Part? = null




    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        //인텐트 값 받자
        if (intent.hasExtra("In_name") &&
            intent.hasExtra("In_age") &&
            intent.hasExtra("In_gender") &&
            intent.hasExtra("In_alchol") ) {

            user_name = intent.getStringExtra("In_name")
            user_age = intent.getStringExtra("In_age")
            user_gender = intent.getStringExtra("In_gender")
            user_alchol = intent.getStringExtra("In_alchol")
            user_today = intent.getStringExtra("In_today")



            // Toast.makeText(this, user_age + user_alchol + user_gender + user_name, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        getWriteBoardResponse()

    }
/*
    public static String saveBitmapToJpeg(Context context, Bitmap bitmap){
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = getRandomString(10) + ".jpg"; // 파일이름은 마음대로!
        File tempFile = new File(storage,fileName);
        try{
            tempFile.createNewFile(); // 파일을 생성해주
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out); // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath(); // 임시파일 저장경로를 리턴해주면 끝!
    }*/




    private fun make_MultiPartBody(path : String, Sever_in_name : String) : MultipartBody.Part?
    {
        val file = File(path)
        val body = RequestBody.create(
            MediaType.parse("multipart/form-data"), file)
        val multipartBody = MultipartBody.Part.createFormData(
            Sever_in_name,
            file.name,
            body
        )

        return multipartBody
    }

    private fun getWriteBoardResponse() {
        val token = SharedPreferenceController.getAuthorization(this)
        var name = RequestBody.create(MediaType.parse("text/plain"), user_name.toString())
        var gender = RequestBody.create(MediaType.parse("text/plain"), user_gender.toString())
        var age : Int = user_age?.toInt()!!
        var status = RequestBody.create(MediaType.parse("text/plain"), user_alchol.toString())

        mVoice1 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "first" + ".wav"
        , "voicefile1"
            )
        mVoice2 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "second" + ".wav"
            , "voicefile2"
        )
        mVoice3 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "third" + ".wav"
            , "voicefile3"
        )
        mLongVoice1 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "Long_first" + ".wav"
            , "voicefile4"
        )
        mLongVoice2 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "Long_second" + ".wav"
            , "voicefile5"
        )
        mLongVoice3 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "Long_third" + ".wav"
            , "voicefile6"
        )
        mImage = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  ".jpg"
            , "videofile"
        )


        /*
        //보이스파일1
        val voice_file1 = File(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "first" + ".3gp")
        val voice_Body1 = RequestBody.create(
            MediaType.parse("multipart/form-data"),voice_file1)
        mVoice1 = MultipartBody.Part.createFormData(
            "voicefile1",
            voice_file1.name,
            voice_Body1
        )

//        val file : File = File(imageURI)
//        val requestfile : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//        val data : MultipartBody.Part = MultipartBody.Part.createFormData("photo", file. name , requestfile)
        //보이스파일2
        val voice_file2 = File(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "second" + ".3gp")
        val voice_Body2 = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            voice_file2
        ) //첫번째 매개변수 String을 꼭! 꼭! 서버 API에 명시된 이름으로 넣어주세요!!!
        mVoice2 = MultipartBody.Part.createFormData(
            "voicefile2",
            voice_file2.name,
            voice_Body2
        )


        //보이스파일3
        val voice_file3 = File(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "third" + ".3gp")
        val voice_Body3 = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            voice_file3
        ) //첫번째 매개변수 String을 꼭! 꼭! 서버 API에 명시된 이름으로 넣어주세요!!!
        mVoice3 = MultipartBody.Part.createFormData(
            "voicefile3",
            voice_file3.name,
            voice_Body3
        )
/*
        //롱 보이스파일1
        val long_voice_file1 = File(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today +  "Long_first" + ".3gp")
        val long_voice_Body1 = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            long_voice_file1
        ) //첫번째 매개변수 String을 꼭! 꼭! 서버 API에 명시된 이름으로 넣어주세요!!!
        mVoice3 = MultipartBody.Part.createFormData(
            "voicefile3",
            voice_file3.name,
            voice_Body3
        )*/

        //이미지파일
        val image_file = File(Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" + user_today + ".jpg")
        val photoBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            image_file
        ) //첫번째 매개변수 String을 꼭! 꼭! 서버 API에 명시된 이름으로 넣어주세요!!!
        mImage = MultipartBody.Part.createFormData(
            "videofile",
            image_file.name,
            photoBody
        )
        */

        val postSendFileResponse = networkService.postSendFileResponse(token, name, gender, age, status, mVoice1, mVoice2, mVoice3, mLongVoice1, mLongVoice2, mLongVoice3, mImage)

            postSendFileResponse.enqueue(object : Callback<PostSendFileResponse> {
            override fun onFailure(call: Call<PostSendFileResponse>, t: Throwable) {
                Log.e("write fail", t.toString())
            }

            override fun onResponse(call: Call<PostSendFileResponse>, response: Response<PostSendFileResponse>) {
                if (response.isSuccessful) {
                    //toast(response.body()!!.message)
                 //    text_send.text = "response.body()!!.message + " - " + response.body()!!.status;
                    text_send.text = "서버 전송 여부" + " - " + response.body()!!.status;


                    Log.i("TEST",response.body()!!.message)
                    Log.i("TEST",response.body()!!.status)
                    //toast( response.body()!!.message + " - " + response.body()!!.status)

                   // finish ()
                }
            }
        })

}}