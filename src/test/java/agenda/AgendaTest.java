package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgendaTest {
    Agenda agenda;

    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // January 5, 2021
    LocalDate jan_5_2021 = LocalDate.of(2021, 1, 5);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // Un événement simple
    // November 1st, 2020, 22:30, 120 minutes
    Event simple;

    // Un événement qui se répète toutes les semaines et se termine à une date
    // donnée
    Event fixedTermination;

    // Un événement qui se répète toutes les semaines et se termine après un nombre
    // donné d'occurrences
    Event fixedRepetitions;

    // A daily repetitive event, never ending
    // Un événement répétitif quotidien, sans fin
    // November 1st, 2020, 22:30, 120 minutes
    Event neverEnding;

    @BeforeEach
    public void setUp() {
        simple = new Event("Simple event", nov_1_2020_22_30, min_120);

        fixedTermination = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedTermination.setRepetition(ChronoUnit.WEEKS);
        fixedTermination.setTermination(jan_5_2021);

        fixedRepetitions = new Event("Fixed termination weekly", nov_1_2020_22_30, min_120);
        fixedRepetitions.setRepetition(ChronoUnit.WEEKS);
        fixedRepetitions.setTermination(10);

        neverEnding = new Event("Never Ending", nov_1_2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);

        agenda = new Agenda();
        agenda.addEvent(simple);
        agenda.addEvent(fixedTermination);
        agenda.addEvent(fixedRepetitions);
        agenda.addEvent(neverEnding);
    }

    @Test
    public void testMultipleEventsInDay() {
        assertEquals(4, agenda.eventsInDay(nov_1_2020).size(),
                "Il y a 4 événements ce jour là");
        assertTrue(agenda.eventsInDay(nov_1_2020).contains(neverEnding));
    }

    @Test
    public void testNoEventsOnOtherDay() {
        LocalDate other_day = LocalDate.of(2020, 10, 30);
        assertEquals(0, agenda.eventsInDay(other_day).size(),
                "Aucun événement ce jour là");
    }

    @Test
    public void testEventAfterTerminationDate() {
        LocalDate after_termination = LocalDate.of(2021, 1, 10);
        assertEquals(1, agenda.eventsInDay(after_termination).size(),
                "L'événement sans fin est présent après la terminaison des autres");
    }

    @Test
    public void testEventWithinTerminationRange() {
        LocalDate within_range = LocalDate.of(2020, 12, 13);
        assertEquals(3, agenda.eventsInDay(within_range).size(),
                "3 événements répétitifs (2 avec terminaison + 1 sans fin) dans la plage");
    }

    @Test
    public void testFindByTitle() {
        var simpleEvents = agenda.findByTitle("Simple event");
        assertEquals(1, simpleEvents.size(),
                "Il y a 1 événement avec ce titre");
        assertTrue(simpleEvents.contains(simple));
    }

    @Test
    public void testFindByTitleMultiple() {
        var fixedTerminationEvents = agenda.findByTitle("Fixed termination weekly");
        assertEquals(2, fixedTerminationEvents.size(),
                "Il y a 2 événements avec ce titre");
    }

    @Test
    public void testFindByTitleNotFound() {
        var notFound = agenda.findByTitle("Non-existent event");
        assertEquals(0, notFound.size(),
                "Aucun événement ");
    }

    @Test
    public void testIsFreeForNoConflict() {
        LocalDateTime freeTime = LocalDateTime.of(2020, 11, 2, 10, 0);
        Duration shortDuration = Duration.ofMinutes(30);
        Event freeEvent = new Event("Free event", freeTime, shortDuration);

        assertTrue(agenda.isFreeFor(freeEvent),
                "pas de conflit ");
    }

    @Test
    public void testIsFreeForConflict() {
        LocalDateTime conflictTime = LocalDateTime.of(2020, 11, 1, 23, 0);
        Duration duration = Duration.ofMinutes(60);
        Event conflictEvent = new Event("Conflict event", conflictTime, duration);

        assertFalse(agenda.isFreeFor(conflictEvent),
                "Il y a un conflit avec l'événement simple");
    }

    @Test
    public void testIsFreeForIgnoresRepeatingEvents() {
        LocalDateTime nearRepeatEvent = LocalDateTime.of(2020, 11, 8, 22, 30);
        Duration duration = Duration.ofMinutes(60);
        Event event = new Event("Test event", nearRepeatEvent, duration);

        assertTrue(agenda.isFreeFor(event),
                "Les événements répétitifs sont ignorés ");
    }

    @Test
    public void testAddMultipleEvents() {
        Agenda newAgenda = new Agenda();
        Event event1 = new Event("Event 1", nov_1_2020_22_30, min_120);
        Event event2 = new Event("Event 2", LocalDateTime.of(2020, 11, 1, 10, 0), Duration.ofMinutes(60));

        newAgenda.addEvent(event1);
        newAgenda.addEvent(event2);

        assertEquals(2, newAgenda.eventsInDay(nov_1_2020).size(),
                "Les 2 événements ajoutés sont présents");
    }

    @Test
    public void testEmptyAgenda() {
        Agenda emptyAgenda = new Agenda();
        assertEquals(0, emptyAgenda.eventsInDay(nov_1_2020).size(),
                " agenda vide ");
    }

    @Test
    public void testIsFreeForPartialOverlap() {
        LocalDateTime overlapStart = LocalDateTime.of(2020, 11, 1, 23, 0);
        Duration overlapDuration = Duration.ofMinutes(60);
        Event overlappingEvent = new Event("test", overlapStart, overlapDuration);

        assertFalse(agenda.isFreeFor(overlappingEvent),
                "Un événement en chevauchement partiel génère un conflit");
    }

    @Test
    public void testIsFreeForExactStart() {
        LocalDateTime exactEnd = nov_1_2020_22_30.plus(min_120);
        Duration nextEventDuration = Duration.ofMinutes(60);
        Event nextEvent = new Event("Next event", exactEnd, nextEventDuration);

        assertTrue(agenda.isFreeFor(nextEvent),
                "Un événement démarrant à la fin d'un autre pas de conflit");
    }

    @Test
    public void testIsFreeForBeforeAllEvents() {
        LocalDateTime beforeTime = nov_1_2020_22_30.minusHours(5);
        Duration duration = Duration.ofMinutes(60);
        Event beforeEvent = new Event("Before", beforeTime, duration);

        assertTrue(agenda.isFreeFor(beforeEvent),
                "Un événement entièrement avant ne crée pas de conflit");
    }

    @Test
    public void testIsFreeForStartExactlyAtAnother() {
        Agenda testAgenda = new Agenda();
        LocalDateTime time1 = LocalDateTime.of(2020, 11, 1, 10, 0);
        Event event1 = new Event("Event 1", time1, Duration.ofMinutes(60));
        testAgenda.addEvent(event1);

        Event event2 = new Event("Event 2", time1, Duration.ofMinutes(60));
        assertFalse(testAgenda.isFreeFor(event2),
                "Un événement commençant au même moment crée un conflit");
    }

    @Test
    public void testIsFreeForEndExactlyWhenAnotherStarts() {
        Agenda testAgenda = new Agenda();
        LocalDateTime time1 = LocalDateTime.of(2020, 11, 1, 10, 0);
        Event event1 = new Event("Event 1", time1, Duration.ofMinutes(60));
        testAgenda.addEvent(event1);

        LocalDateTime time2 = time1.minusMinutes(30);
        Event event2 = new Event("Event 2", time2, Duration.ofMinutes(30));
        assertTrue(testAgenda.isFreeFor(event2),
                "Un événement finissant exactement quand commence un autre n'a pas de conflit");
    }

    @Test
    public void testIsFreeForAfterAllEvents() {
        LocalDateTime afterTime = nov_1_2020_22_30.plus(min_120).plusHours(1);
        Duration duration = Duration.ofMinutes(60);
        Event afterEvent = new Event("After", afterTime, duration);

        assertTrue(agenda.isFreeFor(afterEvent),
                "Un événement entièrement après ne crée pas de conflit");
    }

}