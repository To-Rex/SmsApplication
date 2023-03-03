package com.test.smsapplication.service;

import com.test.smsapplication.models.DataClass;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserService {
    @GET("/sms")
    Call<DataClass> getData();
    @GET("/sms/status?status=2")
    Call<DataClass> getData1();
}