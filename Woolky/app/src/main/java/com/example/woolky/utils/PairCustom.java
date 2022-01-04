package com.example.woolky.utils;

public class PairCustom<A, B> {

    private A first;
    private B second;

    public PairCustom() {}

    public PairCustom(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}
