package com.card.myapplication

import android.widget.TextView
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind

class Items(
    val name: String,
    private val desc: String,
) : ItemBind {
    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        vh.findView<TextView>(R.id.tvName).text = name
        vh.findView<TextView>(R.id.tvDes).text = desc
    }

}

val listItems = arrayListOf<Items>(
    Items("选择文件写入标签", "选择文件写入标签"),
    Items("获取标签文件列表", "获取标签文件列表"),
    Items("读取指定文件", "读取指定文件"),
    Items("删除指定文件", "删除指定文件"),
    Items("获取标签信息", "获取标签信息"),
    Items("获取标签版本", "获取标签版本"),
    Items("清空标签", "清空标签"),
    Items("更新屏幕", "更新屏幕"),
    Items("写入履历信息", "写入履历信息并更新屏幕"),
    Items("获取履历信息", "获取履历信息"),
    Items("传入指令类型获取履历信息", "传入指令类型获取履历信息"),
    Items("获取指定文件大小", "获取指定文件大小"),
    Items("写入一条码加注记录", "写入一条码加注记录"),
    Items("写入一条装备列表", "写入一条装备列表"),
    Items("写入一条设备更换记录", "写入一条设备更换记录"),
    Items("写入一条启油封记录", "写入一条启油封记录"),
    Items("写入一条技术通报记录", "写入一条技术通报记录"),
    Items("写入一条软件变更记录", "写入一条软件变更记录"),
    Items("写入一条修理记录", "写入一条修理记录"),
    Items("写入一条通电时间记录", "写入一条通电时间记录"),
    Items("写入一条维护记录", "写入一条维护记录"),
    Items("写入一条重要记事", "写入一条重要记事"),
    Items("写入一条调拨记录", "写入一条调拨记录"),
    Items("写入一条挂机值班记录", "写入一条挂机值班记录"),
    Items("测试 014 jsq 设备", "测试 014 jsq 设备"),
    Items("测试 nfc 读写", "测试 nfc 读写"),
)
