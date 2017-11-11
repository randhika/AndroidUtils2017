package com.fallwater.androidutils2017.module.camera;

import java.util.Observable;
import java.util.Observer;
public class MyObservable extends Observable {

    private volatile static MyObservable instance;

    private MyObservable() {
    }

    public void add(MyObserver akulakuObserver) {
        addObserver(akulakuObserver);
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    public static MyObservable instance() {
        if (instance == null) {
            synchronized (MyObservable.class) {
                if (instance == null) {
                    instance = new MyObservable();
                }
            }
        }
        return instance;
    }

    public void notifyObservers () {

    }

    @Override
    final public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }

    public void notifyObservers(ObservableAction action) {
        setChanged();
        super.notifyObservers(action);
    }

    public abstract static class MyObserver<T> implements Observer {
        public String action;
        public Object object;

        public MyObserver(String action) {
            this.action = action;
        }
        public String getAction () {
            return action;
        }

        public Object getObject() {
            return object;
        }

        @Override
        public void update(Observable observable, Object data) {
            if (!(data instanceof ObservableAction)) {
                return;
            }
            ObservableAction mObservableAction = (ObservableAction) data;
            if (ObservableAction.ALL.equalsIgnoreCase(mObservableAction.action)
                    || mObservableAction.action.equalsIgnoreCase(getAction())) {
                object = mObservableAction.object;
                update(observable, this);
            }
        }

        public abstract void update(Observable observable, MyObserver observer);
    }

    public static class ObservableAction {
        public final static String ALL = "all";
        public String action = ALL;
        public Object object;

        public ObservableAction(String action) {
            this.action = action;
        }

        public ObservableAction(Object object) {
            this.object = object;
        }

        public ObservableAction(String action, Object object) {
            this.action = action;
            this.object = object;
        }

    }
}
