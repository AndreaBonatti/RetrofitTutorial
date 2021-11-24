package com.example.retrofittutorial

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"

class MainActivity : AppCompatActivity() {

    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutGenerateNewFact: RelativeLayout = findViewById(R.id.layout_generate_new_fact)
        val tvTextview: TextView = findViewById(R.id.tv_textView)
        val tvTimestamp: TextView = findViewById(R.id.tv_timeStamp)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        getCurrentData(tvTextview, tvTimestamp, progressBar)

        layoutGenerateNewFact.setOnClickListener {
            getCurrentData(tvTextview, tvTimestamp, progressBar)
        }
    }

    private fun getCurrentData(
        tv_textView: TextView,
        tv_timeStamp: TextView,
        progressBar: ProgressBar
    ) {
        tv_textView.visibility = View.GONE
        tv_timeStamp.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.text)

                    withContext(Dispatchers.Main) {
                        tv_textView.visibility = View.VISIBLE
                        tv_timeStamp.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE

                        tv_textView.text = data.text
                        tv_timeStamp.text = data.createdAt
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        applicationContext,
                        "Seems like something went wrong...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}