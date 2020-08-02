package com.mitiempo.videollamada.vistaVideollamada.intento_1.ManejadorVideollamada.utlilidadesCamaraRemota

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class EscuchadorSdpObserver : SdpObserver {

    private var EscuchadorOnSetFailure : ((p0: String?)->Unit) ?= null
    fun conEscuchadorOnSetFailure(EscuchadorOnSetFailure : ((p0: String?)->Unit)): EscuchadorSdpObserver {
        this.EscuchadorOnSetFailure = EscuchadorOnSetFailure
        return this
    }
    override fun onSetFailure(p0: String?) {
        EscuchadorOnSetFailure?.invoke(p0)
    }


    private var EscuchadorOnSetSuccess : (()->Unit) ?= null
    fun conEscuchadorOnSetSuccess(EscuchadorOnSetSuccess : (()->Unit)): EscuchadorSdpObserver {
        this.EscuchadorOnSetSuccess = EscuchadorOnSetSuccess
        return this
    }
    override fun onSetSuccess() {
        EscuchadorOnSetSuccess?.invoke()
    }


    private var EscuchadorOnCreateSuccess : ((p0: SessionDescription?)->Unit) ?= null
    fun conEscuchadorOnCreateSuccess(EscuchadorOnCreateSuccess : ((p0: SessionDescription?)->Unit)): EscuchadorSdpObserver {
        this.EscuchadorOnCreateSuccess = EscuchadorOnCreateSuccess
        return this
    }
    override fun onCreateSuccess(p0: SessionDescription?) {
        EscuchadorOnCreateSuccess?.invoke(p0)
    }


    private var EscuchadorOnCreateFailure : ((p0: String?)->Unit) ?= null
    fun conEscuchadorOnCreateFailure(EscuchadorOnCreateFailure : ((p0: String?)->Unit)): EscuchadorSdpObserver {
        this.EscuchadorOnCreateFailure = EscuchadorOnCreateFailure
        return this
    }
    override fun onCreateFailure(p0: String?) {
        EscuchadorOnCreateFailure?.invoke(p0)
    }
}