package org.jboss.aerogear.pushtest.perf;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This report utilitity is able to handle:
 * 
 * Up to {@see PushMessageDeliveryReport#RUN_MAX} test runs, each per {@see PushMessageDeliveryReport#THREAD_MAX} threads in
 * {@see PushMessageDeliveryReport#PROCESS_MAX} processes using {@see PushMessageDeliveryReport#AGENT_MAX} agents
 * 
 * @author kpiwko
 * 
 */
public class PushMessageDeliveryReport {

    public static final int RUN_MAX = 4096; // 2^12
    public static final int THREAD_MAX = 64; // 2^6
    public static final int PROCESS_MAX = 64; // 2^6
    public static final int AGENT_MAX = 4; // 2^2

    private static final int RUN_OFFSET = 1;
    private static final int THREAD_OFFSET = RUN_MAX;
    private static final int PROCESS_OFFSET = THREAD_MAX * THREAD_OFFSET;
    private static final int AGENTS_OFFSET = PROCESS_MAX * PROCESS_OFFSET;

    private BitSet bitmap;
    private int totalDeliveredMessages;
    private int maxAgent;
    private int maxProcess;
    private int maxThread;
    private int maxRun;

    public PushMessageDeliveryReport() {
        reset();
    }

    public void reset() {
        this.bitmap = new BitSet(AGENT_MAX * AGENTS_OFFSET);
        this.totalDeliveredMessages = 0;
        this.maxAgent = 0;
        this.maxProcess = 0;
        this.maxThread = 0;
        this.maxRun = 0;
    }

    public void delivered(int agent, int process, int thread, int run) {

        if (agent >= AGENT_MAX || process >= PROCESS_MAX || thread >= THREAD_MAX || run >= RUN_MAX) {
            throw new IllegalStateException(MessageFormat.format("Invalid agent {0}, process {1}, thread {2}, run number {3}",
                    agent, process, thread, run));
        }

        totalDeliveredMessages++;
        maxAgent = agent > maxAgent ? agent : maxAgent;
        maxProcess = process > maxProcess ? process : maxProcess;
        maxThread = thread > maxThread ? thread : maxThread;
        maxRun = run > maxRun ? run : maxRun;

        bitmap.set(index(agent, process, thread, run));
    }

    public Integer totalDeliveredMessages() {
        return totalDeliveredMessages;
    }

    public List<Integer> missingMessages() {

        List<Integer> missingMessages = new ArrayList<Integer>();

        // we expect at least some message was delivered
        // otherwise we are not able to report stats anyway
        if (totalDeliveredMessages > 0) {
            for (int agent = 0; agent <= maxAgent; agent++) {
                for (int process = 0; process <= maxProcess; process++) {
                    for (int thread = 0; thread <= maxThread; thread++) {
                        for (int run = 0; run <= maxRun; run++) {
                            int messageNo = index(agent, process, thread, run);
                            if (!bitmap.get(messageNo)) {
                                missingMessages.add(messageNo);
                            }
                        }
                    }
                }
            }
        }

        return missingMessages;
    }

    private int index(int agent, int process, int thread, int run) {
        return AGENTS_OFFSET * agent + PROCESS_OFFSET * (process + THREAD_MAX) + THREAD_OFFSET * thread + RUN_OFFSET * run;
    }

}