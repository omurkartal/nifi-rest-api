package edu.omur.nifirestapi.service;

import edu.omur.nifirestapi.model.NifiBaseStatusModel;
import edu.omur.nifirestapi.model.NifiFlowFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Logger")
public class NifiHealthLoggerService implements NifiHealthLogger {
    private static final Logger logger = LoggerFactory.getLogger(NifiHealthLoggerService.class);

    public boolean bulkInsert0fStatus(List<NifiBaseStatusModel> nifiObjectList) {
        for (NifiBaseStatusModel nifiObject : nifiObjectList) {
            logger.info(nifiObject.toString());
            //logger.info(nifiObject.convertToMap().toString());
        }
        return true;
    }

    public boolean bulkInsertOfFlowFile(List<NifiFlowFile> nifiObjectList) {
        for (NifiFlowFile nifiObject : nifiObjectList) {
            logger.info(nifiObject.toString());
        }
        return true;
    }
}
