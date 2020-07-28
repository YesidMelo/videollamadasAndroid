package com.mitiempo.videollamada.vistaVideollamada.ManejadorPermisos

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.mitiempo.videollamada.R

class ManejadorPermisosCamara private constructor(){


    companion object{

        private var tengoLosPermisosHabilitados = false
        private var instancia : ManejadorPermisosCamara ?= null
        val CodigoCamara : Int = 300

        fun getInstancia() : ManejadorPermisosCamara{
            if(instancia == null ){
                instancia = ManejadorPermisosCamara()
            }
            return instancia!!
        }
    }



    private var context : Context?= null
    fun conContexto(context: Context) :  ManejadorPermisosCamara{
        this.context = context
        return this
    }

    private var escuchadorMensajeSolicitarPermisos : ((Int,Int,(()->Unit)?,(()->Unit)?)->Unit) ?=null
    fun conEscuchadorMensajeSolicitarPermisos(escuchadorMensajeSolicitarPermisos : ((Int,Int,(()->Unit)?,(()->Unit)?)->Unit)) :  ManejadorPermisosCamara{
        this.escuchadorMensajeSolicitarPermisos = escuchadorMensajeSolicitarPermisos
        return this
    }




    class ManejadorPermisosSinContexto : Exception("ManejadorPermisos No tiene un contexto")
    fun verificarPermisos() : ManejadorPermisosCamara{
        if(context == null ){
            throw ManejadorPermisosSinContexto()
        }
        if(tengoLosPermisosHabilitados){
            escuchadorRespuestaPositivaDialogo?.invoke()
            return this
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            escuchadorRespuestaPositivaDialogo?.invoke()
            tengoLosPermisosHabilitados = true
            return this
        }

        solicitarPermisosVersionesPosterioresM()

        return this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun solicitarPermisosVersionesPosterioresM(){
        if(tengoLosPermisosEnEstadoGranted()){
            escuchadorRespuestaPositivaDialogo?.invoke()
            return
        }

        solicitarPermisos()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun tengoLosPermisosEnEstadoGranted() : Boolean{
        return context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun solicitarPermisos(){

        if(context == null ){
            throw ManejadorPermisosSinContexto()
        }
        escuchadorMensajeSolicitarPermisos?.invoke(
            R.string.camera_option,
            R.string.camara_deshabilitada,
            ::ejecutarSolicitanteDePermisosEnApp,
            {escuchadorRespuestaNegativaDialogo?.invoke()}
        )

    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun ejecutarSolicitanteDePermisosEnApp(){
        (context as? Activity)
            ?.requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CodigoCamara
            )
    }


    fun conOnRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) : ManejadorPermisosCamara{
        if (requestCode != CodigoCamara){
            return this
        }

        if(tengoLosPermisosHabilitadosComoResultdo(grantResults)){
            escuchadorRespuestaPositivaDialogo?.invoke()
            return this
        }

        mostrarAlertaPermisosDeshabilitados()
        return this
    }

    private fun tengoLosPermisosHabilitadosComoResultdo(grantResults: IntArray) : Boolean{
        return  grantResults[0] == PackageManager.PERMISSION_GRANTED
    }


    private fun mostrarAlertaPermisosDeshabilitados(){
        if(context == null ){
            throw ManejadorPermisosSinContexto()
        }

        escuchadorMensajeSolicitarPermisos?.invoke(
            R.string.camera_option,
            R.string.camara_deshabilitada,
            ::ejecutarSolicitanteDePermisosEnApp,
            {escuchadorRespuestaNegativaDialogo?.invoke()}
        )
    }


    private var escuchadorRespuestaNegativaDialogo : (()->Unit)?= null
    fun conEscuhadorRespuestaNegativaDialogo(accionRespuestaNegativaDialogo : (()->Unit)) : ManejadorPermisosCamara{
        this.escuchadorRespuestaNegativaDialogo = accionRespuestaNegativaDialogo
        return this
    }

    private var escuchadorRespuestaPositivaDialogo : (()->Unit)?= null
    fun conEscuchadorRespuestaPositivaDialogo(escuchadorRespuestaPositivaDialogo : (()->Unit)) : ManejadorPermisosCamara{
        this.escuchadorRespuestaPositivaDialogo = escuchadorRespuestaPositivaDialogo
        return this
    }

}