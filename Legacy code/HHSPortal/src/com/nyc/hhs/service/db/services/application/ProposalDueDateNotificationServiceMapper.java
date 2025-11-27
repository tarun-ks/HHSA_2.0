package com.nyc.hhs.service.db.services.application;


import java.util.ArrayList;

import com.nyc.hhs.model.ProposalReportBean;

public interface ProposalDueDateNotificationServiceMapper {

    public ArrayList<ProposalReportBean> getProposalDueDateList(String dueDate);

    public ArrayList<String> getReportRecipientsList();
}
