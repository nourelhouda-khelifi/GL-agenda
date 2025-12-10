package agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste des événements répétitifs sans fin, mais avec des exceptions
 */
public class NoTerminationEventTest {
    // November 1st, 2020
    LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

    // November 1st, 2020, 22:30
    LocalDateTime nov_1__2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

    // 120 minutes
    Duration min_120 = Duration.ofMinutes(120);

    // Un événement répétitif quotidien, sans fin
    Event neverEnding;

    @BeforeEach
    void setUp() {
        // November 1st, 2020, 22:30, 120 minutes
        neverEnding = new Event("Never Ending", nov_1__2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);
    }

    @Test
    public void eventIsInItsStartDay() {
        assertTrue(neverEnding.isInDay(nov_1_2020),
                "Un événement a lieu dans son jour de début");
    }

    @Test
    public void eventIsNotInDayBefore() {
        assertFalse(neverEnding.isInDay(nov_1_2020.minusDays(1)),
                "Un événement n'a pas lieu avant son jour de début");
    }

    @Test
    public void eventOccurs10DayAfter() {
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(10)),
                "Cet événement doit se produire tous les jours");
    }

    @Test
    public void eventIsNotInExceptionDays() {
        neverEnding.addException(nov_1_2020.plusDays(2)); // ne se produit pas à J+2
        neverEnding.addException(nov_1_2020.plusDays(4)); // ne se produit pas à J+4
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(1)),
                "Cet événement se produit tous les jours sauf exceptions");
        assertFalse(neverEnding.isInDay(nov_1_2020.plusDays(2)),
                "Cet événement ne se produit pas à J+2");
        assertTrue(neverEnding.isInDay(nov_1_2020.plusDays(3)),
                "Cet événement se produit tous les jours sauf exceptions");
        assertFalse(neverEnding.isInDay(nov_1_2020.plusDays(4)),
                "Cet événement ne se produit pas à J+4");
    }

    @Test
    public void weeklyRepetitionNeverEnding() {
        Event weeklyNeverEnding = new Event("Weekly Never Ending", nov_1__2020_22_30, min_120);
        weeklyNeverEnding.setRepetition(ChronoUnit.WEEKS);

        assertTrue(weeklyNeverEnding.isInDay(nov_1_2020),
                "Événement le 1er novembre");
        assertTrue(weeklyNeverEnding.isInDay(nov_1_2020.plusWeeks(1)),
                "Événement une semaine après");
        assertTrue(weeklyNeverEnding.isInDay(nov_1_2020.plusWeeks(10)),
                "Événement 10 semaines après");
        assertFalse(weeklyNeverEnding.isInDay(nov_1_2020.plusDays(1)),
                "Ne doit pas être le jour suivant (pas aligné avec semaines)");
    }

    @Test
    public void monthlyRepetitionNeverEnding() {
        Event monthlyNeverEnding = new Event("Monthly Never Ending", nov_1__2020_22_30, min_120);
        monthlyNeverEnding.setRepetition(ChronoUnit.MONTHS);

        assertTrue(monthlyNeverEnding.isInDay(nov_1_2020),
                "Novembre 2020");
        assertTrue(monthlyNeverEnding.isInDay(LocalDate.of(2020, 12, 1)),
                "Décembre 2020");
        assertTrue(monthlyNeverEnding.isInDay(LocalDate.of(2021, 1, 1)),
                "Janvier 2021");
        assertTrue(monthlyNeverEnding.isInDay(LocalDate.of(2025, 11, 1)),
                "Novembre 2025");
        assertFalse(monthlyNeverEnding.isInDay(nov_1_2020.plusDays(1)),
                "Ne doit pas être le jour suivant (pas aligné avec mois)");
    }

    @Test
    public void weeklyRepetitionNotAlignedDay() {
        Event weeklyEvent = new Event("Weekly", nov_1__2020_22_30, min_120);
        weeklyEvent.setRepetition(ChronoUnit.WEEKS);

        assertTrue(weeklyEvent.isInDay(nov_1_2020),
                "Le jour de départ");
        assertFalse(weeklyEvent.isInDay(LocalDate.of(2020, 11, 3)),
                "Jour pas aligné sur 7 jours");
        assertFalse(weeklyEvent.isInDay(LocalDate.of(2020, 11, 4)),
                "Jour pas aligné sur 7 jours");
    }

    @Test
    public void dailyRepetitionFarInFuture() {
        Event dailyEvent = new Event("Daily far", nov_1__2020_22_30, min_120);
        dailyEvent.setRepetition(ChronoUnit.DAYS);

        assertTrue(dailyEvent.isInDay(nov_1_2020),
                "Le jour de départ");
        assertTrue(dailyEvent.isInDay(nov_1_2020.plusDays(1)),
                "Le jour suivant");
        assertTrue(dailyEvent.isInDay(nov_1_2020.plusDays(365)),
                "Un an après");
        assertFalse(dailyEvent.isInDay(nov_1_2020.minusDays(1)),
                "Le jour avant ne doit pas être couvert");
    }

    @Test
    public void noTerminationEventGetNumberOfOccurrences() {
        Event neverEnding = new Event("Never Ending", nov_1__2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.DAYS);
        // Sans termination, doit retourner Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, neverEnding.getNumberOfOccurrences(),
                "Événement sans termination doit retourner Integer.MAX_VALUE");
    }

    @Test
    public void noTerminationEventGetTerminationDate() {
        Event neverEnding = new Event("Never Ending", nov_1__2020_22_30, min_120);
        neverEnding.setRepetition(ChronoUnit.WEEKS);
        // Sans termination, doit retourner null
        assertNull(neverEnding.getTerminationDate(),
                "Événement sans termination doit retourner null pour getTerminationDate");
    }

}
