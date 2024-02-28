package com.card.lp_server.utils

import com.blankj.utilcode.util.LogUtils

const val TAG = "---->>"
fun <T> logD(t: T, tag: String? = TAG) {
    LogUtils.dTag(tag, t)
}

fun <T> logE(t: T, tag: String? = TAG) {
    LogUtils.eTag(tag, t)
}

fun <T> logI(t: T, tag: String? = TAG) {
    LogUtils.iTag(tag, t)
}

