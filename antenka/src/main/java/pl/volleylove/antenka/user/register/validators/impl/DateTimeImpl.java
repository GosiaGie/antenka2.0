package pl.volleylove.antenka.user.register.validators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import pl.volleylove.antenka.user.register.validators.interfaces.DateTime;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeImpl implements DateTime {

    private Clock clock;

    @Autowired
    public DateTimeImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime getDateTime() {
        return LocalDateTime.now();
    }
}
