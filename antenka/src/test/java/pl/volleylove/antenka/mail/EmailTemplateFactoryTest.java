package pl.volleylove.antenka.mail;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTemplateFactoryTest {

    @Test
    void getOrganizerOptOutTemplateTest() {

        String matchName = "Mecz Charytatywny";
        String htmlResult = EmailTemplateFactory.getOrganizerOptOutTemplate(matchName);

        assertTrue(htmlResult.contains(matchName));
    }

    @Test
    void getPlayerOptOutTemplateTest() {

        String matchName = "Mecz Informatyków";
        String htmlResult = EmailTemplateFactory.getPlayerOptOutTemplate(matchName);

        assertTrue(htmlResult.contains(matchName));
    }

}
