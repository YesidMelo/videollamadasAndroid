package com.mitiempo.videollamada.vistaVideollamada.intento_1.ManejadorVideollamada

import android.content.Context
import android.util.Log
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.*
import com.mitiempo.videollamada.vistaVideollamada.intento_1.ManejadorVideollamada.utlilidadesCamaraRemota.*
import org.webrtc.*

class ManejadorVideoCamaras(
    private val context: Context,
    private val camaraLocal : SurfaceViewRenderer,
    private val camaraRemota : SurfaceViewRenderer,
    private val rutaIceCandidate : String = "stun:stun.l.google.com:19302",
    private val urlVideollamada : String,
    private val room : String
) {

    fun iniciarCapturaVideollamada() : ManejadorVideoCamaras {

        iniciarManejadorSocket()
        iniciarManejadorCamaraLocal()
        iniciarManejadorCamaraRemota()

        return this
    }

    private val manejadorSocket =
        SocketVideollamada(
            urlVideollamada
        )
    private val listaRutasIceServices = listOf(PeerConnection.IceServer.builder(rutaIceCandidate).createIceServer())
    private val escuchadorSdpObserver = EscuchadorSdpObserver()
        .conEscuchadorOnCreateSuccess {
            manejadorSocket.enviarSdpARoom(it!!)
        }
    private fun iniciarManejadorSocket(){
        manejadorSocket
            .conEscuchadorFalla { titulo, mensaje ->  }
            .conEscuchadorSdpRemoto {
                Log.e("SesionDescriptionRemoto","llegue aqui");
                peerConnectionRemoto?.setRemoteDescription(escuchadorSdpObserver,it!!)
                peerConnectionRemoto?.answer(EscuchadorSdpObserver())
            }
            .conEscuchadorIceCandidateRemoto {
                peerConnectionRemoto?.addIceCandidate(it!!)
            }
            .iniciarVideoLlamada()
    }

    private val manejadaorCamaraLocal =
        ManejadorCamaraLocal(
            context,
            camaraLocal
        )
    private var peerConnectionLocal : PeerConnection ?= null
    private fun iniciarManejadorCamaraLocal(){

        manejadaorCamaraLocal
            .conEscuchadorPeerConnectionFactory {
                peerConnectionFactory ->
                peerConnectionLocal = peerConnectionFactory?.createPeerConnection(
                    listaRutasIceServices,
                    EscuchadorPeerConnectionObserver()
                        .conEscuchadorOnIceCandidate {
                            peerConnectionLocal?.addIceCandidate(it!!)
                        }
                        .conEscuchadorOnAddStream {
                            it?.videoTracks?.get(0)?.addSink(camaraLocal)
                        }
                )
            }
            .conEscuchadorMediaStreamCamaraLocal {
                peerConnectionLocal?.addStream(it)
            }
            .iniciarVideoCaptura()

    }


    private val manejadorCamaraRemota1 =
        ManejadorCamaraRemota1(
            context,
            camaraRemota,
            rutaIceCandidate
        )
    private var peerConnectionRemoto : PeerConnection ?= null
    private fun iniciarManejadorCamaraRemota(){
        manejadorCamaraRemota1
            .conEscuchadorPeerConnectionFactory {
                peerConnectionFactory ->
                peerConnectionRemoto = peerConnectionFactory?.createPeerConnection(
                    listaRutasIceServices,
                    EscuchadorPeerConnectionObserver()
                        .conEscuchadorOnIceCandidate {
                            peerConnectionRemoto?.addIceCandidate(it!!)
                        }
                        .conEscuchadorOnAddStream {
                            it?.videoTracks?.get(0)?.addSink(camaraRemota)
                        }
                )
            }
            .conEscuchadorMediaStreamCamaraRemota {
                peerConnectionRemoto?.addStream(it)
            }
            .iniciarVideoCaptura()
    }

    fun llamar(){

        peerConnectionRemoto?.call(EscuchadorSdpObserver())

        peerConnectionLocal?.call(escuchadorSdpObserver)

    }

    private fun PeerConnection.call(sdpObserver: EscuchadorSdpObserver){

        val constraints = MediaConstraints().apply { mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")) }
        createOffer(object : SdpObserver by sdpObserver{
            override fun onCreateSuccess(desc: SessionDescription?) {
                setLocalDescription(EscuchadorSdpObserver(),desc)
                sdpObserver.onCreateSuccess(desc)
            }
        },constraints)

    }

    private fun PeerConnection.answer(sdpObserver: EscuchadorSdpObserver){
        val constraints = MediaConstraints().apply { mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")) }
        createAnswer(object : SdpObserver by sdpObserver{
            override fun onCreateSuccess(desc: SessionDescription?) {
                setLocalDescription(EscuchadorSdpObserver(),desc)
                sdpObserver.onCreateSuccess(desc)
            }
        },constraints)
    }

    fun microfono(){}
    fun colgar(){}




}