package com.example.kotlinapplicationdelivery.providers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.kotlinapplicationdelivery.api.ApiRoutes
import com.example.kotlinapplicationdelivery.models.ResponseHttp
import com.example.kotlinapplicationdelivery.models.User
import com.example.kotlinapplicationdelivery.routes.UsersRoutes
import com.example.kotlinapplicationdelivery.utils.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UsersProvider(private val token: String? = null) {
    val TAG = "UsersProvider"

    private var usersRoutes: UsersRoutes? = null
    private var usersRoutesToken: UsersRoutes? = null

    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()

        if (token != null) {
            usersRoutesToken = api.getUsersRoutesWithToken(token)
        }

    }



    fun getDeliveryMen(): Call<ArrayList<User>>? {
        return usersRoutesToken?.getDeliveryMen(token!!)
    }

    fun register(user: User): Call<ResponseHttp>? {
        return usersRoutes?.register(user)
    }
    fun registerRoles(id: String, idRole: Int): Call<ResponseHttp>? {
        return usersRoutes?.registerRoles(id, idRole)
    }
    fun resetPassword(email: String): Call<ResponseHttp>? {
        return usersRoutes?.resetPassword(email)
    }

    fun changePassword(oldPassword: String, newPassword: String, email: String): Call<ResponseHttp>? {
        return usersRoutesToken?.changePassword(oldPassword, newPassword, email)
    }
    fun login(email: String, password: String): Call<ResponseHttp>? {
        return usersRoutes?.login(email, password)
    }

    fun updateWithoutImage(user: User): Call<ResponseHttp>? {
        return usersRoutesToken?.updateWithoutImage(user, token!!)
    }

    private fun updateNotificationToken(user: User): Call<ResponseHttp>? {
        return usersRoutesToken?.updateNotificationToken(user, token!!)
    }

    fun update(file: File, user: User): Call<ResponseHttp>? {
        val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val image = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), user.toJson())
        return usersRoutesToken?.update(image, requestBody, token!!)
    }

    fun createToken(user: User, context: Activity) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result

            val sharedPref = SharedPref(context)

            user.notificationToken = token

            sharedPref.save("user", user)

            updateNotificationToken(user)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    if (response.body() == null) {
                        Log.d(TAG, "There was an error creating the token")
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }

            })

            // Get new FCM registration token

            Log.d(TAG, "NOTIFICATION TOKEN $token")
        })

    }
}