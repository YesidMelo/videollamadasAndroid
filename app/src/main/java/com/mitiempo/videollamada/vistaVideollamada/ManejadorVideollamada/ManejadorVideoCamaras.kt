package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.ManejadorCamaraLocal
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.ManejadorCamaraRemota1
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.SocketVideollamada
import org.webrtc.SurfaceViewRenderer

class ManejadorVideoCamaras(
    private val context: Context,
    private val camaraLocal : SurfaceViewRenderer,
    private val camaraRemota : SurfaceViewRenderer,
    private val rutaIceCandidate : String = "stun:stun.l.google.com:19302",
    private val urlVideollamada : String,
    private val room : String
) {


    private val manejadorSocket =SocketVideollamada(urlVideollamada)
    private fun iniciarManejadorSocket(){
        manejadorSocket
            .conEscuchadorFalla { titulo, mensaje ->  }
            .conEscuchadorSdpRemoto {  }
            .conEscuchadorIceCandidateRemoto {  }
            .iniciarVideoLlamada()
    }

    private val manejadaorCamaraLocal =
        ManejadorCamaraLocal(
            context,
            camaraLocal
        )
    private fun iniciarManejadorCamaraLocal(){
        manejadaorCamaraLocal
            .conEscuchadorMediaStreamCamaraLocal {

            }
            .iniciarVideoCaptura()
    }

    private val manejadorCamaraRemota1 =
        ManejadorCamaraRemota1(
            context,
            camaraRemota,
            rutaIceCandidate
        )
    private fun iniciarManejadorCamaraRemota(){
        manejadorCamaraRemota1
            .conEscuchadorMediaStreamCamaraRemota {

            }
            .iniciarVideoCaptura()
    }

    fun iniciarCapturaVideollamada() : ManejadorVideoCamaras{

        iniciarManejadorSocket()
        iniciarManejadorCamaraLocal()
        iniciarManejadorCamaraRemota()

        return this
    }
}