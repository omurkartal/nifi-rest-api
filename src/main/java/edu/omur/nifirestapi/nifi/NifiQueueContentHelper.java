package edu.omur.nifirestapi.nifi;

import edu.omur.nifirestapi.configuration.NifiProperties;
import edu.omur.nifirestapi.model.NifiBaseStatusModel;
import edu.omur.nifirestapi.model.NifiFlowFile;
import edu.omur.nifirestapi.utility.DateTimeUtility;
import org.apache.nifi.web.api.dto.FlowFileSummaryDTO;
import org.apache.nifi.web.api.entity.ListingRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

public final class NifiQueueContentHelper {
    private static Logger logger = LoggerFactory.getLogger(NifiQueueContentHelper.class);

    public static List<NifiFlowFile> getQueueContent(NifiProperties nifiProperties
            , String token
            , NifiBaseStatusModel nifiBaseStatusModel) {
        String flowFileQueuesUrl = nifiProperties.getFlowFileQueuesUrl().replace("##connectionId##", nifiBaseStatusModel.getId());

        String requestId = sendListingRequest(flowFileQueuesUrl, token);
        if (requestId == null) {
            return null;
        }

        List<NifiFlowFile> nifiFlowFileList = getQueueContent(flowFileQueuesUrl
                , token
                , requestId
                , nifiBaseStatusModel
                , nifiProperties.getMinDurationForQueueContent());
        return nifiFlowFileList;
    }

    private static String sendListingRequest(String flowFileQueuesUrl, String token) {
        logger.debug("flowfile-queues url:" + flowFileQueuesUrl);
        ListingRequestEntity requestEntity = NifiRestCallHelper.doRestCall(flowFileQueuesUrl
                , "Bearer " + token
                , HttpMethod.POST
                , MediaType.APPLICATION_JSON
                , ""
                , ListingRequestEntity.class);

        if (requestEntity == null || requestEntity.getListingRequest() == null) {
            logger.warn("No valid data returned from flowfile-queues request!");
            return null;
        }

        String requestId = requestEntity.getListingRequest().getId();
        int objectCount = requestEntity.getListingRequest().getQueueSize().getObjectCount();
        logger.info("requestId: {},objectCount: {}", requestId, objectCount);
        if (objectCount < 1) {
            return null;
        }
        return requestId;
    }

    private static List<NifiFlowFile> getQueueContent(String flowFileQueuesUrl
            , String token
            , String requestld
            , NifiBaseStatusModel nifiBaseStatusModel
            , int minQueuedDurationInMinutes) {
        String url = flowFileQueuesUrl + "/" + requestld;
        logger.info("url for getting queue content:" + url);

        ListingRequestEntity reqEntity = NifiRestCallHelper.doRestCall(url
                , "Bearer " + token
                , HttpMethod.GET
                , MediaType.APPLICATION_JSON
                , ""
                , ListingRequestEntity.class);

        if (reqEntity == null || reqEntity.getListingRequest() == null) {
            logger.warn("No data returned from details of flowfile-queuesuest!");
            return null;
        }

        List<NifiFlowFile> nifiFlowFileList = new ArrayList<>();
        List<FlowFileSummaryDTO> flowFileList = reqEntity.getListingRequest().getFlowFileSummaries();
        for (FlowFileSummaryDTO dto : flowFileList) {
            long durationInMin = DateTimeUtility.convertMillisecondsToMinutes(dto.getQueuedDuration());
            if (durationInMin >= minQueuedDurationInMinutes) {
                NifiFlowFile nifiFlowFile = new NifiFlowFile();
                nifiFlowFile.setConnectionId(nifiBaseStatusModel.getId());
                nifiFlowFile.setConnectionName(nifiBaseStatusModel.getName());
                nifiFlowFile.setId(dto.getUuid());
                nifiFlowFile.setName(dto.getFilename());
                nifiFlowFile.setPosition(dto.getPosition());
                nifiFlowFile.setSize(dto.getSize());
                nifiFlowFile.setQueuedDurationInMinutes(durationInMin);
                nifiFlowFile.setQueuedDurationText(DateTimeUtility.convertMillisecondsToDayHourMinuteText(dto.getQueuedDuration()));
                nifiFlowFile.setClusterNodeAddress(dto.getClusterNodeAddress());
                nifiFlowFile.setProcessGroupHierarchy(nifiBaseStatusModel.getProcessGroupHierarchy());
                nifiFlowFileList.add(nifiFlowFile);
            }
        }
        return nifiFlowFileList;
    }
}