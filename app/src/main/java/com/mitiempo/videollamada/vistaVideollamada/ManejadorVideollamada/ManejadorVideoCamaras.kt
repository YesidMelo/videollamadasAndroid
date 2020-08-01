package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.*
import org.webrtc.*

class ManejadorVideoCamaras(
    private val context: Context,
    private val camaraLocal : SurfaceViewRenderer,
    private val camaraRemota : SurfaceViewRenderer,
    private val rutaIceCandidate : String = "stun:stun.l.google.com:19302",
    private val urlVideollamada : String,
    private val room : String
) {

    fun iniciarCapturaVideollamada() : ManejadorVideoCamaras{

        iniciarManejadorSocket()
        iniciarManejadorCamaraLocal()
        iniciarManejadorCamaraRemota()

        return this
    }

    private val manejadorSocket =SocketVideollamada(urlVideollamada)
    private val listaRutasIceServices = listOf(PeerConnection.IceServer.builder(rutaIceCandidate).createIceServer())
    private fun iniciarManejadorSocket(){
        manejadorSocket
            .conEscuchadorFalla { titulo, mensaje ->  }
            .conEscuchadorSdpRemoto {  }
            .conEscuchadorIceCandidateRemoto {  }
            .iniciarVideoLlamada()
    }

    private val manejadaorCamaraLocal = ManejadorCamaraLocal(context,camaraLocal)
    private var peerConnectionLocal : PeerConnection ?= null
    private fun iniciarManejadorCamaraLocal(){

        manejadaorCamaraLocal
            .conEscuchadorPeerConnectionFactory {
                peerConnectionFactory ->
                peerConnectionLocal = peerConnectionFactory?.createPeerConnection(
                    listaRutasIceServices,
                    EscuchadorPeerConnectionObserver()
                )
            }
            .conEscuchadorMediaStreamCamaraLocal {
                peerConnectionLocal?.addStream(it)
            }
            .iniciarVideoCaptura()

    }


    private val manejadorCamaraRemota1 = ManejadorCamaraRemota1( context, camaraRemota, rutaIceCandidate )
    private var peerConnectionRemoto : PeerConnection ?= null
    private fun iniciarManejadorCamaraRemota(){
        manejadorCamaraRemota1
            .conEscuchadorPeerConnectionFactory {
                peerConnectionFactory ->
                peerConnectionRemoto = peerConnectionFactory?.createPeerConnection(
                    listaRutasIceServices,
                    EscuchadorPeerConnectionObserver()
                )
            }
            .conEscuchadorMediaStreamCamaraRemota {
                peerConnectionRemoto?.addStream(it)
            }
            .iniciarVideoCaptura()
    }

    fun llamar(){}

    private fun PeerConnection.call(sdpObserver: EscuchadorSdpObserver){

        val constraints = MediaConstraints().apply { mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")) }
        createOffer(object : SdpObserver by sdpObserver{
            override fun onCreateSuccess(desc: SessionDescription?) {
                setLocalDescription(EscuchadorSdpObserver(),desc)
            }
        },constraints)

    }

    fun microfono(){}
    fun colgar(){}




}