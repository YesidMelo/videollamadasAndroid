package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota

import android.content.Context
import org.webrtc.*
import java.lang.IllegalStateException

class ManejadorCamaraRemota1
    (
    private val context: Context,
    private val camara_remota : SurfaceViewRenderer,
    private val rutaIceServer : String = "stun:stun.l.google.com:19302"
)
{

    companion object{
        private const val LOCAL_TRACK_ID = "local_track"
        private const val LOCAL_SREAM_ID = "local_track"
    }

    private val rootEglBase = EglBase.create()

    private val peerConnectionFactory by lazy { buildPeerConnectionFactory() }
    private fun buildPeerConnectionFactory() : PeerConnectionFactory {
        return PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(rootEglBase.eglBaseContext,true,true))
            .setOptions(PeerConnectionFactory.Options().apply{
                disableEncryption = true
                disableNetworkMonitor = true
            })
            .createPeerConnectionFactory()
    }

    private val videoCapturer by lazy { traerVideoCapturer(context) }
    private fun traerVideoCapturer(context: Context) = Camera2Enumerator(context)
        .run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it,null )
            }?: throw IllegalStateException()
        }

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }

    init {
        iniciarPeerConnectionFactory(context)
        inicializarSurfaceView(camara_remota)
    }

    private fun iniciarPeerConnectionFactory(context: Context){
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)
    }

    private fun inicializarSurfaceView(viewRenderer: SurfaceViewRenderer)  = viewRenderer.run {
//        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext,null)
    }

    fun iniciarVideoCaptura(){
        camara_remota.post {

            val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name,rootEglBase.eglBaseContext)
            (videoCapturer as VideoCapturer).initialize(surfaceTextureHelper,camara_remota.context,localVideoSource.capturerObserver)
            videoCapturer.startCapture(320,240,60)

            val localVideoTrack = peerConnectionFactory.createVideoTrack(LOCAL_TRACK_ID,localVideoSource)
            localVideoTrack.addSink(camara_remota)

            val localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_SREAM_ID)
            localStream.addTrack(localVideoTrack)

            escuchadorMediaStreamCamaraLocal?.invoke(localStream)
        }
    }

    private var escuchadorMediaStreamCamaraLocal : ((MediaStream)->Unit) ?= null
    fun conEscuchadorMediaStreamCamaraRemota(escuchadorMediaStreamCamaraLocal : ((MediaStream)->Unit)) : ManejadorCamaraRemota1 {
        this.escuchadorMediaStreamCamaraLocal = escuchadorMediaStreamCamaraLocal
        return this
    }

}