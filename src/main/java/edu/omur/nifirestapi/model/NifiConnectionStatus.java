package edu.omur.nifirestapi.model;

import edu.omur.nifirestapi.utility.DateTimeUtility;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
public final class NifiConnectionStatus extends NifiBaseStatusModel {
    private String sourceName;
    private String destinationName;
    private int percentUseCount;

    public NifiConnectionStatus() {
        this.objectType = Constants.OBJECT_TYPE_CONNECTION;
        this.timestamp = DateTimeUtility.getCurrentTimestampAsZoneDate();
    }

    @Override
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = super.convertToMap();
        map.put("sourceName", sourceName);
        map.put("destinationName", destinationName);
        map.put("percentUseCount", percentUseCount);
        return map;
    }
}
