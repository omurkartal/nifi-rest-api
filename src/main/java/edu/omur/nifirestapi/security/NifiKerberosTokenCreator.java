package edu.omur.nifirestapi.security;

import com.kerb4j.client.SpnegoClient;
import com.kerb4j.client.SpnegoContext;
import edu.omur.nifirestapi.configuration.NifiProperties;
import edu.omur.nifirestapi.utility.DateTimeUtility;
import edu.omur.nifirestapi.utility.FileUtility;
import edu.omur.nifirestapi.nifi.NifiRestCallHelper;
import org.ietf.jgss.GSSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.time.ZonedDateTime;

public final class NifiKerberosTokenCreator {
    private static final Logger logger = LoggerFactory.getLogger(NifiKerberosTokenCreator.class);
    private static final String FILE_NAME = "lastToken";
    private static final String FILE_DELIMITER = ".delimeter.";

    public static String getKerberosToken(NifiProperties nifiProperties)
            throws MalformedURLException, PrivilegedActionException, GSSException, HttpClientErrorException {
        String spnegoToken = null;
        if (nifiProperties.usePreviousToken()) {
            spnegoToken = getPreviousKerberosTokenIfValid();
        } else {
            logger.info("New token will be created.");
        }
        if (spnegoToken == null) {
            spnegoToken = createNewKerberosToken(nifiProperties);
            writeTokenToFile(spnegoToken);
        }
        return spnegoToken;
    }

    private static String createNewKerberosToken(NifiProperties nifiProperties)
            throws MalformedURLException, PrivilegedActionException, GSSException, HttpClientErrorException {
        SpnegoClient spnegoClient = SpnegoClient
                .loginWithUsernamePassword(nifiProperties.getUsername(), nifiProperties.getPassword());
        SpnegoContext context = spnegoClient.createContext(new URL(nifiProperties.getTokenUrl()));
        String spnegoToken = context.createTokenAsAuthroizationHeader();
        logger.debug("spnegoToken:()", spnegoToken);

        String response = NifiRestCallHelper.doRestCall(nifiProperties.getTokenUrl()
                , spnegoToken
                , HttpMethod.POST
                , MediaType.TEXT_PLAIN
                , ""
                , String.class);

        return response;
    }

    private static String getPreviousKerberosTokenIfValid() {
        String token = null;
        try {
            String lineReadFromFile = FileUtility.readFirstLineFromFile(FILE_NAME);
            logger.debug("Line read from {} file is: {}", FILE_NAME, lineReadFromFile);

            String[] stringList = lineReadFromFile.split(FILE_DELIMITER);
            logger.trace("stringList[0]:{}", stringList[0]);
            logger.trace("stringList[1]:{}", stringList[1]);

            ZonedDateTime lastTimeTokenCreated = ZonedDateTime.parse(stringList[0]);
            ZonedDateTime zdt6HoursBefore = DateTimeUtility.getCurrentTimestampAsZoneDate().minusHours(6);
            logger.debug("6 hours before: {}", zdt6HoursBefore);

            if (lastTimeTokenCreated.isAfter(zdt6HoursBefore)) {
                token = stringList[1];
                logger.info("Previous token is still valid; no need to create a new token");
                logger.info("Creation time of the previous valid token:{}", stringList[0]);
                logger.info(" - Previous valid token :{}", token);
            } else {
                logger.info(" Previous token is expired. New token will be created.");
            }
        } catch (Exception ex) {
            logger.warn("Error occurred while validating previous token but its not critical; error is ignored. New token will be created.", ex);
        }
        return token;
    }

    private static void writeTokenToFile(String spnegoToken) {
        try {
            FileUtility.writeToFile(FILE_NAME, DateTimeUtility.getCurrentTimestampAsZoneDateString() + FILE_DELIMITER + spnegoToken);
        } catch (Exception ex) {
            logger.warn("Error occurred while writing spnego token to the file but it's not critical; error is ignored.", ex);
        }
    }
}
