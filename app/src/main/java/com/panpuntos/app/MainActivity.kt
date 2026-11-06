package com.panpuntos.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val urlBase = "http://localhost:8080/api/puntos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 100, 60, 60)
        }

        val tvTitulo = TextView(this).apply {
            text = "Pan, Puntos y Premios"; textSize = 24f
            setPadding(0, 0, 0, 40)
        }
        val etDocumento = EditText(this).apply {
            hint = "Ingrese su DPI / Documento"
            setPadding(0, 20, 0, 20)
        }
        val btnConsultar = Button(this).apply {
            text = "Consultar Puntos"
        }
        val tvResultado = TextView(this).apply {
            textSize = 18f; setPadding(0, 50, 0, 0)
        }

        btnConsultar.setOnClickListener {
            val doc = etDocumento.text.toString().trim()
            if (doc.isNotEmpty()) {
                consultarPuntos(doc, tvResultado)
            } else {
                tvResultado.text = "Por favor, ingrese un documento."
            }
        }

        layout.addView(tvTitulo)
        layout.addView(etDocumento)
        layout.addView(btnConsultar)
        layout.addView(tvResultado)
        setContentView(layout)
    }

    private fun consultarPuntos(documento: String, tvResultado: TextView) {
        tvResultado.text = "Consultando..."
        val request = Request.Builder().url("$urlBase?documento=$documento").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { tvResultado.text = "Error de conexión: ${e.message}" }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        tvResultado.text = "Cliente: ${json.getString("nombre")}\nPuntos: ${json.getInt("puntos")}"
                    } else {
                        tvResultado.text = "Cliente no encontrado o inactivo."
                    }
                }
            }
        })
    }
}