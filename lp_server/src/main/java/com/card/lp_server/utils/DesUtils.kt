package com.card.lp_server.utils

import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncryptUtils
import kotlin.experimental.inv

fun encrypt3DES(uid: ByteArray, keyString: String, randomNum: ByteArray): ByteArray {
    val keyBytes = ConvertUtils.hexString2Bytes(keyString)
    Log.d(TAG, "(1) 秘钥:$keyString")
    //      uid  3DES算法加密
    val left =
        EncryptUtils.encrypt3DES(uid, keyBytes, "DESede/ECB/NoPadding", null)
    Log.d(
        TAG,
        "(2)使用 uid[${
            ConvertUtils.bytes2HexString(
                uid
            )
        }] 和秘钥进行 3des 加密得到left: ${ConvertUtils.bytes2HexString(left)}"
    )
    // uid 取反
    val reversedCardNumber = ByteArray(8) { i -> uid[i].inv() }
    Log.d(
        TAG,
        "(3) 取反 uid:${
            ConvertUtils.bytes2HexString(
                reversedCardNumber
            )
        }"
    )
    //uid取反 3DES算法加密
    val right =
        EncryptUtils.encrypt3DES(reversedCardNumber, keyBytes, "DESede/ECB/NoPadding", null)

    Log.d(
        TAG, "(4)使用 取反 uid[${
            ConvertUtils.bytes2HexString(
                reversedCardNumber
            )
        }] 和秘钥进行 3des 加密得到right: ${ConvertUtils.bytes2HexString(right)}"
    )
    //合并
    val key1 = ByteArray(16)
    left.copyInto(key1, 0, 0, 8)
    right.copyInto(key1, 8, 0, 8)
    Log.d(TAG, "(5)合并 left,right得到 key1:${ConvertUtils.bytes2HexString(key1)} ")
    //使用密钥(key1)对8字节随机数做3DES算法加密得到8字节密文
    val encryptedA =
        EncryptUtils.encrypt3DES(randomNum, key1, "DESede/ECB/NoPadding", null)

    Log.d(
        TAG,
        " (6)使用密钥(key1)对8字节随机数[${ConvertUtils.bytes2HexString(randomNum)}]做3DES算法加密得到8字节密文: ${
            ConvertUtils.bytes2HexString(encryptedA)
        }"
    )
    //使用A作为密钥，使用单des算法对8字节随机数加密
    val encryptDES =
        EncryptUtils.encryptDES(randomNum, encryptedA, "DES/ECB/NoPadding", null)

    Log.d(
        TAG,
        "(7)使用单des算法对8字节随机数加密最终结果: ${ConvertUtils.bytes2HexString(encryptDES)}"
    )
    return encryptDES
}

fun encryptDES(data: String, keyString: ByteArray): ByteArray {
    val bytesDataDES3 = ConvertUtils.hexString2Bytes(data)
    val encryptDES =
        EncryptUtils.encryptDES(bytesDataDES3, keyString, "DES/ECB/NoPadding", null)
    Log.d(
        TAG,
        "encryptDES: ${ConvertUtils.bytes2HexString(encryptDES)}    长度->${encryptDES.size}"
    )

    return encryptDES
}