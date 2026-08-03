package pl.volleylove.antenka.mail;

public class EmailTemplateFactory {

    private EmailTemplateFactory(){}

    public static final String SUBJECT_ORGANIZER_PLAYER_OPT_OUT = "🏐 Zmiana w Twoim meczu: ";
    public static final String SUBJECT_PLAYER_OPT_OUT = "🏐 Potwierdzenie wypisania z meczu: ";

    public static String getOrganizerOptOutTemplate(String matchName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; padding: 20px; color: #333;">
                <div style="max-width: 600px; background: white; margin: 0 auto; padding: 20px; border-radius: 8px; border-top: 5px solid #ff9800;">
                    <h2 style="color: #ff9800; text-align: center;">🏐 Team Antenka Info</h2>
                    <p>Cześć!</p>
                    <p>Informujemy, że jeden z graczy właśnie <strong>wypisał się</strong> z Twojego meczu: <span style="color: #ff9800; font-weight: bold;">%s</span>.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin-top: 30px;">
                    <p style="font-size: 11px; color: #777; text-align: center;">Wiadomość wygenerowana automatycznie przez aplikację Antenka 2.0</p>
                </div>
            </body>
            </html>
            """.formatted(matchName);
    }

    public static String getPlayerOptOutTemplate(String matchName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f9; padding: 20px; color: #333;">
                <div style="max-width: 600px; background: white; margin: 0 auto; padding: 20px; border-radius: 8px; border-top: 5px solid #2196F3;">
                    <h2 style="color: #2196F3; text-align: center;">🏐 Potwierdzenie rezygnacji</h2>
                    <p>Cześć!</p>
                    <p>Potwierdzamy, że pomyślnie <strong>wypisałeś się</strong> z meczu: <span style="color: #2196F3; font-weight: bold;">%s</span>.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin-top: 30px;">
                    <p style="font-size: 11px; color: #777; text-align: center;">Wiadomość wygenerowana automatycznie przez aplikację Antenka 2.0</p>
                </div>
            </body>
            </html>
            """.formatted(matchName);
    }

}
