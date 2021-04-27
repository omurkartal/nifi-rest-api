package edu.omur.nifirestapi.model;

import edu.omur.nifirestapi.utility.DateTimeUtility;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class NifiFlowFile {
    private String id;
    private String name;
    private int position;
    private long size;
    private long queuedDurationInMinutes;
    private String queuedDurationText;
    private String clusterNodeAddress;
    private String objectType;
    private String connectionId;
    private String connectionName;
    private String processGroupHierarchy;
    protected ZonedDateTime timestamp;

    public NifiFlowFile() {
        objectType = Constants.OBJECT_TYPE_FLOW_FILE;
        timestamp = DateTimeUtility.getCurrentTimestampAsZoneDate();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("position", position);
        map.put("size", size);
        map.put("queuedDurationInMinutes", queuedDurationInMinutes);
        map.put("queuedDurationText", queuedDurationText);
        map.put("clusterNodeAddress", clusterNodeAddress);
        map.put("objectType", objectType);
        map.put("connectionId", connectionId);
        map.put("connectionName", connectionName);
        map.put("processGroupHierarchy", processGroupHierarchy);
        map.put("timestamp", timestamp.toString());
        return map;
    }
}
