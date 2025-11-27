package com.nyc.hhs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.model.ActionBean;
import com.nyc.hhs.model.ActionStatusBean;

public class ActionStatusUtil {
	
	private static List<ActionStatusBean> moActionLst = null ;
	private static HashMap<String, Map<Integer,String>>  moActionMapLst = null;
	private static List<String> moAgencyList = null ;
	private static List<String> moActionNameList = null ;

	public static List<ActionStatusBean> getMoActionLst() {
		return moActionLst;
	}

	public static void setMoActionLst(List<ActionStatusBean> aoActionLst) {
		moActionLst = null;
		if(aoActionLst != null ) {
			ActionStatusUtil.moActionLst = aoActionLst;
			moActionMapLst = new HashMap<String, Map<Integer,String>>();
 
			for(int inx = 0 ; inx <  aoActionLst.size() ; inx++) {
				moActionMapLst.put(aoActionLst.get(inx).getAgencyId(), aoActionLst.get(inx).toMap());
			}
		}

		moAgencyList = genAgencyList();
		moActionNameList = genActionNameList();
	}

	public static ActionStatusBean getMoActionByAgency(String aoAgencyId) {
		if(moActionLst == null ) return null;
		for(ActionStatusBean asb : moActionLst  ) {
			if(asb.getAgencyId().equalsIgnoreCase(aoAgencyId)) {
				return asb;
			}
		}
		return null;
	}
	
	
	private static List<String> genAgencyList() {
		if(moActionLst != null ) {
			ArrayList <String> loLst = new ArrayList<String>();
			for(  ActionStatusBean asb : moActionLst ) {
				loLst.add(asb.getAgencyId());
			}
			
			return loLst;
		} else {
			return null;
		}
	}
	
	public static List<String> getAgencyList(){
		return moAgencyList;
	}

	public static List<String> getActionNameList(){
		return moActionNameList;
	}

	private static List<String> genActionNameList() {
		if(moActionLst != null ) {
			ArrayList <String> loLst = new ArrayList<String>();
			for( int loop = 0; loop < HHSR5Constants.TOTAL_NO_OF_ACTION_DROPDOWN_FOR_SETTING ; loop ++) {
				switch( loop+1) {
				case  HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX        :
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX            : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX            : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX       : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX    : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX  : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX        : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX        : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX         : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX       : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX       : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX   : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX       : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX        : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_NAME);
					break;
				case  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX      : 
					loLst.add(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_NAME);
					break;

					default:
				}
			}

			return loLst;
		} else {
			return null;
		}
	}	
	
	public static HashMap<String, Map<Integer, String>> getMoActionMapLst() {
		return moActionMapLst;
	}
	
	public static Map<Integer, String> getMoActionMapByAgency(String aoAgencyId) {
		if(aoAgencyId == null ) new HashMap<Integer,String>();
		
		if( moActionMapLst.containsKey(aoAgencyId) ) {
			return moActionMapLst.get(aoAgencyId);
		}

		return new HashMap<Integer,String>();
	}

	public static boolean isActionEnabled(String aoAgencyId, int aoInx) {
		if(aoAgencyId == null && aoInx < 1 && aoInx > 21) return false;

		Map<Integer,String> loMap = null;

		if( moActionMapLst.containsKey(aoAgencyId) ) {
			loMap = moActionMapLst.get(aoAgencyId);
		}

		if( loMap != null && loMap.get(aoInx) != null 
				&& (loMap.get(aoInx).equalsIgnoreCase(ActionStatusBean.ACTION_DROPDOWN_ENABLE_VALUE) ) )
			return true;
		else 
			return false;
	}

	public static String toStringMapByAgency( String aoAgencyId ) {
		if ( moActionLst == null ) {
			return null;
		}
		
		for( ActionStatusBean asb :  moActionLst) {
			if( asb.getAgencyId().equalsIgnoreCase(aoAgencyId) ) {
				return asb.toString();
			}
		}
		
		return null;
	}

	public static int  getActionInx(String aoValue) {
		
        if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_NAME)
        		|| aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UNSUSPEND_CONTRACT_NAME) ) {
			return  HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_NAME)    
				|| aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UNFLAG_CONTRACT_NAME)    ) {
			return  HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX  ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_NAME) ) {
			return  HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX  ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX  ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX  ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_NAME)  ) {
			return  HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX  ;
		}
        
		return -1;
	}

	public static boolean  getActionEnabledStatus( String aoAgencyId  , String aoValue) {
		if(aoAgencyId == null || aoValue == null )  return false;

        if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_NAME)
        		|| aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UNSUSPEND_CONTRACT_NAME) ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX) ;
        }else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_NAME)    
				|| aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UNFLAG_CONTRACT_NAME)    ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX ) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_NAME) ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX ) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX ) ;
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX ) ;		
		}else if( aoValue.equalsIgnoreCase(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_NAME)  ) {
			return ActionStatusUtil.isActionEnabled(aoAgencyId, HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX ) ; 
		}

		return true;
	}

	//[Start] QC9720
	public static String  pullAllNotification( ) {
		if(moActionLst == null )  return null;

		StringBuffer loSb = new StringBuffer();
		
		for(ActionStatusBean asb : moActionLst) {
			if ( asb.getNotice() != null ) {
				loSb.append(asb.getNotice()+"</BR>");
			}
		}

		if(loSb.length() > 0 ) return loSb.toString();

		return null;
	}
	
	public static String  pullNotificationByAgency(String aoAgencyId ) {
		if(moActionLst == null && aoAgencyId == null )  return null;

		StringBuffer loSb = new StringBuffer();
		
		ActionStatusBean loAsb =   getMoActionByAgency( aoAgencyId ) ;
		
		if(loAsb != null )  return loAsb.getNotice() ;
		else 		return "";
	}

}



