package com.mitiempo.videollamada.vistaVideollamada

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mitiempo.videollamada.R
import com.mitiempo.videollamada.vistaVideollamada.ManejadorPermisos.ManejadorPermisosCamara
import com.mitiempo.videollamada.vistaVideollamada.ManejadorPermisos.ManejadorPermisosMicrofono
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.ManejadorCamaraLocal
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.ManejadorCamaraRemota1
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.SocketVideollamada
import kotlinx.android.synthetic.main.visualizador_vista_videollamada.view.*

class Videollamada @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var room : String = "usuario_1_usuario2"
    fun conRoom(room : String) : Videollamada{
        this.room = room
        return this
    }

    private var urlVideollamada : String = "http://192.168.0.3:3000"
    fun conURLVideollamada(urlVideollamada : String ) : Videollamada{
        this.urlVideollamada = urlVideollamada
        return this
    }

    private var servidorIceCandidate : String = "stun:stun.l.google.com:19302"
    fun conRutaIceCandidate (servidorIceCandidate : String) : Videollamada{
        this.servidorIceCandidate = servidorIceCandidate
        return this
    }

    private var escuchadorBotonColgar : (()->Unit) ?= null
    fun conEscuchadorBotonColgar(escuchadorBotonColgar : (()->Unit)) : Videollamada{
        this.escuchadorBotonColgar = escuchadorBotonColgar
        return this
    }

    private var escuchadorBotonCamara : (()->Unit) ?= null
    fun conEscuchadorBotonCamara(escuchadorBotonCamara : (()->Unit)) : Videollamada{
        this.escuchadorBotonCamara = escuchadorBotonCamara
        return this
    }

    private var escuchadorBotonMicrofono : (()->Unit) ?= null
    fun conEscuchadorBotonMicrofono(escuchadorBotonMicrofono : (()->Unit)) : Videollamada{
        this.escuchadorBotonMicrofono = escuchadorBotonMicrofono
        return this
    }

    fun conOnRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) : Videollamada{

        ManejadorPermisosCamara.getInstancia().conOnRequestPermissionsResult(requestCode, permissions, grantResults)
        ManejadorPermisosMicrofono.getInstancia().conOnRequestPermissionsResult(requestCode, permissions, grantResults)

        return this
    }

    fun conOnStart():Videollamada{ return this }
    fun conOnResumen() : Videollamada{ return this }
    fun conOnPause() : Videollamada{ return this }
    fun conOnStop() : Videollamada{ return this }
    fun conOnDestroy() : Videollamada { return this }

    fun iniciarVista() : Videollamada{
        post {

            ponerEscuchadoresManejadorVideollamada()
            verificarPermisosCamara()
            verificarPermisosMicrofono()
            inicializarSocketVideollamada()
//            iniciarCapturaCamaraLocal()
            iniciarCapturaCamaraRemota()

        }
        return this
    }

    private fun ponerEscuchadoresManejadorVideollamada(){

        menuManejadorVideollamada
            .conEscuchadorLlamar {}
            .conEscuchadorColgar {}
            .conEscuchadorMicrofono {}

    }

    private fun verificarPermisosCamara(){
        ManejadorPermisosCamara
            .getInstancia()
            .conContexto(context)
            .conEscuchadorRespuestaPositivaDialogo {}
            .conEscuhadorRespuestaNegativaDialogo {}
            .conEscuchadorMensajeSolicitarPermisos{ titulo,mensaje,respuestaPositiva,respuestaNegativa -> respuestaPositiva?.invoke() }
            .verificarPermisos()

    }

    private fun verificarPermisosMicrofono(){
        ManejadorPermisosMicrofono
            .getInstancia()
            .conContexto(context)
            .conEscuchadorRespuestaPositivaDialogo {}
            .conEscuhadorRespuestaNegativaDialogo {}
            .conEscuchadorMensajeSolicitarPermisos{titulo,mensaje,respuestaPositiva,respuestaNegativa ->respuestaPositiva?.invoke()}
            .verificarPermisos()
    }


    private var manejadorSocket = SocketVideollamada(urlVideollamada)
    private fun inicializarSocketVideollamada(){
        manejadorSocket
            .conEscuchadorFalla { titulo, mensaje -> }
            .conEscuchadorSdpRemoto {}
            .conEscuchadorIceCandidateRemoto {}
            .iniciarVideoLlamada()
    }

    private var manejadorCamaraLocal : ManejadorCamaraLocal ?= null
    private fun iniciarCapturaCamaraLocal(){
        if(manejadorCamaraLocal == null ){
            manejadorCamaraLocal = ManejadorCamaraLocal(context,camara_local)
        }

        manejadorCamaraLocal
            ?.conEscuchadorMediaStreamCamaraLocal {
                Log.e("Escuchador","Media stream local :)");
            }
            ?.iniciarVideoCaptura()
    }

    private var manejadorCamaraRemota : ManejadorCamaraRemota1 ?= null
    private fun iniciarCapturaCamaraRemota(){
        if(manejadorCamaraRemota == null ){
            manejadorCamaraRemota = ManejadorCamaraRemota1(context,camara_remota,servidorIceCandidate)
        }

        manejadorCamaraRemota
            ?.conEscuchadorMediaStreamCamaraRemota {

            }
            ?.iniciarVideoCaptura()

    }





    init {
        LayoutInflater.from(context).inflate(R.layout.visualizador_vista_videollamada,this,true)
    }


}