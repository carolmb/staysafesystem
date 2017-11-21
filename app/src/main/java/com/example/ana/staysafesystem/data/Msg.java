package com.example.ana.staysafesystem.data;

/**
 * Created by ana on 20/11/17.
 */
public class Msg {
    String content;
    boolean local;
    boolean heartbeat;
    boolean fall;
    boolean audio;

    public Msg(String content,
               boolean local, boolean heartbeat, boolean fall, boolean audio) {
        this.content = content;
        this.local = local;
        this.heartbeat = heartbeat;
        this.fall = fall;
        this.audio = audio;
    }
}