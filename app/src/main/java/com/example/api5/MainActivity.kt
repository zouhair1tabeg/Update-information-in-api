package com.example.api5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getData()
    }

    override fun onResume() {
        super.onResume()

        getData()
    }

    fun getData() {
        val listView = findViewById<ListView>(R.id.lv)

        // Configurer Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Créer une instance de l'interface ApiService
        val apiService = retrofit.create(ApiService::class.java)

        // Faire un appel réseau pour récupérer les données
        val call = apiService.getCars()
        call.enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    val cars = response.body() ?: emptyList()

                    // Extraire les noms des smartphones en utilisant une boucle for
                    val carNames = mutableListOf<String>()
                    for (c in cars) {
                        carNames.add(c.name+" - "+c.price+ " MAD")
                    }

                    // Utiliser un ArrayAdapter avec simple_list_item_1
                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, carNames)
                    listView.adapter = adapter

                    listView.setOnItemClickListener{parent, view, position, id ->
                        val selectedCar = cars[position]
                        val intent = Intent(this@MainActivity, UpdateActivity::class.java).also {
                            it.putExtra("name", selectedCar.name)
                            it.putExtra("prix", selectedCar.price)
                            it.putExtra("image", selectedCar.image)
                            it.putExtra("check", selectedCar.isFullOptions)
                            it.putExtra("id", selectedCar.id)
                        }
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Échec de la connexion à l'API", Toast.LENGTH_SHORT).show()
            }
        })


//        Ajouter Cars

        // Initialisation de Retrofit
        val retrofit2 = Retrofit.Builder()
            .baseUrl("https://apiyes.net/") // Remplacez par votre URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService2 = retrofit2.create(ApiService::class.java)

        val editTextName = findViewById<EditText>(R.id.editText1)
        val editTextPrice = findViewById<EditText>(R.id.editText2)
        val editTextImageUrl = findViewById<EditText>(R.id.editText3)
        val fullOption = findViewById<CheckBox>(R.id.checkBox)
        val buttonAddCar = findViewById<Button>(R.id.buttonAdd)

        buttonAddCar.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val priceStr = editTextPrice.text.toString().trim()
            val imageUrl = editTextImageUrl.text.toString().trim()
            val isFullOptions = fullOption.isChecked // Get checkbox value

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val price = priceStr.toDouble()

                // Create a Car object to send
                val car = Car(0, name, price, isFullOptions, imageUrl)

                // Add the car via API
                apiService2.addCar(car).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(
                        call: Call<AddResponse>,
                        response: Response<AddResponse>
                    ) {
                        if (response.isSuccessful) {
                            val addResponse = response.body()
                            if (addResponse != null) {
                                Toast.makeText(applicationContext, addResponse.status_message, Toast.LENGTH_LONG).show()
                                if (addResponse.status == 1) {
                                    getData() // Close activity or reset fields
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Failed to add car", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }


    }

}