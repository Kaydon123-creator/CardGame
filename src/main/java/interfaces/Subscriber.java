package interfaces;

import enums.Event;

public interface Subscriber {
    void update(Event event, Object... args);
}
