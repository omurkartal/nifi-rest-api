package edu.omur.nifirestapi.nifi;

import edu.omur.nifirestapi.configuration.NifiProperties;
import edu.omur.nifirestapi.model.NifiBaseStatusModel;
import edu.omur.nifirestapi.model.NifiConnectionStatus;
import edu.omur.nifirestapi.model.NifiProcessGroupStatus;
import org.apache.nifi.web.api.dto.status.*;
import org.apache.nifi.web.api.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class NifiProcessGroupStatusHelper {
    private static final Logger logger = LoggerFactory.getLogger(NifiProcessGroupStatusHelper.class);

    public static List<NifiBaseStatusModel> getObjectStatusList(NifiProperties nifiProperties, String token) {
        String inquiryUrl = nifiProperties.getInquiryUrl().replace("##processGroupId##", nifiProperties.getRootProcessorGroupId());

        ProcessGroupStatusEntity processGroupStatusEntity = NifiRestCallHelper.doRestCall(inquiryUrl
                , "Bearer " + token
                , HttpMethod.GET
                , MediaType.APPLICATION_JSON
                , ""
                , ProcessGroupStatusEntity.class);

        ProcessGroupStatusDTO dto = processGroupStatusEntity.getProcessGroupStatus();
        logger.info("Process Group Id  : {}", dto.getId());
        logger.info("Process Group Name: {}", dto.getName());

        List<ProcessGroupStatusSnapshotDTO> parentList = new ArrayList<>();
        parentList.add(dto.getAggregateSnapshot());

        List<NifiBaseStatusModel> nifiObjectList = new ArrayList<>();
        checkProcessGroupStatus(nifiObjectList, dto.getAggregateSnapshot(), parentList, nifiProperties);

        return nifiObjectList;
    }

    private static void checkProcessGroupStatus(List<NifiBaseStatusModel> nifiObjectList
            , ProcessGroupStatusSnapshotDTO processGroup
            , List<ProcessGroupStatusSnapshotDTO> parentList
            , NifiProperties nifiProperties) {
        logger.info("  Process Group Status  ");

        String processGroupHierarchy = String.join(" » ",
                parentList.stream().map(ProcessGroupStatusSnapshotDTO::getName).collect(Collectors.toList()));
        logger.info("processGroupHierarchy:{}", processGroupHierarchy);

        NifiProcessGroupStatus nifiProcessGroupStatus = createProcessGroupStatus(processGroup, processGroupHierarchy, nifiProperties);
        logger.info(nifiProcessGroupStatus.toString());
        nifiObjectList.add(nifiProcessGroupStatus);

        checkConnectionStatus(nifiObjectList
                , processGroup.getConnectionStatusSnapshots()
                , processGroupHierarchy);

        for (ProcessGroupStatusSnapshotEntity entity : processGroup.getProcessGroupStatusSnapshots()) {
            parentList.add(entity.getProcessGroupStatusSnapshot());
            checkProcessGroupStatus(nifiObjectList, entity.getProcessGroupStatusSnapshot(), parentList, nifiProperties);
            parentList.remove(entity.getProcessGroupStatusSnapshot());
        }

        if (logger.isDebugEnabled()) {
            logProcessorStatus(processGroup.getProcessorStatusSnapshots());
            logPortStatus(processGroup.getInputPortStatusSnapshots(), "Input");
            logPortStatus(processGroup.getOutputPortStatusSnapshots(), "Output");
        }
    }

    private static void checkConnectionStatus(List<NifiBaseStatusModel> nifiObjectList
            , Collection<ConnectionStatusSnapshotEntity> entityList
            , String processGroupHierarchy) {
        for (ConnectionStatusSnapshotEntity entity : entityList) {
            ConnectionStatusSnapshotDTO dto = entity.getConnectionStatusSnapshot();
            NifiConnectionStatus nifiConnectionStatus = createConnectionStatus(dto, processGroupHierarchy);
            if (nifiConnectionStatus != null) {
                logger.info("-->" + nifiConnectionStatus.toString());
                nifiObjectList.add(nifiConnectionStatus);
            }
        }
    }

    private static NifiProcessGroupStatus createProcessGroupStatus(ProcessGroupStatusSnapshotDTO dto
            , String processGroupHierarchy
            , NifiProperties nifiProperties) {
        NifiProcessGroupStatus nifiProcessGroupStatus = new NifiProcessGroupStatus();
        nifiProcessGroupStatus.setId(dto.getId());
        nifiProcessGroupStatus.setName(dto.getName());
        nifiProcessGroupStatus.setQueuedCount(dto.getFlowFilesQueued());
        nifiProcessGroupStatus.setQueuedSize(dto.getBytesQueued());
        nifiProcessGroupStatus.setQueuedSizeText(dto.getQueuedSize());
        nifiProcessGroupStatus.setProcessGroupHierarchy(processGroupHierarchy);
        nifiProcessGroupStatus.hasInnerProcessGroup((dto.getProcessGroupStatusSnapshots() != null) &&
                (dto.getProcessGroupStatusSnapshots().size() > 0));
        nifiProcessGroupStatus.isExcluded(nifiProperties.getExcludedProcessorGroupIdList().contains(dto.getId()));
        return nifiProcessGroupStatus;
    }

    private static NifiConnectionStatus createConnectionStatus(ConnectionStatusSnapshotDTO dto, String processGroupHierarchy) {
        NifiConnectionStatus nifiConnectionStatus = new NifiConnectionStatus();
        nifiConnectionStatus.setId(dto.getId());
        nifiConnectionStatus.setName(dto.getName());
        nifiConnectionStatus.setQueuedCount(dto.getFlowFilesQueued());
        nifiConnectionStatus.setQueuedSize(dto.getBytesQueued());
        nifiConnectionStatus.setQueuedSizeText(dto.getQueued());
        nifiConnectionStatus.setProcessGroupHierarchy(processGroupHierarchy);
        nifiConnectionStatus.setSourceName(dto.getSourceName());
        nifiConnectionStatus.setDestinationName(dto.getDestinationName());
        nifiConnectionStatus.setPercentUseCount(dto.getPercentUseCount());
        return nifiConnectionStatus;
    }

    private static void logProcessorStatus(Collection<ProcessorStatusSnapshotEntity> entityList) {
        if (entityList == null || entityList.size() < 1) {
            return;
        }
        logger.info("Processor Status List");
        for (ProcessorStatusSnapshotEntity entity : entityList) {
            ProcessorStatusSnapshotDTO dto = entity.getProcessorStatusSnapshot();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("id: %s , ", dto.getId()));
            sb.append(String.format("name: %s , ", dto.getName()));
            sb.append(String.format("type: %s , ", dto.getType()));
            sb.append(String.format("runStatus: %s ", dto.getRunStatus()));
            logger.info("--> " + sb.toString());
        }
    }

    private static void logPortStatus(Collection<PortStatusSnapshotEntity> entityList, String portType) {
        if (entityList == null || entityList.size() < 1) {
            return;
        }
        logger.info("Port Status List ({})", portType);
        for (PortStatusSnapshotEntity entity : entityList) {
            PortStatusSnapshotDTO dto = entity.getPortStatusSnapshot();
            StringBuilder sb = new StringBuilder();
            sb.append("id:" + dto.getId());
            sb.append("name:" + dto.getName());
            sb.append("runStatus:" + dto.getRunStatus());
            sb.append("bytesIn:" + dto.getBytesIn());
            sb.append("bytesOut:" + dto.getBytesOut());
            logger.info("-->" + sb.toString());
        }
    }
}