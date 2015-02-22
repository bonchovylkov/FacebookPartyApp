package com.mentormate.academy.fbpartyapp.Utils;

import com.facebook.Session;

/**
 * Created by Student11 on 2/19/2015.
 */
public class SingletonSession {

    private Session currentSession;

    public Session getCurrentSession() {
        return Session.getActiveSession();
    }

    public void setCurrentSession() {
        this.currentSession = Session.getActiveSession();
    }

    private  SingletonSession(){

    }

    private  static SingletonSession instance;

    public static SingletonSession getInstance(){
        if(instance==null)
        {
            instance = new SingletonSession();
            return instance;
        }else{
            return instance;
        }
    }
}
