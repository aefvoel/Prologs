package id.prologs.driver.repository

import com.haroldadmin.cnradapter.NetworkResponse
import id.prologs.driver.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UserService {

    @POST("auth/check_imei")
    suspend fun checkImei(@Body checkIn: Login): NetworkResponse<ResponseSuccess<Profile>, ResponseError>

    @POST("driver/checkin")
    suspend fun checkIn(@Body checkIn: Check): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/checkout")
    suspend fun checkOut(@Body out: Logout): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/list_new_task")
    suspend fun listNewTask(@Body checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError>

    @POST("driver/list_running_task")
    suspend fun listRunningTask(@Body checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError>

    @POST("driver/list_completed_task")
    suspend fun listCompletedTask(@Body checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError>

    @POST("driver/detail_task")
    suspend fun detailTask(@Body detail: Detail): NetworkResponse<DetailResponse, ResponseError>

    @POST("driver/update_task")
    suspend fun updateTask(@Body update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @Multipart
    @POST("driver/update_task_retur")
    suspend fun updateTaskRetur(@PartMap report: @JvmSuppressWildcards Map<String, RequestBody>,
                       @Part photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @Multipart
    @POST("driver/complete_job")
    suspend fun completeJob(@PartMap report: @JvmSuppressWildcards Map<String, RequestBody>,
                                @Part photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @Multipart
    @POST("driver/complete_job_manual")
    suspend fun completeJobManual(@PartMap report: @JvmSuppressWildcards Map<String, RequestBody>,
                            @Part photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/list_reason")
    suspend fun listReason(): NetworkResponse<ResponseSuccess<ArrayList<Reason>>, ResponseError>

    @POST("driver/track_driver")
    suspend fun trackDriver(@Body track: Track): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/app_setting")
    suspend fun appSetting(@Body update: Update): NetworkResponse<ResponseSuccess<Setting>, ResponseError>

    @POST("driver/app_setting")
    suspend fun appSetting(): NetworkResponse<ResponseSuccess<Setting>, ResponseError>

    @POST("driver/update_password")
    suspend fun updatePassword(@Body update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/list_notification")
    suspend fun listNotification(@Body update: Update): NetworkResponse<ResponseSuccess<ArrayList<Notification>>, ResponseError>

    @POST("driver/delete_notification")
    suspend fun deleteNotification(@Body update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>

    @POST("driver/mark_as_read_notification")
    suspend fun markNotification(@Body update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>
}

open class UserRepository(private val userService: UserService) {

    suspend fun checkImei(checkImei: Login): NetworkResponse<ResponseSuccess<Profile>, ResponseError> {
        return userService.checkImei(checkImei)
    }
    suspend fun checkIn(checkIn: Check): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.checkIn(checkIn)
    }
    suspend fun checkOut(out: Logout): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.checkOut(out)
    }
    suspend fun listNewTask(checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError> {
        return userService.listNewTask(checkIn)
    }
    suspend fun listRunningTask(checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError> {
        return userService.listRunningTask(checkIn)
    }
    suspend fun listCompletedTask(checkIn: Check): NetworkResponse<ResponseSuccess<ArrayList<Task>>, ResponseError> {
        return userService.listCompletedTask(checkIn)
    }
    suspend fun detailTask(detail: Detail): NetworkResponse<DetailResponse, ResponseError>{
        return userService.detailTask(detail)
    }
    suspend fun updateTask(update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.updateTask(update)
    }
    suspend fun updateTaskRetur(report: Map<String, RequestBody>, photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>{
        return userService.updateTaskRetur(report, photo)
    }
    suspend fun listReason(): NetworkResponse<ResponseSuccess<ArrayList<Reason>>, ResponseError> {
        return userService.listReason()
    }
    suspend fun completeJob(report: Map<String, RequestBody>, photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>{
        return userService.completeJob(report, photo)
    }
    suspend fun completeJobManual(report: Map<String, RequestBody>, photo: MultipartBody.Part): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>{
        return userService.completeJobManual(report, photo)
    }
    suspend fun trackDriver(track: Track): NetworkResponse<ResponseSuccess<Nothing>, ResponseError>{
        return userService.trackDriver(track)
    }
    suspend fun appSetting(update: Update): NetworkResponse<ResponseSuccess<Setting>, ResponseError> {
        return userService.appSetting(update)
    }
    suspend fun appSetting(): NetworkResponse<ResponseSuccess<Setting>, ResponseError> {
        return userService.appSetting()
    }
    suspend fun updatePassword(update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.updatePassword(update)
    }
    suspend fun listNotification(update: Update): NetworkResponse<ResponseSuccess<ArrayList<Notification>>, ResponseError> {
        return userService.listNotification(update)
    }
    suspend fun deleteNotification(update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.deleteNotification(update)
    }
    suspend fun markNotification(update: Update): NetworkResponse<ResponseSuccess<Nothing>, ResponseError> {
        return userService.markNotification(update)
    }

}