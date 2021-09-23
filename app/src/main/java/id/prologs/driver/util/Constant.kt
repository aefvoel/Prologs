package id.prologs.driver.util

object Constant {


    const val BASE_URL = "https://api.prologs.id/v2/"
    const val API_KEY = "78376329861362737328746357238578"

    const val DIR_API_KEY = "AIzaSyCRB7YnG5wLlipm0VdfNSdglKh3IR_yEEc"

    const val RequestCamera = 10
    const val RequestGallery = 11

    object Header {
        const val TOKEN = "token"
        const val CACHE = "Cache-Control"
        const val KEY = "api-key"

    }

    object StatusCode {
        const val CODE_INVALID_TOKEN = -1
        const val CODE_INVALID_REQUEST = 0
        const val CODE_SUCCESS = 1
        const val CODE_NO_DATA = 2
        const val CODE_INACTIVE_USER = 3
        const val CODE_VERIFY_ACCOUNT = 4
        const val CODE_EMAIL_VERIFICATION = 5
        const val CODE_FORCE_UPDATE = 6
        const val CODE_SIMPLE_APP_UPDATE_ALERT = 7
        const val CODE_UNDER_MAINTENANCE = 8
        const val CODE_SOCIAL_ID_UNAUTHORIZED = 11
    }
}