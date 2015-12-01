package com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.network;

import android.util.Log;

import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Bus;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Config;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.model.Taxi;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit.Call;

/**
 * Created by h on 01/12/15.
 */
public class Service {

    private static Service instance;

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public void getTaxis() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Call<ArrayList<Taxi>> shotsCall = new API().service().taxis("-23.612474,-46.702746", "-23.589548,-46.673392"); //Hardcoded for now
                    ArrayList<Taxi> taxis = shotsCall.execute().body();

                    EventBus.getDefault().post(new Bus<ArrayList<Taxi>>(Config.GET_TAXI_LIST).data(taxis));

                } catch (Exception e) {
                    EventBus.getDefault().post(new Bus<ArrayList<Taxi>>(Config.GET_TAXI_LIST).error(true).info(e.toString()));
                    Log.d("service", "Failure on getTaxis");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
