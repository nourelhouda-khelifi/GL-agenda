package agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Description : An agenda that stores events
 */
public class Agenda {

    /**
     * Adds an event to this agenda
     *
     * @param e the event to add
     */
    private final List<Event> events = new ArrayList<>();

    public void addEvent(Event e) {
        // TODO : implémenter cette méthode
        events.add(e);
    }

    /**
     * Computes the events that occur on a given day
     *
     * @param day the day toi test
     * @return a list of events that occur on that day
     */
    public List<Event> eventsInDay(LocalDate day) {
        // TODO : implémenter cette méthode
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (e.isInDay(day))
                res.add(e);
        }
        return res;
    }

    public List<Event> findByTitle(String title) {
        List<Event> res = new ArrayList<>();
        for (Event e : events) {
            if (e.getTitle().equals(title))
                res.add(e);
        }
        return res;
    }

    public boolean isFreeFor(Event e) {
        LocalDateTime s1 = e.getStart();
        LocalDateTime e1 = e.getStart().plus(e.getDuration());

        for (Event other : events) {
            if (other.hasRepetition())
                continue;

            LocalDateTime s2 = other.getStart();
            LocalDateTime e2 = other.getStart().plus(other.getDuration());

            // Conflit si les deux intervalles se chevauchent strictement (sans se toucher)
            if (e1.isAfter(s2) && e2.isAfter(s1))
                return false;
        }
        return true;
    }
}
