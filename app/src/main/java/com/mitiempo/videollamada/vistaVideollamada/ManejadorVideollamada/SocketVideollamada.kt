package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.mitiempo.videollamada.R
import org.json.JSONObject

class SocketVideollamada(
    private val url : String
)
{

    enum class ServiciosSocket(private val nombreServicio: String) {
        connection("connection"),
        connect("connect"),
        subscribe("subscribe"),
        new_user("new user"),
        newUserStart("newUserStart"),
        sdp("sdp"),
        ice_candidates("ice candidates"),
        chat("chat"),

        ;

        fun traerNombreServicios(): String {
            return nombreServicio
        }
    }

    private var room : String = "usuario1_usuario2"
    fun conRoom(room : String) : SocketVideollamada{
        this.room = room
        return this
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
                    suscribirseAVideollamada()
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

    private fun suscribirseAVideollamada(){

        val jsonAEnviar = "{ \"room\" : \"${this.room}\", \"socketId\" : \"${socket?.id()}\" }"

        socket?.emit(ServiciosSocket.subscribe.traerNombreServicios(), jsonAEnviar)

        socket?.on(ServiciosSocket.new_user.traerNombreServicios()) { args -> iniciarEmisionNuevoUsuario(args[0] as JSONObject) }
    }

    private var listaPCs = emptyList<String>().toMutableList()
    private fun iniciarEmisionNuevoUsuario(jsonObject: JSONObject){

        val jsonAEnviar = "{ \"to\" : \"${jsonObject["socketId"]}\" ,\"sender\" : \"${socket?.id()}\" }"

        socket?.emit(ServiciosSocket.newUserStart.traerNombreServicios(),jsonAEnviar)
        socket?.on(ServiciosSocket.newUserStart.traerNombreServicios()) {
            args ->

            listaPCs.add(((args[0] as JSONObject)["sender"] as String?)!!)
            Log.e(T, "escuchador newUserStart ${listaPCs}");

        }

    }


    private fun unirseVideollamada(){
        try {
            socket?.emit(ServiciosSocket.connection.traerNombreServicios(),"Yesid")
        }catch (e : Exception){

        }
    }



}