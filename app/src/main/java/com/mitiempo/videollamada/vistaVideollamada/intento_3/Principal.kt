package com.mitiempo.videollamada.vistaVideollamada.intento_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mitiempo.videollamada.R
import com.mitiempo.videollamada.vistaVideollamada.intento_2.CompleteActivity
import kotlinx.android.synthetic.main.activity_principal.*

class Principal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        boton_elementos.setOnClickListener {
            val intent = Intent(this, CompleteActivity::class.java)

            val tmp = CompleteActivity.ElementosDeConfiguracion()
            tmp.URL = "http://192.168.0.3:3000/"
            tmp.Sala = "otra"
            tmp.iceServer = "stun:stun.l.google.com:19302"

            intent.putExtra(CompleteActivity.ConfiguracionVideollamada,tmp)

            startActivity(intent)
        }
    }
}
