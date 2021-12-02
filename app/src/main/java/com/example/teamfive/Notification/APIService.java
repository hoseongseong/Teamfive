package com.example.teamfive.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAASMjYtlQ:APA91bEBirldApPzOrKjlD5Ssc-VTllf2PNF0SHf_y27WUQLMvg39GMaCGq87KkS6nJLsQ393x67FCy6FXz2NQlM12Hk92pqa2qTIJUIC2rJzNELrBzetW4K65Ld7xu1KrkInWcZMRtI"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationData body);
}
