package com.samsung.android.sdk.accessory.example.helloaccessory.consumer;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kevin on 19.07.2016.
 */
public class ManagementSystem {
    private static ManagementSystem _ourInstance = new ManagementSystem();

    private List<FitnessExercise> _fitnessexercises = new ArrayList<FitnessExercise>();

    public static ManagementSystem getInstance() {
        return _ourInstance;
    }

    private ManagementSystem() {
        initialize();
    }

    public List<FitnessExercise> getFitnessexercises(){
        return this._fitnessexercises;
    }

    public FitnessExercise getFitnessExercise(String name){
        if(name != null) {
            Iterator<FitnessExercise> iter = this._fitnessexercises.iterator();
            FitnessExercise exercise;
            while(iter.hasNext()){
                exercise = iter.next();
                if(exercise.getName().equals(name)){
                    return exercise;
                }
            }
        }
        return null;
    }

    public FitnessExercise getFitnessExercise(int id){
        if(0 < id) {
            Iterator<FitnessExercise> iter = this._fitnessexercises.iterator();
            FitnessExercise exercise;
            while(iter.hasNext()){
                exercise = iter.next();
                if(exercise.getId() == id){
                    return exercise;
                }
            }
        }
        return null;
    }

    private void initialize(){
        _fitnessexercises.add(new FitnessExercise(1, "Gefangenen-Kniebeuge",
                " - Oberkörper minimal nach vorne neigen<br>" +
                " - Füße schulterbreit voneinander entfernt und leicht nach außen gedreht aufstellen<br>" +
                " - Knie leicht anwinkeln<br>" +
                " - Hände hinter dem Kopf halten (Kopf aber nicht nach vorne drücken)<br>" +
                " - Ellenbogen zeigen jetzt jeweils seitlich nach außen<br>" +
                " - Brust herausstrecken und Schulterblätter zusammenziehen",
                " - strecke dein Gesäß nach hinten heraus und beuge langsam deine Kniegelenke<br>" +
                " - lasse die Knie nicht über deine Zehenspitzen hinausragen, um die Gelenke zu schonen<br>" +
                " - außerdem sollten sie immer in die gleiche Richtung wie deine Füße zeigen<br>" +
                " - sind deine Oberschenkel parallel zum Boden, kannst du die Position für einen kurzen Moment halten, um das Beintraining ohne Geräte zu intensivieren<br>" +
                " - anschließend bringst du dich über Druck auf den Fersen kontrolliert zurück nach oben in die Ausgangsposition<br>" +
                " - halte auch dabei stets deinen Rücken gerade<br>" +
                " - Ausführung wiederholen",
                Uri.parse("android.resource://com.example.kevin.smarttracker/" +R.raw.prisonersquat)));
        _fitnessexercises.add(new FitnessExercise(2, "Liegestütz",
                " - gehe auf die Knie<br>" +
                " - senkrecht unter deinen Schultern platzierst du deine Hände<br>" +
                " - sie stützen deinen Oberkörper ab<br>" +
                " - Finger zusammen und Fingerspitzen zeigen nach vorne<br>" +
                " - Rücken gerade und den Kopf in Verlängerung der Wirbelsäule halten<br>" +
                " - Beine nach hinten strecken<br>" +
                " - du kannst sie zusammen lassen oder alternativ schulterbreit öffnen",
                " - beuge langsam deine Arme<br>" +
                " - Ellenbogen zeigen dabei nach außen und etwas schräg nach hinten<br>" +
                " - dein Körper sinkt so weit, bis deine Nase den Boden berührt<br>" +
                " - er bleibt dabei vollständig in einer Linie<br>" +
                " - vermeide ein Hohlkreuz oder ein Herausstrecken deines Gesäßes<br>" +
                " - drücke dich anschließend wieder nach oben<br>" +
                " - achte dann darauf, dass du deine Arme leicht gebeugt sind<br>" +
                " - Ausführung der Liegestütze mehrmals hintereinander wiederholen",
                Uri.parse("android.resource://com.example.kevin.smarttracker/" +R.raw.pushup)));
        _fitnessexercises.add(new FitnessExercise(3, "Russische Drehung",
                " - setze dich mit deinem Po auf den Boden<br>" +
                " - Beine zu 90 Grad anwinkeln und Fersen auf dem Boden absetzen<br>" +
                " - Oberkörper etwa 45 Grad nach hinten lehnen und Rücken gerade halten<br>" +
                " - Arme gerade vor dem Bauch ausstrecken und Handflächen zusammen<br>" +
                " - Körper ausbalancieren und das Gleichgewicht halten",
                " - Körper nun vollständig anspannen und den Oberkörper so weit wie möglich zur Seite drehen<br>" +
                " - die Bewegung findet im Rumpf statt<br>" +
                " - Hüfte und Beine bleiben nahezu unbewegt<br>" +
                " - nun drehst du dich kontrolliert zur entgegengesetzten Seite<br>" +
                " - Kopf stets in Verlängerung der Wirbelsäule halten<br>" +
                " - Ausführung der Bauchmuskelübung mehrmals wiederholen",
                Uri.parse("android.resource://com.example.kevin.smarttracker/" +R.raw.russiantwist)));
    }
}