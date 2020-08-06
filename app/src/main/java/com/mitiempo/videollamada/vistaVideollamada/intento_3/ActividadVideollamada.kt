package com.mitiempo.videollamada.vistaVideollamada.intento_3

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mitiempo.videollamada.R
import com.mitiempo.videollamada.databinding.ActivitySamplePeerConnectionBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.Socket.EVENT_CONNECT
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class ActividadVideollamada : AppCompatActivity() {

    companion object{

        private const val TAG = "ActividadVideollamada"
        private const val RC_CALL = 111
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val VIDEO_RESOLUTION_WIDTH  = 1280
        private const val VIDEO_RESOLUTION_HEIGTH = 720
        private const val FPS = 30

    }




    private var isStarted = false

    private var audioConstraints : MediaConstraints ?= null
    private var AudioSource : AudioSource ?= null
    private var localAudioTrack : AudioTrack ?= null

    private var binding : ActivitySamplePeerConnectionBinding ?= null
    private var peerConnection : PeerConnection ?= null


    private var videoTrackFromCamera : VideoTrack ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sample_peer_connection)
        start()
    }

    @AfterPermissionGranted(RC_CALL)
    private fun start(){
        if(!tengoLosPermisos()){
            EasyPermissions.requestPermissions(this,getString(R.string.traer_permisos), RC_CALL,Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            return
        }

        connectToSignallingServer()
        initializeSurfaceViews()
        initializePeerConnectionFactory()

    }


    private fun tengoLosPermisos() : Boolean{
        return EasyPermissions.hasPermissions(this,Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }
//region socket
    private var socket : Socket ?= null
    private val hostingVideollamada = "http://192.168.0.3:3000/"
    private val crearOUnirHabitacion = "create or join"
    private val habitacion = "foo"
    private val ipaddr = "ipaddr"
    private val created = "created"
    private var isInitiator = false
    private val full = "full"
    private val join = "join"
    private var isChannelReady = false
    private val joined = "joined"
    private val log = "log"
    private val message = "message"
    private val type = "type"
    private val offer = "offer"
    private val answer = "answer"
    private val sdp = "sdp"
    private val candidate = "candidate"
    private val id = "id"
    private val label  = "label"
    private val disconnect = "disconnect"
    private fun connectToSignallingServer(){

        try {
            socket = IO.socket(hostingVideollamada)

            socket?.on(EVENT_CONNECT){
                    args ->
                imprimirEnConsolaConnectSignallingServer(EVENT_CONNECT)
                socket?.emit(crearOUnirHabitacion,habitacion)
            }?.on(ipaddr){imprimirEnConsolaConnectSignallingServer(ipaddr)}
                ?.on(created){
                    imprimirEnConsolaConnectSignallingServer(created)
                    isInitiator = true
                }?.on(full){
                    imprimirEnConsolaConnectSignallingServer(full)
                }?.on(join){
                    imprimirEnConsolaConnectSignallingServer(join)
                        .imprimirEnConsolaConnectSignallingServer("Another peer made a request to join room ")
                        .imprimirEnConsolaConnectSignallingServer("this peer is initiator of room")
                    isChannelReady = true
                }?.on(joined){
                    imprimirEnConsolaConnectSignallingServer(joined)
                    isChannelReady = true
                }?.on(log){
                        args-> args.forEach {
                    imprimirEnConsolaConnectSignallingServer(" $it ")
                }
                }?.on(message){
                    imprimirEnConsolaConnectSignallingServer(message)
                }?.on(message){
                        args ->
                    try {

                        if(args[0] is String){
                            if(args[0].equals("got user media")){
                                maybeStart()
                            }

                        }else{

                            val mensaje = args[0] as JSONObject
                            imprimirEnConsolaConnectSignallingServer(message)

                            if(mensaje.getString(type).equals(offer)){

                                imprimirEnConsolaConnectSignallingServer(" $isInitiator $isStarted")
                                if(!isInitiator && !isStarted){
                                    maybeStart()
                                }

                                peerConnection?.setRemoteDescription(SimpleSdpObserver(),SessionDescription(SessionDescription.Type.OFFER,mensaje.getString(sdp)))
                                doAnswer()

                            }else if(mensaje.getString(type).equals(answer)){

                                peerConnection?.setRemoteDescription(SimpleSdpObserver(),SessionDescription(SessionDescription.Type.ANSWER,mensaje.getString(sdp)))

                            }else if(mensaje.getString(type).equals(candidate) && isStarted){

                                imprimirEnConsolaConnectSignallingServer("receiving candidates")
                                val candidate = IceCandidate(mensaje.getString(id),mensaje.getInt(label),mensaje.getString(candidate))
                                peerConnection?.addIceCandidate(candidate)

                            }

                        }
                    }catch (e :Exception){
                        e.printStackTrace()
                    }
                }?.on(EVENT_CONNECT){
                    imprimirEnConsolaConnectSignallingServer(disconnect)
                }
            socket?.connect()

        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun imprimirEnConsolaConnectSignallingServer(mensaje : String) : ActividadVideollamada{
        Log.d(TAG,"connectToSignallingServer : $mensaje")
        return this
    }

    private fun maybeStart(){
        Log.d(TAG, "maybeStart $isStarted $isChannelReady")
        if(!isStarted && isChannelReady){
            isStarted = true
            if(isInitiator){
                doCall()
            }
        }
    }

    private val OfferToReceiveAudio = "OfferToReceiveAudio"
    private val OfferToReceiveVideo = "OfferToReceiveVideo"
    private val _true = "true"
    private fun doCall(){

        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair(OfferToReceiveAudio,_true))
        sdpMediaConstraints.mandatory.add(MediaConstraints.KeyValuePair(OfferToReceiveVideo,_true))

        peerConnection?.createOffer(
            SimpleSdpObserver()
                .conEscuchadorOnCreateSuccess {
                    sessionDescription ->

                    peerConnection?.setLocalDescription(SimpleSdpObserver(),sessionDescription)
                    val mensaje = JSONObject()

                    try{
                        mensaje.put(type,offer)
                        mensaje.put(sdp,sessionDescription!!.description)
                        sendMessage(mensaje)
                    }catch (e : JSONException){
                        e.printStackTrace()
                    }

        },sdpMediaConstraints)
    }

    private fun sendMessage(mensajeAEnviar  : JSONObject){
        socket?.emit(message,mensajeAEnviar)
    }

    private fun doAnswer(){
        peerConnection?.createAnswer(SimpleSdpObserver().conEscuchadorOnCreateSuccess {
            sessionDescription ->

            peerConnection?.setLocalDescription(SimpleSdpObserver(),sessionDescription)

            val mensaje = JSONObject()
            mensaje.put(type,answer)
            mensaje.put(sdp,sessionDescription!!.description)
            sendMessage(mensaje)

        }, MediaConstraints())
    }
//endregion

    private var rootEglBase : EglBase ?= null
    private fun initializeSurfaceViews(){

        rootEglBase = EglBase.create()
        binding!!.surfaceView.init(rootEglBase!!.eglBaseContext,null )
        binding!!.surfaceView.setEnableHardwareScaler(true)
        binding!!.surfaceView.setMirror(true)

        binding!!.surfaceView2.init(rootEglBase!!.eglBaseContext,null )
        binding!!.surfaceView2.setEnableHardwareScaler(true)
        binding!!.surfaceView2.setMirror(true)


    }

    private var factory  : PeerConnectionFactory ?= null
    private fun initializePeerConnectionFactory(){

        PeerConnectionFactory.initializeAndroidGlobals(this,true,true,true)
        factory = PeerConnectionFactory(null )
        factory?.setVideoHwAccelerationOptions(rootEglBase?.eglBaseContext,rootEglBase?.eglBaseContext)


    }


}
