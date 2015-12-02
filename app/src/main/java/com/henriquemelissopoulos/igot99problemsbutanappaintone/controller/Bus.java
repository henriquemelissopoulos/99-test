package com.henriquemelissopoulos.igot99problemsbutanappaintone.controller;

/**
 * Created by h on 01/12/15.
 */
public class Bus<T> {

    public int key;
    public boolean error = false;
    public Exception e;
    public String info = "";
    public String requesterID = "";
    public T data;


    public Bus(int key) {
        this.key = key;
    }

    public Bus<T> data(T data) {
        this.data = data;
        return this;
    }

    public Bus<T> info(String info) {
        this.info = info;
        return this;
    }

    public Bus<T> requesterID(String requesterID) {
        this.requesterID = requesterID;
        return this;
    }

    public Bus<T> error(boolean error) {
        this.error = error;
        return this;
    }

    public Bus<T> error(boolean error, Exception e) {
        this.error = error;
        this.e = e;
        return this;
    }
}
