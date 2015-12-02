package com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.network;

import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Bus;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Config;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.model.Taxi;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    public void getTaxis(String sw, String ne) {

        new API().service().getTaxis(sw, ne)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Taxi>>() {

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new Bus<ArrayList<Taxi>>(Config.GET_TAXI_LIST).error(true).info(e.toString()));
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<Taxi> taxis) {
                        EventBus.getDefault().post(new Bus<ArrayList<Taxi>>(Config.GET_TAXI_LIST).data(taxis));
                    }

                    @Override
                    public void onCompleted() {}
                });
    }
}
