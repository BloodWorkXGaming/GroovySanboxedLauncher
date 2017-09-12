package de.bloodworkxgaming.groovysandboxedlauncher.events;

import java.util.ArrayList;
import java.util.List;

public class EventList<T> {
    private List<IGSLEvent<T>> eventList = new ArrayList<>();

    public void registerEvent(IGSLEvent<T> event){
        eventList.add(event);
    }

    public void clear(){
        eventList.clear();
    }

    public void callOnEach(T eventObject){
        for (IGSLEvent<T> tigslEvent : eventList) {
            tigslEvent.handleEvent(eventObject);
        }
    }
}
