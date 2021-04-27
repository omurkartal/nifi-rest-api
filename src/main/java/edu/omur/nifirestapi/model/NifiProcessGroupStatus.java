package edu.omur.nifirestapi.model;

import edu.omur.nifirestapi.utility.DateTimeUtility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
public final class NifiProcessGroupStatus extends NifiBaseStatusModel {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean hasInnerProcessGroup;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean isExcluded;

    public NifiProcessGroupStatus() {
        this.objectType = Constants.OBJECT_TYPE_PROCESS_GROUP;
        this.timestamp = DateTimeUtility.getCurrentTimestampAsZoneDate();
    }

    @Override
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = super.convertToMap();
        map.put("hasInnerProcessGroup", hasInnerProcessGroup);
        map.put("isExcluded", isExcluded);
        return map;
    }

    public boolean hasInnerProcessGroup() {
        return hasInnerProcessGroup;
    }

    public void hasInnerProcessGroup(boolean flag) {
        hasInnerProcessGroup = flag;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public void isExcluded(boolean flag) {
        isExcluded = flag;
    }
}
