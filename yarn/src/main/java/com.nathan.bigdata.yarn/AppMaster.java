package com.nathan.bigdata.yarn;

import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppMaster implements AMRMClientAsync.CallbackHandler {

    private YarnConfiguration conf = new YarnConfiguration();
    private NMClient nmClient;
    private int containerCount = 3;

    public static void main(String[] args) {
        System.out.println("AppMaster: Initializing");
        try {
            new AppMaster().run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() throws Exception {
        conf = new YarnConfiguration();

        // Create NM Client
        nmClient = NMClient.createNMClient();
        nmClient.init(conf);
        nmClient.start();

        // Create AM - RM Client
        AMRMClientAsync<ContainerRequest> rmClient = AMRMClientAsync.createAMRMClientAsync(1000, this);
        rmClient.init(conf);
        rmClient.start();

        // Register with RM
        rmClient.registerApplicationMaster("", 0, "");
        System.out.println("AppMaster: Registered");

        // Priority for worker containers - priorities are intra-application
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(0);

        // Resource requirements for worker containers
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(128);
        capability.setVirtualCores(1);

        // Reqiest Containers from RM
        System.out.println("AppMaster: Requesting " + containerCount + " Containers");
        for (int i = 0; i < containerCount; ++i) {
            rmClient.addContainerRequest(new ContainerRequest(capability, null, null, priority));
        }

        while (!containersFinished()) {
            Thread.sleep(100);
        }

        System.out.println("AppMaster: Unregistered");
        rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "", "");
    }

    private boolean containersFinished() {
        return containerCount == 0;
    }

    @Override
    public void onContainersAllocated(List<Container> containers) {
        for (Container container : containers) {
            try {
                nmClient.startContainer(container, initContainer());
                System.err.println("AppMaster: Container launched " + container.getId());
            } catch (Exception ex) {
                System.err.println("AppMaster: Container not launched " + container.getId());
                ex.printStackTrace();
            }
        }
    }

    private ContainerLaunchContext initContainer() {
        try {
            // Create Container Context
            ContainerLaunchContext cCLC = Records.newRecord(ContainerLaunchContext.class);
            cCLC.setCommands(Collections.singletonList("$JAVA_HOME/bin/java"
                    + " -Xmx256M"
                    + " com.nathan.bigdata.yarn.Container"
                    + " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout"
                    + " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"));

            // Set Container jar
            LocalResource jar = Records.newRecord(LocalResource.class);
            Utils.setUpLocalResource(Utils.YARNAPP_JAR_PATH, jar, conf);
            cCLC.setLocalResources(Collections.singletonMap(Utils.YARNAPP_JAR_NAME, jar));

            // Set Container CLASSPATH
            Map<String, String> env = new HashMap<String, String>();
            Utils.setUpEnv(env, conf);
            cCLC.setEnvironment(env);

            return cCLC;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onContainersCompleted(List<ContainerStatus> statusOfContainers) {
        for (ContainerStatus status : statusOfContainers) {
            System.err.println("AppMaster: Container finished " + status.getContainerId());
            synchronized (this) {
                containerCount--;
            }
        }
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNodesUpdated(List<NodeReport> nodeReports) {
    }

    @Override
    public void onShutdownRequest() {
    }

    @Override
    public float getProgress() {
        return 0;
    }
}
