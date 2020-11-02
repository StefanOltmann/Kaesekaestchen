package de.stefan_oltmann.kaesekaestchen.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.stefan_oltmann.kaesekaestchen.model.FeldGroesse
import de.stefan_oltmann.kaesekaestchen.model.SpielModus

class StartViewModel : ViewModel() {

    val spielModus = MutableLiveData(SpielModus.EINZELSPIELER)
    val feldGroesse =  MutableLiveData(FeldGroesse.KLEIN)
}