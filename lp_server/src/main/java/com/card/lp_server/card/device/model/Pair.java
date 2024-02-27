package com.card.lp_server.card.device.model;

/**
 * 返回状态和数据
 *
 * @param <K> 状态
 * @param <V> 数据
 * @author ma_zhe
 */
public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public void setFirst(K first) {
        this.first = first;
    }
}
