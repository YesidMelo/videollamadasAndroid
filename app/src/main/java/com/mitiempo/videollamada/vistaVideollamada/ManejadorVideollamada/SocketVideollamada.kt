package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.mitiempo.videollamada.R

class SocketVideollamada(
    private val url : String
) {

    enum class ServiciosSocket(private val nombreServicio: String) {
        subscribe("subscribe"),
        new_user("new user"),
        newUserStart("newUserStart"),
        sdp("sdp"),
        ice_candidates("ice candidates"),
        chat("chat"),
        connect("connect"),
        ;

        fun traerNombreServicios(): String {
            return nombreServicio
        }
    }

    private var escuchadorFalla: ((Int, Int) -> Unit)? = null
    fun conEscuchadorFalla(escuchadorFalla: ((Int, Int) -> Unit)): SocketVideollamada {
        this.escuchadorFalla = escuchadorFalla
        return this
    }


    fun iniciarVideoLlamada(): SocketVideollamada {
        iniciarSocket()
        return this
    }

    private val T = "Socket"

    private var socket: Socket? = null
    private val numero_maximo_intentos = 10
    private var contador_numero_intentos = 0
    private fun iniciarSocket() {

        if (socket != null) {
            return
        }
        Log.e(T, "la url es : ${url}");

        try {

            socket = IO.socket(url)
            socket?.connect()


            adicionarEscuchadores()
            unirseVideollamada()


        } catch (e: Exception) {
            Log.e(T, "", e);
            if (numero_maximo_intentos != contador_numero_intentos) {
                contador_numero_intentos++
                iniciarSocket()
                return
            }

            contador_numero_intentos = 0
            escuchadorFalla?.invoke(R.string.videollamada, R.string.fallo_la_conexion_videollamada)

        }
    }

    private fun adicionarEscuchadores() {
        socket
            ?.on(ServiciosSocket.connect.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador connect");
                }

            })
            ?.on(ServiciosSocket.subscribe.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador subscribe");
                }

            })
            ?.on(ServiciosSocket.newUserStart.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador newUserStart");
                }

            })
            ?.on(ServiciosSocket.sdp.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador sdp");
                }

            })
            ?.on(ServiciosSocket.ice_candidates.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador ice candidate");
                }

            })
            ?.on(ServiciosSocket.chat.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador chat");
                }

            })

    }


    private fun unirseVideollamada(){
        try {
            socket?.emit(ServiciosSocket.connect.traerNombreServicios(),"Yesid")
        }catch (e : Exception){

        }
    }



}