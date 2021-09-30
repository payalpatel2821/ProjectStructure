package com.task.newapp.models.contact;

import androidx.annotation.Nullable;

import com.task.newapp.models.ResponseIsAppUser;
import com.task.newapp.models.ResponseMyContact;
import com.task.newapp.models.contact.Contact;
import com.task.newapp.realmDB.models.MyContacts;

/**
 * Created by EgemenH on 02.06.2017.
 */

public class ContactRecyclerViewModel {
    private ResponseMyContact.Data contact;
    private String letter;
    private int type;

    public ContactRecyclerViewModel(@Nullable ResponseMyContact.Data contact, String letter, int type) {
        this.contact = contact;
        this.letter = letter;
        this.type = type;
    }

    @Nullable
    public ResponseMyContact.Data getContact() {
        return contact;
    }

    public String getLetter() {
        return letter;
    }

    public int getType() {
        return type;
    }
}
