/**
 * 
 */
package com.nyc.hhs.service.db.services.notification;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * @author mypark
 *
 */
public class ProposalReportOnDueDateService extends ServiceState {
    public static String PROPOSAL_DUE_DATE_REPORT_METHOD = "getProposalReportOnDueDate";
    public static String PROPOSAL_DUE_DATE_REPORT_RESULT = "proposalReportOnDueDate";

    public static String MAPPER_CLASS_PROPOSAL_REPORT_ON_DUE_DATE  = "com.nyc.hhs.service.db.services.application.ProposalDueDateNotificationServiceMapper";
    public static String METHOD_PROPOSAL_REPORT_ON_DUE_DATE = "getProposalDueDateList";

    public static String METHOD_REPORT_RECIPIENTS_LIST = "getReportRecipientsList";
    public static String RESULT_REPORT_RECIPIENTS_LIST = "recipientsList";
    
    public static String PROPOSAL_REPORT_BEFORE_DUE_DATE_METHOD = "getProposalReportBeforeDueDate";
    public static String PROPOSAL_REPORT_BEFORE_DUE_DATE_REPORT = "proposalReportBeforeDueDate";

    /**
     * This method will fetch the list of Due Date
     * for the proposal
     * 
     * query: getProposalDueDateList
     * @param aoMyBatisSession
     * @return list of ProposalReportBean
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    public List<ProposalReportBean> getProposalReportOnDueDate(SqlSession aoMyBatisSession) throws ApplicationException{
        List<ProposalReportBean> loNotificationDetailList = null;
        try
        {   
            String dueDate = DateUtil.getCurrentDate();
            loNotificationDetailList = (List<ProposalReportBean>) DAOUtil.masterDAO(aoMyBatisSession, dueDate ,
                    MAPPER_CLASS_PROPOSAL_REPORT_ON_DUE_DATE, METHOD_PROPOSAL_REPORT_ON_DUE_DATE, HHSConstants.JAVA_LANG_STRING);
            setMoState("Transaction Success: Due Date Proposal Data fetched successfully");
        }
        catch (ApplicationException aoAppEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of Due Date Proposal Data ");
            throw aoAppEx;
        }
        catch (Exception aoEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of Due Date Proposal Data ");
            throw new ApplicationException("Transaction Failure: Error Occurred while fetching the list of Due Date Proposal Data for Propvider", aoEx);
        }

        return loNotificationDetailList;
    }
    
    /**
 	 * This method will fetch
 	 * the list of 1 business day Before Due Date Proposal Data   
     * query: getProposalDueDateList
     * @param aoMyBatisSession
     * @return list of ProposalReportBean
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    public List<ProposalReportBean> getProposalReportBeforeDueDate(SqlSession aoMyBatisSession) throws ApplicationException{
        List<ProposalReportBean> loNotificationDetailList = null;
        try
        {
            String dueDate = DateUtil.nextWeekday();
           
            loNotificationDetailList = (List<ProposalReportBean>) DAOUtil.masterDAO(aoMyBatisSession, dueDate ,
                    MAPPER_CLASS_PROPOSAL_REPORT_ON_DUE_DATE, METHOD_PROPOSAL_REPORT_ON_DUE_DATE, HHSConstants.JAVA_LANG_STRING);
            setMoState("Transaction Success: Due Date Proposal Data fetched successfully");

        }
        catch (ApplicationException aoAppEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of 1 business day Before Due Date Proposal Data");
            throw aoAppEx;
        }
        catch (Exception aoEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of 1 business day Before Due Date Proposal Data ");
            throw new ApplicationException("Transaction Failure: Error Occurred while fetching the list of 1 business day Before Due Date Proposal Data ", aoEx);
        }

        return loNotificationDetailList;
    }
    
    /**
     * This method will fetch the list
     * of Due Date report Recipients List
     * @param aoMyBatisSession
     * @return
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
    public List<String> getRecipientsList(SqlSession aoMyBatisSession) throws ApplicationException{
        List<String> loRecipientsList = null;
        try
        {
           
            loRecipientsList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, null , MAPPER_CLASS_PROPOSAL_REPORT_ON_DUE_DATE, METHOD_REPORT_RECIPIENTS_LIST, null);
            setMoState("Transaction Success: notification Recipients List fetched successfully");
        }
        catch (ApplicationException aoAppEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of Due Date report Recipients List");
            throw aoAppEx;
        }
        catch (Exception aoEx)
        {
            setMoState("Transaction Failure: Error Occurred while fetching the list of Due Date report Recipients Listr");
            throw new ApplicationException("Transaction Failure: Error Occurred while fetching the list of Due Date report Recipients List", aoEx);
        }

        return loRecipientsList;
    }
    
}

