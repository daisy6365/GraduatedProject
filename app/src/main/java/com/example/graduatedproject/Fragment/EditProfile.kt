package com.example.graduatedproject.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.graduatedproject.R
import com.example.graduatedproject.Util.ServerUtil
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_study_modify.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.*


class EditProfile() : DialogFragment() {
    //DialogFragment를 호출한 부모 Fragment에 결과를 반환
    lateinit var imageUrl : String
    lateinit var nickname : String
    var new_imageUrl : Uri? = null //맨처음! 넣어지는 이미지 경로
    var new_imageUrlPath: String? = null
    var imageFile : File? = null // 절대경로로 변환되어 파일형태의 이미지

    var deleteImage : Boolean = false
    lateinit var newNickname : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val args : Bundle? = getArguments()
        nickname = args?.getString("nickname").toString()
        imageUrl = args?.getString("imageUrl").toString()


        //cancel 버튼 누르면 다시 MYPAGE로 돌아가도록 함
        view.edit_profile_cancel.setOnClickListener {
            dismiss()
        }
        //apply 버튼 누르면 받아온 imageUrl과 nickname 서버로 보내기
        // 서버 통신
        // 다시 MYPAGE로 돌아가도록 함
        return view
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //결과값이 사진을 선택했을때
            new_imageUrl = result.data?.data!!
            Glide.with(this)
                .load(new_imageUrl)
                .centerCrop()
                .into(edit_profile_img)
            //절대경로변환 함수 호출
            new_imageUrlPath= absolutelyPath(new_imageUrl!!)
            imageFile = File(new_imageUrlPath)
        } else {
            //취소했을때
            Toast.makeText(activity, "사진 선택 취소", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var edit_profile_img : ImageView = view.findViewById(R.id.edit_profile_img)
        var edit_profile_name : EditText = view.findViewById(R.id.edit_profile_name)
        var change_img : Button= view.findViewById(R.id.change_img)
        var change_default : Button= view.findViewById(R.id.change_default)
        var edit_profile_apply : Button = view.findViewById(R.id.edit_profile_apply)

        lateinit var requestImg: RequestBody
        var imageBitmap : MultipartBody.Part? = null


        //그 사용자한테 저장된 이미지, 닉네임 불러옴
        // 변경전 사진 화면에 붙이기
        if(imageUrl.equals("null")){
            Glide.with(this)
                .load("https://project-lambda-bucket.s3.ap-northeast-2.amazonaws.com/KakaoTalk_Image_2021-07-19-03-04-43.png")
                .centerCrop()
                .into(edit_profile_img)
        }
        else{
            Glide.with(view)
                .load(imageUrl.toUri())
                .centerCrop()
                .into(edit_profile_img)
        }

        // 변경전 닉네임 화면에 붙이기
        edit_profile_name.setText(nickname)


        change_img.setOnClickListener {
            openGalley()
        }
        //원하는 사진 누르면 edit_profile_img에 갖다 붙임
        //원하는 사진의 url 받아 놓기

        //기본이미지로 변경
        change_default.setOnClickListener {
            Glide.with(this)
                .load(R.drawable.profile_init)
                .centerCrop()
                .error(imageUrl)
                .into(edit_profile_img)
            deleteImage = true
        }



        //확인 버튼
        edit_profile_apply.setOnClickListener {
            //accessToken을 가져옴
            val pref = requireActivity().getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            var accessToken: String = "Bearer " + pref.getString("access_token", "").toString()

            newNickname = edit_profile_name.text.toString()

            // RequestBody로 변환 후 MultipartBody.Part로 파일 컨버전
            if(imageFile != null){
                requestImg= RequestBody.create(MediaType.parse("image/*"),imageFile)
                imageBitmap = MultipartBody.Part.createFormData("image", imageFile?.getName(), requestImg)

            }else{}

            //deleteImage여부, 새로운 닉네임
            val paramObject = JsonObject()
            paramObject.addProperty("deleteImage", deleteImage)
            paramObject.addProperty("nickName", newNickname)

            val request = RequestBody.create(MediaType.parse("application/json"),paramObject.toString())

//            requestdelete = RequestBody.create(MediaType.parse("deleteImage"),deleteImage.toString())
//            reqestnickname = RequestBody.create(MediaType.parse("nickName"),newNickname)



            //변경된이름을 EditText로부터 가져옴
            ServerUtil.retrofitService.requestModifyProfile(accessToken,imageBitmap,request)
                .enqueue(object : retrofit2.Callback<Void> {
                    @SuppressLint("ResourceType")
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("EditProfile", "프로필변경 성공")
                            Toast.makeText(getActivity(), "프로필변경 되었습니다.", Toast.LENGTH_SHORT).show()

                            dismiss()
                        }
                    }
                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Log.d("EditProfile", "프로필변경 실패")
                    }
                })
        }
    }

    private fun openGalley(){
        //갤러리에 접근하도록 함 -> 갤러리를 엶
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getImage.launch(intent)
    }


    //저장한 선택사진의 경로를 절대경로로 바꿈
    fun absolutelyPath(new_imageUrl: Uri): String {
        val contentResolver = requireContext()!!.contentResolver?: return null.toString()

        val filePath = requireContext()!!.applicationInfo.dataDir + File.separator +
                System.currentTimeMillis()
        val file = File(filePath)

        try {
            val inputStream = contentResolver.openInputStream(new_imageUrl) ?: return null.toString()
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int

            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

            outputStream.close()
            inputStream.close()

        } catch (e: IOException) {
            return null.toString()
        }
        val result = file.absolutePath
        return result
    }



    fun getInstance(): EditProfile {
        return EditProfile()
    }
}


