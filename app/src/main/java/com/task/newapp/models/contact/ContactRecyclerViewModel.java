package com.task.newapp.models.contact;

import androidx.annotation.Nullable;

import com.task.newapp.models.contact.Contact;

/**
 * Created by EgemenH on 02.06.2017.
 */

public class ContactRecyclerViewModel {
    private Contact contact;
    private String letter;
    private int type;

    public ContactRecyclerViewModel(@Nullable Contact contact, String letter, int type) {
        this.contact = contact;
        this.letter = letter;
        this.type = type;
    }

    @Nullable
    public Contact getContact() {
        return contact;
    }

    public String getLetter() {
        return letter;
    }

    public int getType() {
        return type;
    }
}
