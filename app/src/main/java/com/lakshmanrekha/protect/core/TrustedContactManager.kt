package com.lakshmanrekha.protect.core

import android.content.Context
import com.lakshmanrekha.protect.utils.AppPrefs

object TrustedContactManager {

    fun isTrusted(context: Context, phone: String): Boolean {
        return AppPrefs.getTrustedContacts(context).contains(phone)
    }

    fun add(context: Context, phone: String) {
        val list = AppPrefs.getTrustedContacts(context).toMutableSet()
        list.add(phone)
        AppPrefs.saveTrustedContacts(context, list)
    }

    fun remove(context: Context, phone: String) {
        val list = AppPrefs.getTrustedContacts(context).toMutableSet()
        list.remove(phone)
        AppPrefs.saveTrustedContacts(context, list)
    }
}