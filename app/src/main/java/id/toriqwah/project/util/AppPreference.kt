package id.toriqwah.project.util

import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import id.toriqwah.project.model.Profile

class AppPreference {

    companion object {
        private const val a_first_time = "a_first_time"
        private const val a_login = "a_login"
        private const val a_token = "a_token"
        private const val a_profile = "a_profile"
        private const val a_image = "a_image"
        private const val a_att = "a_att"



        fun deleteAll() {
            Hawk.delete(a_first_time)
            Hawk.delete(a_login)
            Hawk.delete(a_token)
            Hawk.delete(a_profile)
            Hawk.delete(a_image)
            Hawk.delete(a_att)

        }

        fun putFirstTime(value: Boolean) {
            Hawk.put(a_first_time, value)
        }

        fun isFirstTime(): Boolean {
            return (Hawk.get(a_first_time, true))
        }

        fun putLogin(value: Boolean) {
            Hawk.put(a_login, value)
        }

        fun isLogin(): Boolean {
            return (Hawk.get(a_login, false))
        }
        fun putToken(value: String) {
            Hawk.put(a_token, value)
        }

        fun getToken(): String {
            return (Hawk.get(a_token, ""))
        }
        fun putProfile(value: Profile) {
            Hawk.put(a_profile, value)
        }

        fun getProfile(): Profile {
            return (Hawk.get(a_profile))
        }

        fun putImage(value: Bitmap) {
            Hawk.put(a_image, Gson().toJson(value))
        }

        fun getImage(): Bitmap? {
            val type = object : TypeToken<Bitmap>() {}.type
            return Gson().fromJson<Bitmap>(Hawk.get(a_image, ""), type)
        }
        fun putAttendance(value: String) {
            Hawk.put(a_att, value)
        }

        fun getAttendance(): String {
            return (Hawk.get(a_att, ""))
        }
    }

}