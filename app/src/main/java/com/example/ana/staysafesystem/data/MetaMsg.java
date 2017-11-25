package com.example.ana.staysafesystem.data;

import java.io.Serializable;

/**
 * Created by ana on 20/11/17.
 */
public class MetaMsg implements Serializable {
    String content;
    boolean local;
    boolean heartbeat;
    boolean fall;
    boolean audio;

    public MetaMsg(String content,
                   boolean local, boolean heartbeat, boolean fall, boolean audio) {
        this.content = content;
        this.local = local;
        this.heartbeat = heartbeat;
        this.fall = fall;
        this.audio = audio;
    }
}