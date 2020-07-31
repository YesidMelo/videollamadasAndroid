package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.EscuchadorPeerConnectionObserver
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.utlilidadesCamaraRemota.EscuchadorSdpObserver
import org.webrtc.*
import java.lang.IllegalStateException

class ManejadorCamaraRemota
    (
    private val context: Context,
    private val escuchadorPeerConnectionObserver: EscuchadorPeerConnectionObserver,
    private val rutaIceServer : String = "stun:stun.l.google.com:19302"
)
{

    companion object{
        private const val LOCAL_TRACK_ID = "local_track"
    }

    private val rootEglBase = EglBase.create()

    private val peerConnectionFactory by lazy { buildPeerConnectionFactory() }
    private fun buildPeerConnectionFactory() : PeerConnectionFactory{
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
    }

    private fun iniciarPeerConnectionFactory(context: Context){
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)
    }

    fun initSurfaceView(viewRenderer: SurfaceViewRenderer)  = viewRenderer.run {
        setMirror(true)
        setEnableHardwareScaler(true)
        init(rootEglBase.eglBaseContext,null)
    }

    fun iniciarVideoCaptura(videoLocal : SurfaceViewRenderer){
        videoLocal.post {

            val surfaceTextureHelper = SurfaceTextureHelper.create(Thread.currentThread().name,rootEglBase.eglBaseContext)
            (videoCapturer as VideoCapturer).initialize(surfaceTextureHelper,videoLocal.context,localVideoSource.capturerObserver)
            videoCapturer.startCapture(320,240,60)

            val localVideoTrack = peerConnectionFactory.createVideoTrack(LOCAL_TRACK_ID,localVideoSource)
            localVideoTrack.addSink(videoLocal)
        }
    }

}