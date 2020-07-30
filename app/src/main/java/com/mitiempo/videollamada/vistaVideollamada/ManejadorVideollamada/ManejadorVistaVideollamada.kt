package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.content.Context
import org.webrtc.SurfaceViewRenderer

class ManejadorVistaVideollamada(
    context: Context,
    camaraLocal : SurfaceViewRenderer,
    camaraRemota : SurfaceViewRenderer

) {
    private var URL : String ?= null
    fun conURL(URL : String): ManejadorVistaVideollamada{
        this.URL = URL
        return this
    }

    private var Room : String ?= null
    fun conRoom(Room : String): ManejadorVistaVideollamada{
        this.Room = Room
        return this
    }


    fun iniciarVideoLlamada(): ManejadorVistaVideollamada{
        return this
    }

}