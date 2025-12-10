package agenda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Teste des événements répétitifs avec une date de terminaison, et des
 * exceptions
 */
public class FixedTerminationEventTest {

        // November 1st, 2020
        LocalDate nov_1_2020 = LocalDate.of(2020, 11, 1);

        // November 1st, 2020, 22:30
        LocalDateTime nov_1__2020_22_30 = LocalDateTime.of(2020, 11, 1, 22, 30);

        // January 5, 2021
        LocalDate jan_5_2021 = LocalDate.of(2021, 1, 5);

        // 120 minutes
        Duration min_120 = Duration.ofMinutes(120);

        // Un événement qui se répète toutes les semaines et se termine à une date
        // donnée
        Event fixedTermination;

        // Un événement qui se répète toutes les semaines et se termine après un nombre
        // donné d'occurrences
        Event fixedRepetitions;

        @BeforeEach
        void setUp() {
                fixedTermination = new Event("Fixed termination weekly", nov_1__2020_22_30, min_120);
                fixedTermination.setRepetition(ChronoUnit.WEEKS);
                fixedTermination.setTermination(jan_5_2021);

                fixedRepetitions = new Event("Fixed termination weekly", nov_1__2020_22_30, min_120);
                fixedRepetitions.setRepetition(ChronoUnit.WEEKS);
                fixedRepetitions.setTermination(10);
        }

        @Test
        public void canCalculateNumberOfOccurrencesFromTerminationDate() {
                assertEquals(10, fixedTermination.getNumberOfOccurrences(),
                                "Cet événement répéter 10 fois");
        }

        @Test
        public void canCalculateTerminationDateFromNumberOfOccurrences() {
                LocalDate termination = LocalDate.of(2021, 1, 3);
                assertEquals(termination, fixedRepetitions.getTerminationDate(),
                                "Cet événement doit terminer le 3 janvier");
        }

        @Test
        public void occursInTerminationDay() {
                LocalDate lastDay = nov_1_2020.plusWeeks(9);
                assertTrue(fixedRepetitions.isInDay(lastDay),
                                "Cet événement a lieu  jour de sa terminaison");
        }

        @Test
        public void eventIsInItsStartDay() {
                assertTrue(fixedTermination.isInDay(nov_1_2020),
                                "Un événement a lieu dans son jour de début");
                assertTrue(fixedRepetitions.isInDay(nov_1_2020),
                                "Un événement a lieu dans son jour de début");
        }

        @Test
        public void eventIsNotInDayBefore() {
                assertFalse(fixedTermination.isInDay(nov_1_2020.minusDays(1)),
                                "Un événement n'a pas lieu avant son jour de début");
                assertFalse(fixedRepetitions.isInDay(nov_1_2020.minusDays(1)),
                                "Un événement n'a pas lieu avant son jour de début");
        }

        @Test
        public void eventOccurs10WeeksAfter() {
                assertTrue(fixedTermination.isInDay(nov_1_2020.plusWeeks(9)),
                                "Cet événement se produit toutes les semaines");
                assertTrue(fixedRepetitions.isInDay(nov_1_2020.plusWeeks(9)),
                                "Cet événement se produit toutes les semaines");
        }

        @Test
        public void eventIsNotInExceptionDays() {
                fixedTermination.addException(nov_1_2020.plusWeeks(2)); // ne se produit pas à W+2
                fixedTermination.addException(nov_1_2020.plusWeeks(4)); // ne se produit pas à W+4
                assertTrue(fixedTermination.isInDay(nov_1_2020.plusWeeks(1)),
                                "Cet événement se produit toutes les semaines");
                assertFalse(fixedTermination.isInDay(nov_1_2020.plusWeeks(2)),
                                "Cet événement ne se produit pas à W+2");
                assertTrue(fixedTermination.isInDay(nov_1_2020.plusWeeks(3)),
                                "Cet événement se produit toutes les semaines");
                assertFalse(fixedTermination.isInDay(nov_1_2020.plusWeeks(4)),
                                "Cet événement ne se produit pas à W+4");
        }

        @Test
        public void dailyRepetitionWithFixedTermination() {
                Event dailyEvent = new Event("Daily with termination", nov_1__2020_22_30, min_120);
                dailyEvent.setRepetition(ChronoUnit.DAYS);
                dailyEvent.setTermination(LocalDate.of(2020, 11, 5));

                assertTrue(dailyEvent.isInDay(nov_1_2020),
                                "Événement le 1er novembre");
                assertTrue(dailyEvent.isInDay(LocalDate.of(2020, 11, 2)),
                                "Événement le 2 novembre");
                assertTrue(dailyEvent.isInDay(LocalDate.of(2020, 11, 5)),
                                "Événement le 5 novembre (terminaison)");
                assertFalse(dailyEvent.isInDay(LocalDate.of(2020, 11, 6)),
                                "Événement ne doit pas être après terminaison");
        }

        @Test
        public void monthlyRepetitionWithFixedTermination() {
                Event monthlyEvent = new Event("Monthly with termination", nov_1__2020_22_30, min_120);
                monthlyEvent.setRepetition(ChronoUnit.MONTHS);
                monthlyEvent.setTermination(LocalDate.of(2021, 2, 1));

                assertTrue(monthlyEvent.isInDay(nov_1_2020),
                                "Novembre 2020");
                assertTrue(monthlyEvent.isInDay(LocalDate.of(2020, 12, 1)),
                                "Décembre 2020");
                assertTrue(monthlyEvent.isInDay(LocalDate.of(2021, 1, 1)),
                                "Janvier 2021");
                assertTrue(monthlyEvent.isInDay(LocalDate.of(2021, 2, 1)),
                                "Février 2021 (terminaison)");
                assertFalse(monthlyEvent.isInDay(LocalDate.of(2021, 3, 1)),
                                "Mars 2021 (après terminaison)");
                assertFalse(monthlyEvent.isInDay(nov_1_2020.plusDays(1)),
                                "Ne doit pas être aligné sur jours autres que le 1er");
        }

        @Test
        public void weeklyRepetitionNotAlignedDay() {
                Event weeklyEvent = new Event("Weekly with term", nov_1__2020_22_30, min_120);
                weeklyEvent.setRepetition(ChronoUnit.WEEKS);
                weeklyEvent.setTermination(LocalDate.of(2021, 1, 10));

                assertTrue(weeklyEvent.isInDay(nov_1_2020),
                                "Le jour de départ");
                assertFalse(weeklyEvent.isInDay(LocalDate.of(2020, 11, 3)),
                                "Jour pas aligné sur 7 jours");
                assertFalse(weeklyEvent.isInDay(LocalDate.of(2020, 11, 4)),
                                "Jour pas aligné sur 7 jours");
        }

        @Test
        public void dailyRepetitionWithTerminationJustAfter() {
                Event dailyEvent = new Event("Daily term", nov_1__2020_22_30, min_120);
                dailyEvent.setRepetition(ChronoUnit.DAYS);
                dailyEvent.setTermination(LocalDate.of(2020, 11, 5));

                assertTrue(dailyEvent.isInDay(nov_1_2020),
                                "Premier jour");
                assertTrue(dailyEvent.isInDay(LocalDate.of(2020, 11, 3)),
                                "Jour au milieu");
                assertTrue(dailyEvent.isInDay(LocalDate.of(2020, 11, 5)),
                                "Jour de terminaison");
                assertFalse(dailyEvent.isInDay(LocalDate.of(2020, 11, 6)),
                                "Jour juste après terminaison");
        }

        @Test
        public void weeklyWithExceptionsAndTermination() {
                Event weeklyEvent = new Event("Weekly exc term", nov_1__2020_22_30, min_120);
                weeklyEvent.setRepetition(ChronoUnit.WEEKS);
                weeklyEvent.setTermination(LocalDate.of(2020, 12, 15));

                weeklyEvent.addException(nov_1_2020.plusWeeks(2));

                assertTrue(weeklyEvent.isInDay(nov_1_2020),
                                "Première semaine");
                assertTrue(weeklyEvent.isInDay(nov_1_2020.plusWeeks(1)),
                                "Deuxième semaine");
                assertFalse(weeklyEvent.isInDay(nov_1_2020.plusWeeks(2)),
                                "Exception : ne doit pas être là");
                assertTrue(weeklyEvent.isInDay(nov_1_2020.plusWeeks(3)),
                                "Troisième semaine");
                assertFalse(weeklyEvent.isInDay(LocalDate.of(2021, 1, 1)),
                                "Après terminaison");
        }

        @Test
        public void monthlyWithTerminationNotAligned() {
                Event monthlyEvent = new Event("Monthly not aligned", nov_1__2020_22_30, min_120);
                monthlyEvent.setRepetition(ChronoUnit.MONTHS);
                monthlyEvent.setTermination(LocalDate.of(2021, 2, 1));

                assertTrue(monthlyEvent.isInDay(nov_1_2020),
                                "1er novembre");
                assertFalse(monthlyEvent.isInDay(LocalDate.of(2020, 11, 15)),
                                "15 novembre pas aligné (pas sur le 1er)");
                assertTrue(monthlyEvent.isInDay(LocalDate.of(2020, 12, 1)),
                                "1er décembre");
                assertFalse(monthlyEvent.isInDay(LocalDate.of(2020, 12, 2)),
                                "2 décembre pas aligné");
        }

        @Test
        public void repetitionWithoutTermination() {
                Event dailyEvent = new Event("No term", nov_1__2020_22_30, min_120);
                dailyEvent.setRepetition(ChronoUnit.DAYS);

                assertTrue(dailyEvent.isInDay(nov_1_2020),
                                "Jour de début");
                assertTrue(dailyEvent.isInDay(nov_1_2020.plusDays(100)),
                                "Jour loin dans le futur (pas de terminaison)");
                assertTrue(dailyEvent.isInDay(nov_1_2020.plusDays(1000)),
                                "Jour très loin dans le futur (pas de terminaison)");
        }

        @Test
        public void repetitionHasRepetition() {
                assertTrue(fixedTermination.hasRepetition(),
                                "Un événement avec répétition doit retourner true");
                assertTrue(fixedRepetitions.hasRepetition(),
                                "Un événement avec répétition doit retourner true");
        }

}