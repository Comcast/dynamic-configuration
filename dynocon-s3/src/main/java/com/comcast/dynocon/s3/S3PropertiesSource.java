package com.comcast.dynocon.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.comcast.dynocon.ConfigFactory;
import com.comcast.dynocon.ConfigUtil;
import com.comcast.dynocon.PropertiesSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class S3PropertiesSource implements PropertiesSource {

    public static final String PARAM_TABLE = "dynocon.s3.bucket";
    public static final String PARAM_POLLING_DELAY = "dynocon.s3.delay";

    private static final Logger LOGGER = LoggerFactory.getLogger(S3PropertiesSource.class);

    private static final AmazonS3 CLIENT = AmazonS3ClientBuilder.standard().build();
    private static final String DEFAULT_BUCKET_NAME = "config";
    private static final int DEFAULT_POLLING_DELAY_SEC = 30;

    protected String bucketName;

    protected Map<String, String> rawProperties;
    protected Map<String, String> lastKnownGood;

    public S3PropertiesSource() {
        this(null);
    }

    public S3PropertiesSource(String s3BucketName) {

        bucketName = Optional.ofNullable(s3BucketName)
                .orElse(
                        Optional.ofNullable(Optional.ofNullable(System.getenv(PARAM_TABLE))
                                .orElse(System.getProperty(PARAM_TABLE)
                                )
                        ).orElse(DEFAULT_BUCKET_NAME)
                );

        rawProperties = getProperties();

        String intStr = Optional.ofNullable(System.getenv(PARAM_POLLING_DELAY)).orElse(System.getProperty(PARAM_POLLING_DELAY));
        int delaySec = DEFAULT_POLLING_DELAY_SEC;
        if (intStr != null) {
            try {
                delaySec = Integer.parseInt(intStr);
            } catch (NumberFormatException e) {
                LOGGER.error("Error parsing " + PARAM_POLLING_DELAY + " property", e);
            }
        }

        ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        exService.scheduleWithFixedDelay(() -> {
            LOGGER.trace("Starting pulling Dynamic Properties from S3 bucket `{}`.", bucketName);
            Map<String, String> result = getProperties();
            if (ConfigUtil.isChanged(rawProperties, result)) {
                rawProperties = result;
                ConfigFactory.instance.reloadProperties();
                LOGGER.debug("Dynamic Properties are reloaded from S3 bucket `{}`.", bucketName);
            }
            LOGGER.trace("Finishing pulling Dynamic Properties from S3 bucket `{}`.", bucketName);
        }, delaySec, delaySec, TimeUnit.SECONDS);

        LOGGER.info("Dynamic Properties Source is configured to pull values from S3 bucket `{}` every {} seconds.", bucketName, delaySec);
    }

    protected Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        try {
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(100);
            ListObjectsV2Result s3Result;
            do {
                s3Result = CLIENT.listObjectsV2(req);
                for (S3ObjectSummary objectSummary : s3Result.getObjectSummaries()) {
                    S3Object fullObject = CLIENT.getObject(new GetObjectRequest(bucketName, objectSummary.getKey()));
                    String key = objectSummary.getKey();
                    if (key.endsWith(".json")) {
                        key = key.substring(0, key.length() - 5);
                    }
                    String value = IOUtils.toString(fullObject.getObjectContent());
                    LOGGER.trace("S3 item: key=`{}` value=`{}`", key, value);
                    result.put(key, value);
                }
                req.setContinuationToken(s3Result.getNextContinuationToken());
            } while (s3Result.isTruncated());
            lastKnownGood = result;
        } catch (Throwable e) {
            result = lastKnownGood;
            LOGGER.error("Error getting properties from S3 bucket `{}`", bucketName, e);
        }
        return result;
    }

    @Override
    public Map<String, String> getRawProperties() {
        return rawProperties;
    }
}
