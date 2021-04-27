package edu.omur.nifirestapi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public abstract class NifiBaseStatusModel {
    protected String id;
    protected String name;
    protected long queuedCount;
    protected long queuedSize;
    protected String queuedSizeText;
    protected String objectType;
    protected String processGroupHierarchy;
    protected ZonedDateTime timestamp;

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("queuedCount", queuedCount);
        map.put("queuedSize", queuedSize);
        map.put("queuedSizeText", queuedSizeText);
        map.put("objectType", objectType);
        map.put("processGroupHierarchy", processGroupHierarchy);
        map.put("timestamp", timestamp.toString());
        return map;
    }
}