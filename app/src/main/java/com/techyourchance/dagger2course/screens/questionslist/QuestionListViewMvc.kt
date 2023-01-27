package com.techyourchance.dagger2course.screens.questionslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techyourchance.dagger2course.R
import com.techyourchance.dagger2course.questions.Question
import java.util.ArrayList

//? Holds the UI logic of QuestionListActivity
class QuestionListViewMvc(private val layoutInflater: LayoutInflater, private val parent: ViewGroup?) {

    //. Implementing observer pattern
    interface Listener{
        fun onRefreshClick()
        fun onQuestionCLicked(clickedQuestion: Question)
    }

    private val swipeRefresh: SwipeRefreshLayout
    private val recyclerView: RecyclerView
    private val questionsAdapter: QuestionsAdapter

    val rootView: View = layoutInflater.inflate(R.layout.layout_questions_list, parent,false)
    private val context: Context get() = rootView.context
    private val listeners = HashSet<Listener>()

    //. Called after constructor of the class is called
    //* Called everytime when object is created
    init {
        // init pull-down-to-refresh
        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            for (listener in listeners){
                listener.onRefreshClick()
            }
        }

        // init recycler view
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)
        questionsAdapter = QuestionsAdapter{ clickedQuestion ->
           for (listener in listeners){
               listener.onQuestionCLicked(clickedQuestion)
           }
        }
        recyclerView.adapter = questionsAdapter
    }

    fun bindQuestions(questions: List<Question>) {
        questionsAdapter.bindData(questions)
    }

    fun showProgressIndication() {
        swipeRefresh.isRefreshing = true
    }

    fun hideProgressIndication() {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
    }

    //. Easy access of findViewById without putting rootView. everytime
    private fun<T : View?> findViewById(@IdRes id: Int): T {
        return rootView.findViewById<T>(id)
    }

    fun registerListener(listener: Listener){
        listeners.add(listener)
    }

    fun unRegisterListener(listener: Listener){
        listeners.remove(listener)
    }

    //? ---------------- Adapter Class ------------------

    class QuestionsAdapter(private val onQuestionClickListener: (Question) -> Unit) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

        private var questionsList: List<Question> = ArrayList(0)

        fun bindData(questions: List<Question>) {
            questionsList = ArrayList(questions)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_question_list_item, parent, false)
            return QuestionViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
            holder.title.text = questionsList[position].title
            holder.itemView.setOnClickListener {
                onQuestionClickListener.invoke(questionsList[position])
            }
        }

        override fun getItemCount(): Int {
            return questionsList.size
        }

        ///. --------------------- View Holder class -------------------

        inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.txt_title)
        }


    }

}