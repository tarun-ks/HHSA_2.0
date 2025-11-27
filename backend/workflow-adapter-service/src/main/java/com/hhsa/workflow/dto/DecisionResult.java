package com.hhsa.workflow.dto;

import java.util.Map;

/**
 * Result of DMN decision evaluation
 */
public class DecisionResult {

    private String decisionId;
    private String decisionName;
    private Map<String, Object> result;
    private Map<String, Object> evaluatedInputs;
    private Map<String, Object> matchedRules;

    public DecisionResult() {
    }

    public DecisionResult(String decisionId, String decisionName, Map<String, Object> result) {
        this.decisionId = decisionId;
        this.decisionName = decisionName;
        this.result = result;
    }

    // Getters and Setters

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public String getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(String decisionName) {
        this.decisionName = decisionName;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public Map<String, Object> getEvaluatedInputs() {
        return evaluatedInputs;
    }

    public void setEvaluatedInputs(Map<String, Object> evaluatedInputs) {
        this.evaluatedInputs = evaluatedInputs;
    }

    public Map<String, Object> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(Map<String, Object> matchedRules) {
        this.matchedRules = matchedRules;
    }
}




