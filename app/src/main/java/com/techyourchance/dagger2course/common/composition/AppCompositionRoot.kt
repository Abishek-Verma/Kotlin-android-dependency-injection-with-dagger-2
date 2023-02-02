package com.techyourchance.dagger2course.common.composition

import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppCompositionRoot {

    //. Only one instance of the object is created for entire application

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val stackoverflowApi: StackoverflowApi = retrofit.create(StackoverflowApi::class.java)

    val fetchQuestionsUseCase get() = FetchQuestionsUseCase(stackoverflowApi)
}