package de.stefan_oltmann.kaesekaestchen.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreBoardViewModel : ViewModel() {

    val pokalDrawableResId = MutableLiveData<Int>()
    val punktestandKaese = MutableLiveData<String>()
    val punktestandMaus = MutableLiveData<String>()

}