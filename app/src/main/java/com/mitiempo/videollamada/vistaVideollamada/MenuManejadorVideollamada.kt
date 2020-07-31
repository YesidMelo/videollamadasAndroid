package com.mitiempo.videollamada.vistaVideollamada

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mitiempo.videollamada.R
import kotlinx.android.synthetic.main.menu_manejador_videollamada.view.*

class MenuManejadorVideollamada @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var EscuchadorLlamar : (()->Unit) ?= null
    fun conEscuchadorLlamar(EscuchadorLlamar : (()->Unit)): MenuManejadorVideollamada{
        this.EscuchadorLlamar = EscuchadorLlamar
        return this
    }

    private var EscuchadorColgar : (()->Unit) ?= null
    fun conEscuchadorColgar(EscuchadorColgar : (()->Unit)): MenuManejadorVideollamada{
        this.EscuchadorColgar = EscuchadorColgar
        return this
    }

    private var EscuchadorMicrofono : (()->Unit) ?= null
    fun conEscuchadorMicrofono(EscuchadorMicrofono : (()->Unit)): MenuManejadorVideollamada{
        this.EscuchadorMicrofono = EscuchadorMicrofono
        return this
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.menu_manejador_videollamada,this,true)
        ponerEscuchadores()
    }

    private fun ponerEscuchadores() {

        boton_videollamada.setOnClickListener { EscuchadorLlamar?.invoke() }
        boton_colgar.setOnClickListener { EscuchadorColgar?.invoke() }
        boton_microfono.setOnClickListener { EscuchadorMicrofono }

    }

}