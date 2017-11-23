package com.example.ana.staysafesystem.data;

/**
 * Created by ana on 22/11/17.
 */

public class Call {
    String day;
    String hour;
    String description;
    String finished;

    public Call(String day, String hour, String description, String finished) {
        this.day = day;
        this.hour = hour;
        this.description = description;
        this.finished = finished;
    }

    public Call(String day, String hour, String description) {
        this.day = day;
        this.hour = hour;
        this.description = description;
        this.finished = "Não foi resolvido";
    }

    public void setFinished() {
        finished = "Foi resolvido";
    }

    public String longToString() {
        return day + "\n" + hour + "\n" + description + '\n' + finished;
    }

    public String toString() {
        return day + " " + hour;
    }
}
