package com.mitiempo.videollamada.vistaVideollamada.ManejadorVideollamada

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.mitiempo.videollamada.R
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

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
                    crearRoomParaVideollamada()
                }

            })

            ?.on(ServiciosSocket.chat.traerNombreServicios(), object : Emitter.Listener {

                override fun call(vararg args: Any?) {
                    Log.e(T, "escuchador chat");
                }

            })

    }

    private var to = ""
    private var sender = ""
    private fun crearRoomParaVideollamada(){

        val jsonAEnviar = "{ \"room\" : \"${this.room}\", \"socketId\" : \"${socket?.id()}\" }"
        sender = socket?.id()!!
        socket?.emit(ServiciosSocket.subscribe.traerNombreServicios(),jsonAEnviar)
        socket?.on(ServiciosSocket.new_user.traerNombreServicios()){
            args ->

            val sockets = (args[0] as JSONObject)["sockets"] as JSONArray

            for(contador in 0 until sockets.length()){
                if (sockets.getString(contador) == sender){ continue }
                to = sockets.getString(contador)
            }

            Log.e(T," socketID : ${socket?.id()}, sender : ${sender}, to : ${to}");
            val jsonSender = "{ \"to\" : \"${to}\" , \"sender\" : \"${sender}\"}"
            socket?.emit(ServiciosSocket.newUserStart.traerNombreServicios(),jsonSender)
        }

        socket?.on(ServiciosSocket.newUserStart.traerNombreServicios()){
            args ->
            val objetoJson = args[0] as JSONObject
            to = objetoJson["sender"].toString()

        }

    }

    private var escuchadorSdpRemoto : ((SessionDescription?)->Unit) ?= null
    fun conEscuchadorSdpRemoto(escuchadorSdpRemoto : ((SessionDescription?)->Unit)) : SocketVideollamada{
        this.escuchadorSdpRemoto = escuchadorSdpRemoto
        return this
    }

    fun enviarSdpARoom(sessionDescription: SessionDescription){
//        val description = sessionDescription.description.replace("\r","\\r").replace("\n","\\n")
        val mySessionDescription = SessionDescription(sessionDescription.type,sessionDescription.description.replace("\r","\\r").replace("\n","\\n"))
        val description = Gson().toJson(mySessionDescription)

        var jsonAEnviar = "{"
        jsonAEnviar+= "\"to\" : \"${to}\","
        jsonAEnviar+= "\"sender\" : \"${sender}\","
        jsonAEnviar+= "\"description\" : ${description}"
        jsonAEnviar+= "}"


        socket?.emit(ServiciosSocket.sdp.traerNombreServicios(),jsonAEnviar)
        socket?.on(ServiciosSocket.sdp.traerNombreServicios()){
            args ->
            Thread {

                val objetoJson = args[0] as JSONObject
                val descripcionRemota:String  = objetoJson["description"].toString()
                val sessionDescriptionLlegada =Gson().fromJson(descripcionRemota,SessionDescription::class.java)

                val descripcionDellegada = sessionDescription.description.replace("\\r","\r").replace("\\n","\n")
                escuchadorSdpRemoto?.invoke(SessionDescription(sessionDescriptionLlegada.type,descripcionDellegada))
                Log.e(T,"objeto recibido : ${descripcionRemota}")

            }.start()
        }


    }

    fun enviarOnIceCandidate(iceCandidate: IceCandidate){

    }



}