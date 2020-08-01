package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import org.webrtc.SurfaceViewRenderer

class ManejadorCamaraRemota1
    (
    private val context: Context,
    private val camara_remota : SurfaceViewRenderer,
    private val rutaIceServer : String = "stun:stun.l.google.com:19302"
)
{
}