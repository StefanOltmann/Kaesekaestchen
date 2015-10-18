/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
 *
 * Contact : dotsandboxes@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
 *
 * This file is part of Kaesekaestchen.
 *
 * Kaesekaestchen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaesekaestchen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kaesekaestchen. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.kaesekaestchen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import de.stefan_oltmann.kaesekaestchen.model.Kaestchen;
import de.stefan_oltmann.kaesekaestchen.model.Spieler;
import de.stefan_oltmann.kaesekaestchen.model.SpielerManager;
import de.stefan_oltmann.kaesekaestchen.model.SpielerTyp;
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld;
import de.stefan_oltmann.kaesekaestchen.model.Strich;

/**
 * Die Haupt-Acitivty, die das Spielfeld verwaltet und den Gameloop steuert.
 */
public class SpielActivity extends Activity {

    private SpielfeldView    spielfeldView;
    private Spielfeld        spielfeld;
    private SpielerManager   spielerManager;

    private final Handler    mHandler = new Handler();

    /** Diese Variable steuert den Game Loop Thread. */
    private volatile boolean running  = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Bundle intentExtras = getIntent().getExtras();

        SpielerTyp spielerTyp1 = (SpielerTyp) intentExtras.get("spielerTyp1");
        SpielerTyp spielerTyp2 = (SpielerTyp) intentExtras.get("spielerTyp2");

        int feldGroesseX = intentExtras.getInt("feldGroesseX");
        int feldGroesseY = intentExtras.getInt("feldGroesseY");

        spielfeld = Spielfeld.generieren(feldGroesseX, feldGroesseY);
        spielerManager = new SpielerManager();

        spielfeldView = (SpielfeldView) findViewById(R.id.spielfeldView);
        spielfeldView.init(spielfeld);

        spielerManager.addSpieler(
                new Spieler(getResources().getString(R.string.spieler_1_name),
                        BitmapFactory.decodeResource(getResources(), R.drawable.spieler_symbol_kaese),
                        getResources().getColor(R.color.spieler_1_farbe), spielerTyp1));
        spielerManager.addSpieler(
                new Spieler(getResources().getString(R.string.spieler_2_name),
                        BitmapFactory.decodeResource(getResources(), R.drawable.spieler_symbol_maus),
                        getResources().getColor(R.color.spieler_2_farbe), spielerTyp2));

        startGameLoop();
    }

    @Override
    protected void onStop() {
        running = false;
        super.onStop();
    }

    public void startGameLoop() {
        Thread thread = new Thread(new GameLoopRunnable());
        thread.start();
        running = true;
    }

    private class GameLoopRunnable implements Runnable {

        public void run() {

            /* Auswahl des ersten Spielers */
            spielerManager.naechstenSpielerAuswaehlen();

            while (!isGameOver()) {

                final Spieler spieler = spielerManager.getAktuellerSpieler();

                /*
                 * Anzeige welcher Spieler dran ist und wieviele Punkt dieser
                 * schon hat.
                 */
                mHandler.post(new Runnable() {
                    public void run() {

                        ImageView imageView = (ImageView) findViewById(R.id.aktuellerSpielerSymbol);
                        imageView.setImageBitmap(spieler.getSymbol());

                        TextView textView = (TextView) findViewById(R.id.punkteAnzeige);
                        textView.setText(String.valueOf(ermittlePunktzahl(spieler)));
                    }
                });

                Strich eingabe = null;

                if (!spieler.isComputerGegner()) {

                    spielfeldView.resetLetzteEingabe();

                    /*
                     * Der Benutzer muss nun seine Eingabe tätigen. Dieser
                     * Gameloop- Thread soll nun darauf warten. Dafür wird hier
                     * die wait()/notify()-Technik von Java verwendet. Solange
                     * keine neue Eingabe getätigt wurde, schläft dieser Thread
                     * nun.
                     */
                    while ((eingabe = spielfeldView.getLetzteEingabe()) == null) {
                        try {
                            synchronized (spielfeldView) {
                                spielfeldView.wait();
                            }
                        } catch (InterruptedException ignore) {
                            /*
                             * Dieser Fall kann ignoriert werden. Sollte der
                             * Thread plötzlich wieder aufwachen, wird er sofern
                             * noch keine Eingabe getätigt ist durch die
                             * umgebene while-Schleife direkt wieder schlafen
                             * gelegt.
                             */
                        }
                    }

                } else {

                    try { /* Der Nutzer soll die Eingabe des Computers sehen. */
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {
                    }

                    eingabe = computerGegnerZug(spieler.getSpielerTyp());
                }

                waehleStrich(eingabe);

                /*
                 * Wurde die Activity beendet, dann auch diesen Thread stoppen.
                 * Ohne diese Zeile würde die Activity niemals enden und der
                 * Thread immer weiter laufen, bis Android diesen killt. Wir
                 * wollen aber natürlich nicht negativ auffallen.
                 */
                if (!running)
                    return;
            }

            /*
             * Wenn alle Kästchen besetzt sind, ist das Spiel vorbei und der
             * "Game Score" kann angezeigt werden.
             */
            if (isGameOver()) {

                mHandler.post(new Runnable() {

                    public void run() {

                        Spieler gewinner = ermittleGewinner();

                        /* FIXME Hartkodierte Pokalbilder */
                        int pokalBildID = 0;
                        if (gewinner.getName().equals(getResources().getString(R.string.spieler_1_name)))
                            pokalBildID = R.drawable.pokal_kaese;
                        else
                            pokalBildID = R.drawable.pokal_maus;

                        AlertDialog alertDialog = new AlertDialog.Builder(SpielActivity.this)
                                .setTitle(getResources().getText(R.string.game_score))
                                .setIcon(getResources().getDrawable(pokalBildID))
                                .setMessage(getGameOverDialogMessage())
                                .setCancelable(false)
                                .setPositiveButton(getResources().getText(R.string.play_again),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(getIntent());
                                            }
                                        })
                                .setNegativeButton(getResources().getText(R.string.zurueck_zum_hauptmenue),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                SpielActivity.this.finish();
                                            }
                                        })
                                .create();

                        alertDialog.show();
                    }
                });
            }
        }

    }

    private String getGameOverDialogMessage() {

        Spieler gewinner = ermittleGewinner();

        StringBuilder sb = new StringBuilder();

        sb.append(getResources().getString(R.string.gewinner) + ": " + gewinner.getName() + "\n\n");

        for (Spieler spieler : spielerManager.getSpieler())
            sb.append(spieler.getName() + ":\t\t" + ermittlePunktzahl(spieler) + "\n");

        return sb.toString();
    }

    private Strich computerGegnerZug(SpielerTyp spielerTyp) {

        Strich strich = waehleLetztenOffenenStrichFuerKaestchen();

        if (strich != null)
            return strich;

        Strich zufallsStrich = waehleZufallsStrich();

        /*
         * Die einfache KI wählt einfach irgendeinen Strich, die mittlere KI
         * passt wenigstens auf, dass kein Strich gewählt wird, der beim Zug des
         * Gegners ein Kästchen schließen könnte und diesem damit einen Punkt
         * schenkt.
         */
        if (spielerTyp == SpielerTyp.COMPUTER_MITTEL) {

            int loopCounter = 0;

            while (zufallsStrich.isKoennteUmliegendendesKaestchenSchliessen()) {

                zufallsStrich = waehleZufallsStrich();

                /*
                 * Dies wird maximal 30 Mal versucht. Konnte dann immer noch
                 * keine gefunden werden, gibt es entweder keine mehr oder der
                 * Gegner darf auch mal Glück haben.
                 */
                if (++loopCounter >= 30)
                    break;
            }
        }

        return zufallsStrich;
    }

    private Strich waehleLetztenOffenenStrichFuerKaestchen() {

        for (Kaestchen kaestchen : spielfeld.getOffeneKaestchenListe())
            if (kaestchen.getStricheOhneBesitzer().size() == 1)
                return kaestchen.getStricheOhneBesitzer().get(0);

        return null;
    }

    private Strich waehleZufallsStrich() {

        List<Strich> stricheOhneBesitzer = new ArrayList<Strich>(spielfeld.getStricheOhneBesitzer());
        Strich zufallsStrich = stricheOhneBesitzer.get(new Random().nextInt(stricheOhneBesitzer.size()));

        return zufallsStrich;
    }

    private void waehleStrich(Strich strich) {

        if (strich.getBesitzer() != null)
            return;

        Spieler aktuellerSpieler = spielerManager.getAktuellerSpieler();

        boolean kaestchenKonnteGeschlossenWerden = spielfeld.waehleStrich(strich, aktuellerSpieler);

        /*
         * Wenn ein Kästchen geschlossen werden konnte, ist derjenige Spieler
         * noch einmal dran. Konnte er keines schließen, ist der andere Spieler
         * wieder dran:
         */
        if (!kaestchenKonnteGeschlossenWerden)
            spielerManager.naechstenSpielerAuswaehlen();

        spielfeldView.anzeigeAktualisieren();
    }

    public boolean isGameOver() {
        return spielfeld.isAlleKaestchenHabenBesitzer();
    }

    public Spieler ermittleGewinner() {

        Spieler gewinner = null;
        int maxPunktZahl = 0;

        for (Spieler spieler : spielerManager.getSpieler()) {

            int punktZahl = ermittlePunktzahl(spieler);

            if (punktZahl > maxPunktZahl) {
                gewinner = spieler;
                maxPunktZahl = punktZahl;
            }
        }

        return gewinner;
    }

    public int ermittlePunktzahl(Spieler spieler) {

        int punkte = 0;

        for (Kaestchen kaestchen : spielfeld.getKaestchenListe())
            if (kaestchen.getBesitzer() == spieler)
                punkte++;

        return punkte;
    }

}