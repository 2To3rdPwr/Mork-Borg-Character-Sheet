package com.example.morkborgcharactersheet.util

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData

/**
 * Default LiveData doesn't notify observers if properties change.
 *  This version seeks to remedy that
 *  Credit to https://stackoverflow.com/questions/48020377/livedata-update-on-object-field-change
 */
class PropertyAwareMutableLiveData<T: BaseObservable>: MutableLiveData<T>() {
    private val callback = object: Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            value = value
        }
    }

    override fun setValue(value: T?) {
        super.setValue(value)

        value?.addOnPropertyChangedCallback(callback)
    }
}