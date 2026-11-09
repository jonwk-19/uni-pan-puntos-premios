package com.panpuntos.app

import android.os.Bundle
import android.widget.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    private val urlBase =
        "http://10.0.2.2:8080/pan-puntos/api/puntos"

    private val urlReportes =
        "http://10.0.2.2:8080/pan-puntos/api/reportes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 80, 60, 60)
        }

        val scroll = ScrollView(this)
        scroll.addView(layout)

        val tvTitulo = TextView(this).apply {
            text = "Pan, Puntos y Premios"
            textSize = 25f
            setPadding(0, 0, 0, 35)
        }

        val etDocumento = EditText(this).apply {
            hint = "Ingrese su DPI / Documento"
        }

        val btnConsultar = Button(this).apply {
            text = "Consultar Puntos"
        }

        val tvResultado = TextView(this).apply {
            textSize = 17f
            setPadding(0, 30, 0, 40)
        }

        btnConsultar.setOnClickListener {

            val documento = etDocumento.text.toString().trim()

            if (documento.isNotEmpty()) {
                consultarPuntos(documento, tvResultado)
            } else {
                tvResultado.text =
                    "Por favor, ingrese un documento."
            }
        }

        val tvReportes = TextView(this).apply {
            text = "REPORTES"
            textSize = 22f
            setPadding(0, 40, 0, 20)
        }

        layout.addView(tvTitulo)
        layout.addView(etDocumento)
        layout.addView(btnConsultar)
        layout.addView(tvResultado)
        layout.addView(tvReportes)

        agregarBotonReporte(
            layout,
            "Ventas por período",
            "ventas_periodo",
            tvResultado
        )

        agregarBotonReporte(
            layout,
            "Productos más vendidos",
            "productos_vendidos",
            tvResultado
        )

        agregarBotonReporte(
            layout,
            "Productos con bajo stock",
            "bajo_stock",
            tvResultado
        )

        agregarBotonReporte(
            layout,
            "Puntos por cliente",
            "puntos_cliente",
            tvResultado
        )

        agregarBotonReporte(
            layout,
            "Historial de canjes",
            "canjes",
            tvResultado
        )

        agregarBotonReporte(
            layout,
            "Ventas de menús",
            "menus_vendidos",
            tvResultado
        )

        setContentView(scroll)
    }

    private fun consultarPuntos(
        documento: String,
        tvResultado: TextView
    ) {

        tvResultado.text = "Consultando puntos..."

        val request = Request.Builder()
            .url("$urlBase?documento=$documento")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    tvResultado.text =
                        "Error de conexión: ${e.message}"
                }
            }

            override fun onResponse(
                call: Call,
                response: Response
            ) {

                val body = response.body?.string()

                runOnUiThread {

                    if (response.isSuccessful && body != null) {

                        try {

                            val json = JSONObject(body)

                            tvResultado.text =
                                "Cliente: ${json.getString("nombre")}\n" +
                                "Puntos: ${json.getInt("puntos")}"

                        } catch (e: Exception) {

                            tvResultado.text =
                                "No se pudo interpretar la respuesta."
                        }

                    } else {

                        tvResultado.text =
                            "Cliente no encontrado o inactivo."
                    }
                }
            }
        })
    }

    private fun agregarBotonReporte(
        layout: LinearLayout,
        titulo: String,
        tipo: String,
        tvResultado: TextView
    ) {

        val boton = Button(this).apply {
            text = titulo
            isAllCaps = false

            setOnClickListener {
                consultarReporte(tipo, tvResultado)
            }
        }

        layout.addView(boton)
    }

    private fun consultarReporte(
        tipo: String,
        tvResultado: TextView
    ) {

        tvResultado.text = "Consultando reporte..."

        val request = Request.Builder()
            .url("$urlReportes/$tipo")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    tvResultado.text =
                        "Error de conexión: ${e.message}"
                }
            }

            override fun onResponse(
                call: Call,
                response: Response
            ) {

                val body = response.body?.string()

                runOnUiThread {

                    if (response.isSuccessful && body != null) {

                        try {

                            val json = JSONObject(body)

                            val resultado = StringBuilder()

                            resultado.append(
                                json.getString("titulo")
                            ).append("\n\n")

                            val encabezados =
                                json.getJSONArray("encabezados")

                            val filas =
                                json.getJSONArray("filas")

                            for (i in 0 until encabezados.length()) {
                                resultado.append(
                                    encabezados.getString(i)
                                )

                                if (i < encabezados.length() - 1) {
                                    resultado.append(" | ")
                                }
                            }

                            resultado.append("\n\n")

                            if (filas.length() == 0) {

                                resultado.append(
                                    "No hay registros disponibles."
                                )

                            } else {

                                for (i in 0 until filas.length()) {

                                    val fila = filas.getJSONArray(i)

                                    for (j in 0 until fila.length()) {

                                        resultado.append(
                                            fila.getString(j)
                                        )

                                        if (j < fila.length() - 1) {
                                            resultado.append(" | ")
                                        }
                                    }

                                    resultado.append("\n")
                                }
                            }

                            tvResultado.text = resultado.toString()

                        } catch (e: Exception) {

                            tvResultado.text =
                                "Error al mostrar el reporte: ${e.message}"
                        }

                    } else {

                        tvResultado.text =
                            "No se pudo consultar el reporte."
                    }
                }
            }
        })
    }
}
