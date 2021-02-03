package com.comcast.dynocon.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
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

public class DynamodbPropertiesSource implements PropertiesSource {

    public static final String PARAM_TABLE = "dynocon.dynamodb.table";
    public static final String PARAM_POLLING_DELAY = "dynocon.dynamodb.delay";

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamodbPropertiesSource.class);

    private static final AmazonDynamoDB CLIENT = AmazonDynamoDBClientBuilder.standard().build();
    private static final String DEFAULT_TABLE_NAME = "config";
    private static final int DEFAULT_POLLING_DELAY_SEC = 30;

    protected String tableName;

    protected Map<String, String> rawProperties;
    protected Map<String, String> lastKnownGood;

    public DynamodbPropertiesSource() {
        this(null);
    }

    public DynamodbPropertiesSource(String dynamodbTableName) {
        tableName = Optional.ofNullable(dynamodbTableName)
                .orElse(
                        Optional.ofNullable(Optional.ofNullable(System.getenv(PARAM_TABLE))
                                .orElse(System.getProperty(PARAM_TABLE)
                                )
                        ).orElse(DEFAULT_TABLE_NAME)
                );

        rawProperties = getProperties();

        String intStr = System.getProperty(PARAM_POLLING_DELAY);
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
            LOGGER.trace("Starting pulling Dynamic Properties from DynamoDB table `{}`.", tableName);
            Map<String, String> result = getProperties();
            if (ConfigUtil.isChanged(rawProperties, result)) {
                rawProperties = result;
                ConfigFactory.instance.reloadProperties();
                LOGGER.debug("Dynamic Properties are reloaded from DynamoDB table `{}`.", tableName);
            }
            LOGGER.trace("Finishing pulling Dynamic Properties from DynamoDB table `{}`.", tableName);
        }, delaySec, delaySec, TimeUnit.SECONDS);

        LOGGER.info("Dynamic Properties Source is configured to pull values from DynamoDB table `{}` every {} seconds.", tableName, delaySec);
    }

    protected Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        try {
            Map<String, AttributeValue> lastKeyEvaluated = null;
            do {
                ScanResult scanResult = CLIENT.scan(new ScanRequest().withTableName(tableName).withExclusiveStartKey(lastKeyEvaluated));
                for (Map<String, AttributeValue> item : scanResult.getItems()) {
                    LOGGER.trace("DynamoDB item: key=`{}` value=`{}`", item.get("key").getS(), item.get("value").getS());
                    result.put(item.get("key").getS(), item.get("value").getS());
                }
                lastKeyEvaluated = scanResult.getLastEvaluatedKey();
            } while (lastKeyEvaluated != null);
            lastKnownGood = result;
        } catch (Throwable e) {
            result = lastKnownGood;
            LOGGER.error("Error getting properties from DynamoDB table `{}`", tableName, e);
        }
        return result;
    }

    @Override
    public Map<String, String> getRawProperties() {
        return rawProperties;
    }
}
