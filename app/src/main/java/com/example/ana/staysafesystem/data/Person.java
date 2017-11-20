package com.example.ana.staysafesystem.data;

import java.io.Serializable;
import java.util.Objects;

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

    public String toString() {
        return name;
    }

    public String viewContactString() {
        return "Nome: " + name + " Número: " + phoneNumber;
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
}