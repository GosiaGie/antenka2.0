package pl.volleylove.antenka.enums;

public enum SigningUpEndReason {
    // OUT_OF_SLOTS = every Event's Slot has Player applied on it
    // ENDED_BY_ORGANIZER = Event is still active, but no one more can sign up; isActive still has value 'true'
    // CANCELLED = Event was cancelled by Organizer or Admin
    OUT_OF_FREE_SLOTS, ENDED_BY_ORGANIZER, CANCELLED
}
