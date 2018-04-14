package com.example.rmit.playgame.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by RMIT on 28/12/2017.
 */
interface IQuestion {

    @FormUrlEncoded
    @POST("insert_question.php")
    fun insertQuestion(
            @Field("ques_id") quesID: String,
            @Field("question") question: String,
            @Field("option_a") optionA: String,
            @Field("option_b") optionB: String,
            @Field("option_c") optionC: String,
            @Field("option_d") optionD: String,
            @Field("option_e") optionE: String,
            @Field("answer") answer: String): Call<Question>


    @FormUrlEncoded
    @POST("quiz/get_question.php")
    fun getQuestion(
            @Field("ques_id") questionID: String): Call<Question>


}