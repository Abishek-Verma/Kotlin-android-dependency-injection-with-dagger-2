package com.techyourchance.dagger2course.screens.questiondetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.techyourchance.dagger2course.Constants
import com.techyourchance.dagger2course.networking.StackoverflowApi
import com.techyourchance.dagger2course.questions.FetchQuestionsUseCase
import com.techyourchance.dagger2course.screens.common.ScreenNavigator
import com.techyourchance.dagger2course.screens.common.dialogs.DialogNavigator
import com.techyourchance.dagger2course.screens.common.dialogs.ServerErrorDialogFragment
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionDetailsActivity : AppCompatActivity(), QuestionDetailsViewMvc.Listener {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var viewDetailMvc: QuestionDetailsViewMvc
    private lateinit var fetchQuestionsUseCase: FetchQuestionsUseCase
    private lateinit var dialogNavigator: DialogNavigator
    private lateinit var screenNavigator: ScreenNavigator

    private lateinit var questionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDetailMvc = QuestionDetailsViewMvc(LayoutInflater.from(this), null) // Since activity itself is root passing it null

        setContentView(viewDetailMvc.rootView)

        fetchQuestionsUseCase = FetchQuestionsUseCase()
        dialogNavigator = DialogNavigator(supportFragmentManager)
        screenNavigator = ScreenNavigator(this)

        // retrieve question ID passed from outside
        questionId = intent.extras!!.getString(EXTRA_QUESTION_ID)!!
    }

    override fun onStart() {
        super.onStart()
        viewDetailMvc.registerListener(this)
        fetchQuestionDetails(questionId)
    }

    override fun onStop() {
        super.onStop()
        viewDetailMvc.unRegisterListener(this)
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun fetchQuestionDetails(questionId: String) {
        coroutineScope.launch {
            viewDetailMvc.showProgressIndication()
            try {
                val result = fetchQuestionsUseCase.fetchDetailQuestions(questionId)
                when(result){
                    is FetchQuestionsUseCase.ResultDetail.Success ->{
                        viewDetailMvc.updateUI(result.questionDetail)
                    }

                    is FetchQuestionsUseCase.ResultDetail.Failure -> onFetchFailed()
                }
            }finally {
             viewDetailMvc.hideProgressIndication()
            }
        }
    }

    private fun onFetchFailed() {
       dialogNavigator.showServerErrorDialog()
    }

    companion object {
        const val EXTRA_QUESTION_ID = "EXTRA_QUESTION_ID"
        fun start(context: Context, questionId: String) {
            val intent = Intent(context, QuestionDetailsActivity::class.java)
            intent.putExtra(EXTRA_QUESTION_ID, questionId)
            context.startActivity(intent)
        }
    }

    override fun onBackClicked() {
        screenNavigator.navigateBack()
    }
}