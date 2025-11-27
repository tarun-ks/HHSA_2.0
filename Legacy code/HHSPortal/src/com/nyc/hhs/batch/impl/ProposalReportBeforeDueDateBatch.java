package com.nyc.hhs.batch.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.service.db.services.notification.ProposalReportOnDueDateService;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class ProposalReportBeforeDueDateBatch implements IBatchQueue {
    protected static final String REPORT_BODY_MESSAGE = "5602_REPORT_EMAIL_BODY_MESSAGE_BEFORE_DUE_DATE";
    
    @Override
    public List getQueue(Map aoMParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void executeQueue(List aoLQueue)  {
        try
        {
            ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
            Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
                    ApplicationConstants.BATCH_TRANSACTION_CONFIG));
            
            loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

            Channel loChannelObj = new Channel();
            /**
             * This transaction will get  Notification details. Will get
             * all the groupIds from the List and fetch userIds for ORgId and
             * GropuID. Then it will update User Notification with userIDs and
             * alert Notification details
             */
            TransactionManager.executeTransaction(loChannelObj, ProposalReportOnDueDateService.PROPOSAL_REPORT_BEFORE_DUE_DATE_METHOD);
            @SuppressWarnings("unchecked")
            List<ProposalReportBean> loGroupDetailList = (List<ProposalReportBean>) loChannelObj
                    .getData(ProposalReportOnDueDateService.PROPOSAL_REPORT_BEFORE_DUE_DATE_REPORT);

            @SuppressWarnings("unchecked")
            List<String> loRecipientsList = (List<String>) loChannelObj.getData(ProposalReportOnDueDateService.RESULT_REPORT_RECIPIENTS_LIST);

            String loEmailBodyMessage = PropertyLoader.getProperty(ProposalReportOnDueDateBatch.REPORT_PROPERTY_FILE, REPORT_BODY_MESSAGE);

            if(loGroupDetailList != null && !loGroupDetailList.isEmpty()){
                Map<String,List<ProposalReportBean>> loMap = reorganizeReportByProcurement( loGroupDetailList);
                for(String key: loMap.keySet()){
                    ProposalReportOnDueDateBatch.sendEmailWithReport(loMap.get(key), loRecipientsList, loEmailBodyMessage);

                    // to give mail server a break
                    Thread.sleep(1000);
                }
            }
        }
        catch (ApplicationException aoExp)
        {
            ProposalReportOnDueDateBatch.LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
        }
        catch (InterruptedException aoExp)
        {
            ProposalReportOnDueDateBatch.LOG_OBJECT.Error("Error occurred while running email Notification batch:", aoExp);
        }

    }
    
    /**
     * This method will reorganize list from DB put into a Map of list by Agency
     * 
     * @param aoTo a string array of user ids
     * @param asSubject a string value of email subject
     * @param asMessage a string value of email body
     * @throws ApplicationException If an Application Exception occurs
     */
    protected Map<String,List<ProposalReportBean>> reorganizeReportByAgency(List<ProposalReportBean> aoProposalList){

        Map<String,List<ProposalReportBean>> propoRep = new HashMap <String,List<ProposalReportBean>>();
        
        for(ProposalReportBean repVo: aoProposalList){
            if(propoRep.containsKey(repVo.getAgencyId())){
                propoRep.get(repVo.getAgencyId()).add(repVo);
            }else{
                propoRep.put(repVo.getAgencyId(), new ArrayList<ProposalReportBean>());
                propoRep.get(repVo.getAgencyId()).add(repVo);
            }
        }

        return propoRep;
    }

    /**
     * This method will reorganize list from DB put into a Map of list by Procurement 
     * 
     * @param aoTo a string array of user ids
     * @param asSubject a string value of email subject
     * @param asMessage a string value of email body
     * @throws ApplicationException If an Application Exception occurs
     */
    protected Map<String,List<ProposalReportBean>> reorganizeReportByProcurement(List<ProposalReportBean> aoProposalList){

        Map<String,List<ProposalReportBean>> propoRep = new HashMap <String,List<ProposalReportBean>>();
        
        for(ProposalReportBean repVo: aoProposalList){
            if(propoRep.containsKey(repVo.getProcurementTitle())){
                propoRep.get(repVo.getProcurementTitle()).add(repVo);
            }else{
                propoRep.put(repVo.getProcurementTitle(), new ArrayList<ProposalReportBean>());
                propoRep.get(repVo.getProcurementTitle()).add(repVo);
            }
        }
        return propoRep;
    }
    
    public static void main(String[] args) {
             (new ProposalReportBeforeDueDateBatch()).executeQueue(null);
    }

}
