package edu.omur.nifirestapi.service;

import edu.omur.nifirestapi.model.Constants;
import edu.omur.nifirestapi.model.NifiBaseStatusModel;
import edu.omur.nifirestapi.model.NifiFlowFile;
import edu.omur.nifirestapi.security.NifiKerberosTokenCreator;
import edu.omur.nifirestapi.nifi.NifiProcessGroupStatusHelper;
import edu.omur.nifirestapi.configuration.NifiProperties;
import edu.omur.nifirestapi.nifi.NifiQueueContentHelper;
import org.ietf.jgss.GSSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NifiHealthCheckService {
    private static final Logger logger = LoggerFactory.getLogger(NifiHealthCheckService.class);

    private String nifiSpnegoToken;
    private NifiProperties nifiProperties;
    private NifiHealthLogger nifiHealthLogger;

    @Autowired
    public NifiHealthCheckService(NifiProperties nifiProperties
            , @Qualifier("Logger") NifiHealthLogger nifiHealthLogger)
            throws GSSException, PrivilegedActionException, MalformedURLException {
        logger.info("NifiHealthCheckService is created");
        this.nifiProperties = nifiProperties;
        this.nifiHealthLogger = nifiHealthLogger;
        if (nifiProperties.hasSecureConnection()) {
            this.nifiSpnegoToken = NifiKerberosTokenCreator.getKerberosToken(nifiProperties);
        }
        logger.info("nifiSpnegoToken: {}", nifiSpnegoToken);
    }

    public void startHealthCheck() {
        logger.info("  start health check  ");

        logger.info(" >>» start checking processGroup status...");
        List<NifiBaseStatusModel> nifiObjectList = checkProcessGroupStatus();

        logger.info("");
        logger.info(" »» start checking queue contents...");
        checkQueueContent(nifiObjectList);

        logger.info("  end of health check  \n");
    }

    private List<NifiBaseStatusModel> checkProcessGroupStatus() {
        String inquiryUrl = nifiProperties.getInquiryUrl().replace("##processGroupId##", nifiProperties.getRootProcessorGroupId());
        List<NifiBaseStatusModel> nifiObjectList = NifiProcessGroupStatusHelper.getObjectStatusList(this.nifiProperties, this.nifiSpnegoToken);
        logger.info("ProcessGroupStatus data is read from Nifi.");

        boolean isSuccessful = nifiHealthLogger.bulkInsert0fStatus(nifiObjectList);
        logger.info("Is data saved to Elasticsearch successfully: {}", isSuccessful);

        return nifiObjectList;
    }

    private void checkQueueContent(List<NifiBaseStatusModel> nifiObjectList) {
        if (nifiObjectList == null || nifiObjectList.size() < 1) {
            return;
        }

        List<NifiBaseStatusModel> listForContentCheck = nifiObjectList.stream()
                .filter(item -> item.getName().toLowerCase().contains(Constants.KEYWORD_OF_CHECK_QUEUE_ITEMS) && item.getQueuedCount() > 0)
                .collect(Collectors.toList());
        logger.info("Number of connections will be checked: {}", listForContentCheck.size());

        List<NifiFlowFile> flowFileList = new ArrayList<>();
        for (NifiBaseStatusModel nifiBaseStatusModel : listForContentCheck) {
            String urlOfFlowFileQuery = nifiProperties.getFlowFileQueuesUrl()
                    .replace("##connectionId##", nifiBaseStatusModel.getId());
            List<NifiFlowFile> tmpList = NifiQueueContentHelper.getQueueContent(this.nifiProperties, this.nifiSpnegoToken, nifiBaseStatusModel);
            flowFileList.addAll(tmpList);
        }

        logger.info("FlowFiles waiting in queues are read from Nifi. List size: {}", flowFileList.size());
        if (flowFileList.size() > 0) {
            for (NifiFlowFile flowFile : flowFileList) {
                logger.info(flowFile.convertToMap().toString());
            }
            boolean isSuccessful = nifiHealthLogger.bulkInsertOfFlowFile(flowFileList);
            logger.info("Is data saved to Elasticsearch successfully: {}", isSuccessful);
        }
    }
}
