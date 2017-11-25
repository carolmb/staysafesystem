package com.example.ana.staysafesystem.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ana on 20/11/17.
 */

public class Person implements Serializable {
    String name;
    String phoneNumber;
    public Person(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String toString() {
        return name;
    }

    public String viewContactString() {
        return "Nome: " + name + "\nNúmero: " + phoneNumber;
    }

    public boolean isValid() {
        return name != null && phoneNumber != null &&
                name.length() > 0 && phoneNumber.length() > 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        // null check
        if (other == null)
            return false;
        // type check and cast
        if (getClass() != other.getClass())
            return false;
        Person person = (Person) other;
        // field comparison
        return name.contentEquals(person.name)
                && phoneNumber.contentEquals(person.phoneNumber);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}