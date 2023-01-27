package com.techyourchance.dagger2course.questions

import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FetchQuestionsUseCase(private val retrofit: Retrofit) {

    sealed class Result {
        class Success(val questions: List<Question>) : Result()
        object Failure : Result()
    }

    sealed class ResultDetail {
        class Success(val questionDetail: String) : ResultDetail()
        object Failure : ResultDetail()
    }

    private val stackOverflowApi = retrofit.create(StackoverflowApi::class.java)

    suspend fun fetchLatestQuestions(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val response = stackOverflowApi.lastActiveQuestions(20)
                if (response.isSuccessful && response.body() != null) {
                    return@withContext Result.Success(response.body()!!.questions)
                } else {
                    return@withContext Result.Failure
                }
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    return@withContext Result.Failure
                } else {
                    throw t
                }
            }
        }
    }

    suspend fun fetchDetailQuestions(questionId: String): ResultDetail {
        return withContext(Dispatchers.IO) {
            try {
                val response = stackOverflowApi.questionDetails(questionId)
                if (response.isSuccessful && response.body() != null) {
                    val questionBody = response.body()!!.question.body
                    return@withContext ResultDetail.Success(questionBody)
                } else {
                    return@withContext ResultDetail.Failure
                }
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    return@withContext ResultDetail.Failure
                }else{
                    throw t
                }
            }
        }
    }
}