package com.nyc.hhs.daomanager.servicetestR7;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	FinancialsListServiceR7.class,
	ContractBudgetModificationServiceTestR7.class,
	InvoiceServiceTestR7.class
})

public class JunitTestSuite {   
}  	