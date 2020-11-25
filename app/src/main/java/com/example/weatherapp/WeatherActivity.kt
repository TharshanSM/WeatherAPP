package com.example.weatherapp

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_weather.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        verifyLogin()

        btnSearchLocaton.setOnClickListener(){
            val city=txtLocationSearch.text.toString()
            val api:String="dc1f47de7f5aed259f9290e964543146"
            WeatherTask(city,api).execute()
        }

        btnOK.setOnClickListener(){
            finish()
            val intent=Intent(this,WeatherActivity::class.java)
            startActivity(intent)
        }
    }





//    api execute
    inner class WeatherTask(var city:String,var api:String ) : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            conDetails.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String {
            var response: String
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$api")
                        .readText(Charsets.UTF_8)
                Log.d("MainActivity","Get HTTP Request Successfully")
            } catch (e: Exception) {
                response = "";
                Log.d("MainActivity","HTTP Request Failed ${e.message}")
                Toast.makeText(this@WeatherActivity,"Message",Toast.LENGTH_LONG)
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {

                //declare json objects to val
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys=jsonObj.getJSONObject("sys")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)

                val country=sys.getString("country")
                val city=jsonObj.getString("name")
                val temp = main.getString("temp") + " \u2103"
                val status=weather.getString("description")
                val minTemp=main.getString("temp_min") + " \u2103"
                val maxTemp=main.getString("temp_max") + " \u2103"
                val sunrise:Long=sys.getLong("sunrise")
                val sunset:Long=sys.getLong("sunset")
                //Log.d("Weather","Get Details Successfully")

                //assign obj to elements
                lblCountry.text=country
                lblCity.text=city
                lblTemperature.text=temp
                lblStatus.text=status
                lblMin_Temp.text=minTemp
                lblMax_Temp.text=maxTemp
                lblSunrise.text=SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                lblSunset.text=SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))

                //conSearch.visibility=View.GONE
                conDetails.visibility = View.VISIBLE
            } catch (e: Exception) {
                conDetails.visibility = View.GONE
                Toast.makeText(this@WeatherActivity, "${e.message}", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }


    private fun verifyLogin(){
        val uid=FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent=Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        FirebaseAuth.getInstance().signOut()
        val intent=Intent(this,MainActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}