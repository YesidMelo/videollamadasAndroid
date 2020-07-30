package com.mitiempo.videollamada.vistaVideollamada

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mitiempo.videollamada.R
import com.mitiempo.videollamada.vistaVideollamada.ManejadorPermisos.ManejadorPermisosCamara
import com.mitiempo.videollamada.vistaVideollamada.ManejadorPermisos.ManejadorPermisosMicrofono
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.ManejadorVistaVideollamada
import com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada.SocketVideollamada
import kotlinx.android.synthetic.main.visualizador_vista_videollamada.view.*

class Videollamada @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var canalComunicacion : String ?= null
    fun conCanalDeComunicacion(canalComunicacion : String) : Videollamada{
        this.canalComunicacion = canalComunicacion
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
            verificarPermisosCamara()
            verificarPermisosMicrofono()
            iniciarManejadorCamaraLocal()
            inicializarVideollamada()
        }
        return this
    }

    private fun verificarPermisosCamara(){
        ManejadorPermisosCamara
            .getInstancia()
            .conContexto(context)
            .conEscuchadorRespuestaPositivaDialogo {

            }
            .conEscuhadorRespuestaNegativaDialogo {

            }
            .conEscuchadorMensajeSolicitarPermisos{
                titulo,mensaje,respuestaPositiva,respuestaNegativa ->
                respuestaPositiva?.invoke()
            }
            .verificarPermisos()

    }

    private fun verificarPermisosMicrofono(){
        ManejadorPermisosMicrofono
            .getInstancia()
            .conContexto(context)
            .conEscuchadorRespuestaPositivaDialogo {

            }
            .conEscuhadorRespuestaNegativaDialogo {

            }
            .conEscuchadorMensajeSolicitarPermisos{
                    titulo,mensaje,respuestaPositiva,respuestaNegativa ->
                respuestaPositiva?.invoke()
            }
            .verificarPermisos()
    }

    private fun iniciarManejadorCamaraLocal(){

        ManejadorVistaVideollamada(
            context,
            camara_local,
            camara_remota
        )
            .conURL("ws://192.168.0.3:3000/?room=")
            .conRoom("Prueba_1869339814")
            .iniciarVideoLlamada()


    }

    private var manejador = SocketVideollamada("http://192.168.0.3:3000")
    private fun inicializarVideollamada(){
        manejador
            .conEscuchadorFalla { titulo, mensaje ->

            }
            .iniciarVideoLlamada()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.visualizador_vista_videollamada,this,true)
    }


}