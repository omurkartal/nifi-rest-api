package edu.omur.nifirestapi.service;

import edu.omur.nifirestapi.model.NifiBaseStatusModel;
import edu.omur.nifirestapi.model.NifiFlowFile;

import java.util.List;

public interface NifiHealthLogger {
    boolean bulkInsert0fStatus(List<NifiBaseStatusModel> nifiObjectList);

    boolean bulkInsertOfFlowFile(List<NifiFlowFile> nifiObjectList);
}
