package de.stefan_oltmann.kaesekaestchen.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.stefan_oltmann.kaesekaestchen.controller.SpielLogik
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld

class SpielViewModel : ViewModel() {

    val spielfeld = MutableLiveData<Spielfeld>()
    val spielLogik = MutableLiveData<SpielLogik>()

}