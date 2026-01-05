package interfaces;

import enums.Event;

public interface Publisher {
    void addSubscriber(Subscriber s);
    void removeSubscriber(Subscriber s);
    void notify(Event event, Object... args);
}
