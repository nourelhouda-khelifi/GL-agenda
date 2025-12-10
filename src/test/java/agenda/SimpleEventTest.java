package agenda;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste des événements simples, sans répétition
 */
public class SimpleEventTest {
    public static final String SIMPLE_EVENT = "Simple event";
    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1_2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // 89 minutes
    Duration min_89 = Duration.ofMinutes(89);

    // Un événement simple
    // November 1st, 2020, 22:30, 89 minutes
    Event simple = new Event(SIMPLE_EVENT, nov_1_2020_22_30, min_89);

    // Un événement qui chevauche 2 jours
    // November 1st, 2020, 22:30, 120 minutes
    Event overlapping = new Event("Overlapping event", nov_1_2020_22_30, min_120);

    @Test
    public void eventIsInItsStartDay() {
        assertTrue(simple.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
        assertTrue(overlapping.isInDay(nov_1_2020), "Un événement a lieu dans son jour de début");
    }

    @Test
    public void eventIsNotInDayBefore() {
        assertFalse(simple.isInDay(nov_1_2020.minusDays(1)), "Un événement n'a pas lieu avant son jour de début");
        assertFalse(overlapping.isInDay(nov_1_2020.minusDays(1)), "Un événement n'a pas lieu avant son jour de début");
    }

    @Test
    public void overlappingEventIsInDayAfter() {
        assertFalse(simple.isInDay(nov_1_2020.plusDays(1)), "Cet événement ne déborde pas sur le jour suivant");
        assertTrue(overlapping.isInDay(nov_1_2020.plusDays(1)), "Cet événement déborde sur le jour suivant");
    }

    @Test
    public void toStringShowsEventTitle() {
        assertTrue(simple.toString().contains(SIMPLE_EVENT),
                "toString() doit montrer le titre de l'événement");
    }

    @Test
    public void simpleEventFarInPast() {
        LocalDate farPast = nov_1_2020.minusDays(100);
        assertFalse(simple.isInDay(farPast),
                "Un événement ne doit pas être bien avant son début");
    }

    @Test
    public void simpleEventFarInFuture() {
        LocalDate farFuture = nov_1_2020.plusDays(100);
        assertFalse(simple.isInDay(farFuture),
                "Un événement ne doit pas être bien après sa fin");
    }

    @Test
    public void eventWithZeroDuration() {
        Duration zeroDuration = Duration.ofMinutes(0);
        Event zeroEvent = new Event("Zero duration", nov_1_2020_22_30, zeroDuration);

        assertTrue(zeroEvent.isInDay(nov_1_2020),
                "Un événement de durée zéro doit être dans son jour");
        assertFalse(zeroEvent.isInDay(nov_1_2020.plusDays(1)),
                "Un événement de durée zéro ne doit pas déborder");
    }

    @Test
    public void addExceptionOnSimpleEventDoesNothing() {
        simple.addException(nov_1_2020);
        assertTrue(simple.isInDay(nov_1_2020),
                "addException sur événement sans répétition ne change rien");
    }

    @Test
    public void setTerminationWithDateOnSimpleEventDoesNothing() {
        simple.setTermination(nov_1_2020.plusDays(10));
        assertTrue(simple.isInDay(nov_1_2020),
                "setTermination sur événement sans répétition ne change rien");
    }

    @Test
    public void setTerminationWithCountOnSimpleEventDoesNothing() {
        simple.setTermination(5);
        assertTrue(simple.isInDay(nov_1_2020),
                "setTermination sur événement sans répétition ne change rien");
    }

    @Test
    public void eventEndingExactlyAtMidnight() {
        LocalDateTime endAtMidnight = nov_1_2020.atStartOfDay().plusDays(1);
        LocalDateTime startTime = endAtMidnight.minusSeconds(1);
        Duration duration = Duration.between(startTime, endAtMidnight);
        Event event = new Event("Ends at midnight", startTime, duration);

        assertTrue(event.isInDay(nov_1_2020),
                "Doit être dans le jour de début");
        assertTrue(event.isInDay(nov_1_2020.plusDays(1)),
                "Déborde sur jour suivant (finit à minuit)");
    }

    @Test
    public void eventCompletelyAfterDate() {
        Event event = new Event("Much later", nov_1_2020_22_30.plusDays(10), min_120);

        assertFalse(event.isInDay(nov_1_2020),
                "Événement bien après n'est pas le 1er novembre");
        assertTrue(event.isInDay(nov_1_2020.plusDays(10)),
                "Événement le 11 novembre est dedans");
    }

    @Test
    public void eventEndsExactlyAtStartOfDay() {
        LocalDateTime endTime = nov_1_2020.atStartOfDay();
        LocalDateTime startTime = endTime.minusMinutes(30);
        Duration duration = Duration.between(startTime, endTime);
        Event event = new Event("Ends at start", startTime, duration);

        assertTrue(event.isInDay(nov_1_2020.minusDays(1)),
                "Événement qui finit à minuit du jour commence doit être inclus");
    }

    @Test
    public void eventBoundaryConditions() {
        LocalDate day1 = nov_1_2020;
        LocalDate day2 = nov_1_2020.plusDays(2);
        LocalDateTime start = day1.atTime(10, 0);
        Duration duration = Duration.ofDays(1).plusHours(12);
        Event event = new Event("Boundary", start, duration);

        assertTrue(event.isInDay(day1),
                "Jour de début inclus");
        assertTrue(event.isInDay(day1.plusDays(1)),
                "Jour intermédiaire inclus");
        assertFalse(event.isInDay(day2),
                "Jour après la fin exclus");
    }

    @Test
    public void simpleEventNotHasRepetition() {
        assertFalse(simple.hasRepetition(),
                "Un événement simple ne doit pas avoir de répétition");
    }

    @Test
    public void eventStartsBeforeDay() {
        Event event = new Event("Early start", nov_1_2020_22_30, Duration.ofHours(1));
        assertTrue(event.isInDay(nov_1_2020),
                "Événement démarrant le jour");
        assertFalse(event.isInDay(nov_1_2020.plusDays(1)),
                "Événement ne s'étend pas au jour suivant");
    }

    @Test
    public void simpleEventGetNumberOfOccurrences() {
        // Événement simple sans répétition retourne Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, simple.getNumberOfOccurrences(),
                "Événement simple doit retourner Integer.MAX_VALUE pour getNumberOfOccurrences");
    }

    @Test
    public void simpleEventGetTerminationDate() {
        // Événement simple sans répétition retourne null pour getTerminationDate
        assertNull(simple.getTerminationDate(),
                "Événement simple doit retourner null pour getTerminationDate");
    }

}
