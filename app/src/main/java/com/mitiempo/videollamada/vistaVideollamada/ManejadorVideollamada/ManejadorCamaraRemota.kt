package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.EscuchadorPeerConnectionObserver
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.EscuchadorSdpObserver
import org.webrtc.*

class ManejadorCamaraRemota
    (
    private val context: Context,
    private val escuchadorPeerConnectionObserver: EscuchadorPeerConnectionObserver,
    private val rutaIceServer : String = "stun:stun.l.google.com:19302"
){

    companion object{
        private const val LOCAL_TRACK_ID = "local_track"
        private const val LOCAL_STREAM_ID = "local_track"
    }

    private val rootEglBase = EglBase.create()

    private val peerConnectionFactory by lazy{ buildPeerConnectionFactory() }
    private fun buildPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(rootEglBase.eglBaseContext,true,true))
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = true
                disableNetworkMonitor = true
            })
            .createPeerConnectionFactory()
    }

    private val videoCapturer by lazy { getVideoCapturer(context) }
    private fun getVideoCapturer(context : Context)= Camera2Enumerator(context).run {
        deviceNames.find {
            isFrontFacing(it)
        }?.let {
            createCapturer(it,null)
        }?: throw IllegalStateException()
    }

    private val iceServer = listOf(PeerConnection.IceServer.builder(rutaIceServer).createIceServer())

    private val localVideoSource by lazy{ peerConnectionFactory.createVideoSource(false) }

    private val peerConnection by lazy { buildPeerConnection() }

    private fun buildPeerConnection() = peerConnectionFactory.createPeerConnection(iceServer,escuchadorPeerConnectionObserver)

    //funciones de comunicacion
    fun initSurfaceView(video : SurfaceViewRenderer) = video .run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext,null )
    }

    fun startLocalVideoCapture(localVideoOutput : SurfaceViewRenderer){

        val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name, rootEglBase.eglBaseContext)
        (videoCapturer as VideoCapturer).initialize(surfaceTextureHelper,localVideoOutput.context,localVideoSource.capturerObserver)
        videoCapturer.startCapture(320,240,60)

        //crea escuchador de stream
        val localVideoTrack = peerConnectionFactory.createVideoTrack(LOCAL_TRACK_ID,localVideoSource)
        localVideoTrack.addSink(localVideoOutput)

        //crea local stream
        val localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID)
        localStream.addTrack(localVideoTrack)
        peerConnection?.addStream(localStream)

    }


    fun call (sdpObserver: EscuchadorSdpObserver) = peerConnection?.call(sdpObserver)

    private fun PeerConnection.call(escuchadorSdpObserver: EscuchadorSdpObserver){

        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }

        createOffer(object : SdpObserver by escuchadorSdpObserver{

            override fun onCreateSuccess(p0: SessionDescription?) {
                setLocalDescription(EscuchadorSdpObserver(),p0)
                escuchadorSdpObserver.onCreateSuccess(p0)
            }

        },constraints)

    }

    fun answer(escuchadorSdpObserver: EscuchadorSdpObserver) = peerConnection?.answer(escuchadorSdpObserver)

    private fun PeerConnection.answer(escuchadorSdpObserver: EscuchadorSdpObserver){

        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }

        createAnswer(
            object : SdpObserver by escuchadorSdpObserver{
                override fun onCreateSuccess(p0: SessionDescription?) {
                    setLocalDescription(EscuchadorSdpObserver(),p0)
                    escuchadorSdpObserver.onCreateSuccess(p0)
                }
            }
            ,constraints
        )

    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription){
        peerConnection?.setRemoteDescription(EscuchadorSdpObserver(),sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate?){
        peerConnection?.addIceCandidate(iceCandidate)
    }

}