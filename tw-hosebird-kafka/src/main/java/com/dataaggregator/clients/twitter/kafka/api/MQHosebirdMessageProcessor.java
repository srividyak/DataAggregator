package com.dataaggregator.clients.twitter.kafka.api;

import com.twitter.hbc.common.DelimitedStreamReader;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.processor.HosebirdMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by srividyak on 01/01/15.
 */
public class MQHosebirdMessageProcessor implements HosebirdMessageProcessor {

    private final static Logger logger = LoggerFactory.getLogger(MQHosebirdMessageProcessor.class);
    private final static int DEFAULT_BUFFER_SIZE = 50000;
    private final static int MAX_ALLOWABLE_BUFFER_SIZE = 500000;
    private final static String EMPTY_LINE = "";
    private MessageProducer producer;

    private DelimitedStreamReader reader;

    public MQHosebirdMessageProcessor(MessageProducer producer) {
        this.producer = producer;
    }

    protected String processNextMessage() throws IOException {
        int delimitedCount = -1;
        int retries = 0;
        while (delimitedCount < 0 && retries < 3) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Unable to read new line from stream");
            } else if (line.equals(EMPTY_LINE)) {
                return null;
            }

            try {
                delimitedCount = Integer.parseInt(line);
            } catch (NumberFormatException n) {
                // resilience against the occasional malformed message
                logger.warn("Error parsing delimited length", n);
            }
            retries += 1;
        }

        if (delimitedCount < 0) {
            throw new RuntimeException("Unable to process delimited length");
        }

        if (delimitedCount > MAX_ALLOWABLE_BUFFER_SIZE) {
            // this is to protect us from nastiness
            throw new IOException("Unreasonable message size " + delimitedCount);
        }
        return reader.read(delimitedCount);
    }

    @Override
    public void setup(InputStream input) {
        reader = new DelimitedStreamReader(input, Constants.DEFAULT_CHARSET, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public boolean process() throws IOException, InterruptedException {
        String msg = processNextMessage();
        while (msg == null) {
            msg = processNextMessage();
        }
        return producer.send(msg);
    }
}
