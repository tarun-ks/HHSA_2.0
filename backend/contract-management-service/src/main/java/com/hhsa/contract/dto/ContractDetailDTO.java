package com.hhsa.contract.dto;

import java.util.List;

/**
 * Contract detail DTO with configurations
 */
public class ContractDetailDTO {

    private ContractDTO contract;
    private List<ContractConfigurationDTO> configurations;

    // Getters and Setters

    public ContractDTO getContract() {
        return contract;
    }

    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }

    public List<ContractConfigurationDTO> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ContractConfigurationDTO> configurations) {
        this.configurations = configurations;
    }
}


