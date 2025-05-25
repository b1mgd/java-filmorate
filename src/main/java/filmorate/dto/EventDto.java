package filmorate.dto;

import lombok.Data;
import filmorate.model.EventType;
import filmorate.model.Operation;

@Data
public class EventDto {
    private long id;
    private long timestamp;
    private long userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private long entityId;
}
