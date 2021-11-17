package ru.oktemsec.pr9_abramov

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy


class MainActivity : AppCompatActivity() {

    lateinit var view_city:TextView
    lateinit var view_temp:TextView
    lateinit var view_desc:TextView
    lateinit var view_weather:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_city = findViewById(R.id.town)
        view_temp = findViewById(R.id.temp)
        view_desc = findViewById(R.id.desc)

        view_city.text = "";
        view_temp.text = "";
        view_desc.text = "";

        view_weather = findViewById(R.id.weather)
        val search:EditText = findViewById(R.id.editText)
        val search_floating:FloatingActionButton = findViewById(R.id.floating_search)

        search_floating.setOnClickListener {
            val imm:InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.rootView?.windowToken, 0)
            api_key(search.text.toString())
        }
    }

    private fun api_key(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=1b47ad3333bd70d8cda1d025e53a2c33&units=metric"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("brearey", "Call fun api_key")

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("brearey", "Call fun onFailure")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("brearey", "Call fun onResponse")
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    //Log.d("brearey", response.body!!.string())

                    try {
                        val json: JSONObject = JSONObject(response.body!!.string())
                        val array: JSONArray = json.getJSONArray("weather")
                        val jsonObject: JSONObject = array.getJSONObject(0)

                        val description: String = jsonObject.getString("description")
                        val icons: String = jsonObject.getString("icon")

                        val temp1: JSONObject = json.getJSONObject("main")
                        val temperature: Double = temp1.getDouble("temp")

                        setText(view_city, city)
                        val temps: String = "${Math.round(temperature)} °C"
                        setText(view_temp, temps)
                        setText(view_desc, description)
                        setImage(view_weather, icons)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun setText(text:TextView, value:String) {
        runOnUiThread {
            text.setText(value)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setImage(imageView:ImageView, value:String) {
        runOnUiThread {
            when(value) {
                "01d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon1))
                "01n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon1))
                "02d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon2))
                "02n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon2))
                "03d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon3))
                "03n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon3))
                "04d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon4))
                "04n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon4))
                "09d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon5))
                "09n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon5))
                "10d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon6))
                "10n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon6))
                "11d" -> imageView.setImageDrawable(getDrawable(R.drawable.icon7))
                "11n" -> imageView.setImageDrawable(getDrawable(R.drawable.icon7))
                else -> imageView.setImageDrawable(getDrawable(R.drawable.weather))
            }
        }
    }
}