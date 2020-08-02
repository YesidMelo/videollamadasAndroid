package com.mitiempo.videollamada.vistaVideollamada.intento_1.ManejadorVideollamada.utlilidadesCamaraRemota

import org.webrtc.*

class EscuchadorPeerConnectionObserver : PeerConnection.Observer {

    private var escuchadorOnIceCandidate : ((p0: IceCandidate?)->Unit) ?= null
    fun conEscuchadorOnIceCandidate(escuchadorOnIceCandidate : ((p0: IceCandidate?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.escuchadorOnIceCandidate = escuchadorOnIceCandidate
        return this
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        escuchadorOnIceCandidate?.invoke(p0)
    }

    private var EscuchadorOnDataChannel : ((p0: DataChannel?)->Unit) ?= null
    fun conEscuchadorOnDataChannel(EscuchadorOnDataChannel : ((p0: DataChannel?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnDataChannel = EscuchadorOnDataChannel
        return this
    }

    override fun onDataChannel(p0: DataChannel?) {
        EscuchadorOnDataChannel?.invoke(p0)
    }

    private var EscuchadorOnIceConnectionReceivingChange : ((p0: Boolean?)->Unit) ?= null
    fun conEscuchadorOnIceConnectionReceivingChange(EscuchadorOnIceConnectionReceivingChange : ((p0: Boolean?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnIceConnectionReceivingChange = EscuchadorOnIceConnectionReceivingChange
        return this
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        EscuchadorOnIceConnectionReceivingChange?.invoke(p0)
    }

    private var EscuchadorOnIceConnectionChange : ((p0: PeerConnection.IceConnectionState?)->Unit) ?= null
    fun conEscuchadorOnIceConnectionChange(EscuchadorOnIceConnectionChange : ((p0: PeerConnection.IceConnectionState?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnIceConnectionChange = EscuchadorOnIceConnectionChange
        return this
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        EscuchadorOnIceConnectionChange?.invoke(p0)
    }

    private var EscuchadorOnIceGatheringChange : ((p0: PeerConnection.IceGatheringState?)->Unit) ?= null
    fun conEscuchadorOnIceGatheringChange(EscuchadorOnIceGatheringChange : ((p0: PeerConnection.IceGatheringState?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnIceGatheringChange = EscuchadorOnIceGatheringChange
        return this
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        EscuchadorOnIceGatheringChange?.invoke(p0)
    }

    private var EscuchadorOnAddStream : ((p0: MediaStream?)->Unit) ?= null
    fun conEscuchadorOnAddStream(EscuchadorOnAddStream : ((p0: MediaStream?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnAddStream = EscuchadorOnAddStream
        return this
    }

    override fun onAddStream(p0: MediaStream?) {
        EscuchadorOnAddStream?.invoke(p0)
    }

    private var EscuchadorOnSignalingChange : ((p0: PeerConnection.SignalingState?)->Unit) ?= null
    fun conEscuchadorOnSignalingChange(EscuchadorOnSignalingChange : ((p0: PeerConnection.SignalingState?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnSignalingChange = EscuchadorOnSignalingChange
        return this
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        EscuchadorOnSignalingChange?.invoke(p0)
    }

    private var EscuchadorOnIceCandidatesRemoved : ((p0: Array<out IceCandidate>?)->Unit) ?= null
    fun conEscuchadorOnIceCandidatesRemoved(EscuchadorOnIceCandidatesRemoved : ((p0: Array<out IceCandidate>?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnIceCandidatesRemoved = EscuchadorOnIceCandidatesRemoved
        return this
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        EscuchadorOnIceCandidatesRemoved?.invoke(p0)
    }

    private var EscuchadorOnRemoveStream : ((p0: MediaStream?)->Unit) ?= null
    fun conEscuchadorOnRemoveStream(EscuchadorOnRemoveStream : ((p0: MediaStream?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnRemoveStream = EscuchadorOnRemoveStream
        return this
    }

    override fun onRemoveStream(p0: MediaStream?) {
        EscuchadorOnRemoveStream?.invoke(p0)
    }

    private var EscuchadorOnRenegotiationNeeded : (()->Unit) ?= null
    fun conEscuchadorOnRenegotiationNeeded(EscuchadorOnRenegotiationNeeded : (()->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnRenegotiationNeeded = EscuchadorOnRenegotiationNeeded
        return this
    }

    override fun onRenegotiationNeeded() {
        EscuchadorOnRenegotiationNeeded?.invoke()
    }

    private var EscuchadorOnAddTrack : ((p0: RtpReceiver?, p1: Array<out MediaStream>?)->Unit) ?= null
    fun conEscuchadorOnAddTrack(EscuchadorOnAddTrack : ((p0: RtpReceiver?, p1: Array<out MediaStream>?)->Unit)) : EscuchadorPeerConnectionObserver {
        this.EscuchadorOnAddTrack = EscuchadorOnAddTrack
        return this
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        EscuchadorOnAddTrack?.invoke(p0,p1)
    }
}