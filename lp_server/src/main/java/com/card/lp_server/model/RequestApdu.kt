package com.card.lp_server.model

import androidx.annotation.Keep

@Keep
class RequestApdu(
    val CLA: Int = 0x00,
    val INS: Int,
    val P1: Int = 0x00,
    val P2: Int = 0x00,
    val LC: Int? = null,
    val DATA: ByteArray? = null,
    val LE: Int? = null
) {
    fun toBytes(): ByteArray {
        val bytes = mutableListOf<Byte>()

        bytes.add(CLA.toByte())
        bytes.add(INS.toByte())
        bytes.add(P1.toByte())
        bytes.add(P2.toByte())

        LC?.let {
            bytes.add(it.toByte())
        }

        DATA?.let {
            bytes.addAll(DATA.map { it }.toMutableList())
        }

        LE?.let {
            bytes.add(it.toByte())
        }
        return bytes.toByteArray()
    }
}

@Keep
class ResponseApdu(
    var body: ByteArray? = null,
    var SW: ByteArray? = null
)

