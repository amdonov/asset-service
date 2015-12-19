package com.mycompany.assets.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * Created by amdonov on 12/19/15.
 */
public class CassandraStoreConfiguration {
    @Min(1)
    @JsonProperty
    private int resultsPerPage = 100;
    @JsonProperty
    @NotEmpty
    private String keyspaceStatement = "CREATE KEYSPACE IF NOT EXISTS Assets WITH REPLICATION = {'class':'NetworkTopologyStrategy', 'datacenter1':2};";
    @Min(1)
    @JsonProperty
    private int healthyNodeCount = 3;
    @Min(1)
    @JsonProperty
    private int usedHostsPerRemoteDC = 2;
    @JsonProperty
    @NotEmpty
    private String contactPoint = "127.0.0.1";
    @JsonProperty
    @NotEmpty
    private String datacenter = "datacenter1";

    public int getHealthyNodeCount() {
        return healthyNodeCount;
    }

    public void setHealthyNodeCount(int healthyNodeCount) {
        this.healthyNodeCount = healthyNodeCount;
    }

    public String getKeyspaceStatement() {
        return keyspaceStatement;
    }

    public void setKeyspaceStatement(String keyspaceStatement) {
        this.keyspaceStatement = keyspaceStatement;
    }

    public int getUsedHostsPerRemoteDC() {
        return usedHostsPerRemoteDC;
    }

    public void setUsedHostsPerRemoteDC(int usedHostsPerRemoteDC) {
        this.usedHostsPerRemoteDC = usedHostsPerRemoteDC;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public String getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(String contactPoint) {
        this.contactPoint = contactPoint;
    }

    public String getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }
}
