package com.mitiempo.videollamada.vistaVideollamada.intento_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mitiempo.videollamada.R
import kotlinx.android.synthetic.main.activity_main.*
//codigo basado en https://github.com/amrfarid140/webrtc-android-codelab/tree/step/local-video
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videollamada
            .conURLVideollamada("http://192.168.0.3:3000")
            .conRoom("Paciente_psicologo")
            .iniciarVista()
    }

    override fun onStart() {
        videollamada.conOnStart()
        super.onStart()
    }

    override fun onResume() {
        videollamada.conOnResumen()
        super.onResume()
    }

    override fun onPause() {
        videollamada.conOnPause()
        super.onPause()
    }

    override fun onStop() {
        videollamada.conOnStop()
        super.onStop()
    }

    override fun onDestroy() {
        videollamada.conOnDestroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        videollamada.conOnRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
