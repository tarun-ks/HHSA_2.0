package com.nyc.hhs.service.db.services.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DAOUtil;

public class ShrinkNotificationService extends ServiceState {

   
    
    //User_Notification, Notification_alerts_url
    
    public Integer shrinkUserNotificationData(SqlSession aoMyBatisSession) throws ApplicationException{
        setMoState( "[Start] Archive old User Notification Data \n " );

        //get all data to delete
        List<Integer> loListOfNitoficationId = (List<Integer>) DAOUtil.masterDAO(aoMyBatisSession, null ,
                ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_OLD_USER_NOTIFICATION, null);

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        setMoState( "Total Data to remove is "+ loListOfNitoficationId.size() +" records \n " );

        double loBlkCnt  =  (double) loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList(   (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_NOTIFICATION)   ));

            Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                    ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                    HHSConstants.ARCHIVE_USER_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);

            Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                    ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                    HHSConstants.SHRINK_USER_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);

            loTotalArchivedCnt = loTotalArchivedCnt +  (  ( loArcCnt != null ) ? loArcCnt : 0)  ;
            loTotalRemovedCnt  = loTotalRemovedCnt  +  (  ( loRemCnt != null ) ? loRemCnt : 0)  ;

            setMoState( " User Notification data ("+ loTotalArchivedCnt + ":"+ loTotalRemovedCnt +") are archived!\n " );
        }
        
        setMoState( "[End] Archive old User Notification Data \n " );

        return  loTotalRemovedCnt ;
    }

    public String list2Str(List<Integer> loBlkList){
        StringBuffer sb = new StringBuffer();
        
        sb.append("<");
        for( Integer subStr : loBlkList  ){
            sb.append(" "+ subStr.toString()  +" ");
        }
        sb.append(">");

        return sb.toString();
    }

    public Integer shrinkNotificationAlertUrlData(SqlSession aoMyBatisSession) throws ApplicationException{
        setMoState( "[Start] Archive old Notification Alert URL Data \n " );

        //get all data to delete
        List<Integer> loListOfNitoficationId = (List<Integer>) DAOUtil.masterDAO(aoMyBatisSession, null ,
                ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_OLD_NOTIFICATION_ALERT_URL, null);

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_ALERT_URL, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_ALERT_URL)   ));
            
                Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.ARCHIVE_NOTIFICATION_ALERT_URL_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
                
                Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.SHRINK_NOTIFICATION_ALERT_URL_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
    
                loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0);
                loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Notification Alert URL data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Notification Alert URL Data \n " );

        return  loTotalRemovedCnt ;
    }

    //[Start]Added in R8.4.1 for QC9513  -->
    
    public void archiveGroupNotificationChilderenData(SqlSession aoMyBatisSession ,List<Integer> aoListOfGroupNitoficationId )throws ApplicationException{
        //shrinkGroupNotificationData

        shrinkNotificationData(aoMyBatisSession, aoListOfGroupNitoficationId);
        shrinkNotificationParamValueData(aoMyBatisSession, aoListOfGroupNitoficationId);
        shrinkReturnPaymentNotifHistoryData(aoMyBatisSession, aoListOfGroupNitoficationId);
        shrinkReturnPaymentNotifMappingData(aoMyBatisSession, aoListOfGroupNitoficationId);
    }
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer shrinkGroupNotificationData(SqlSession aoMyBatisSession) throws ApplicationException{
        setMoState( "[Start] Archive old Group Notification Data \n " );

        //get all data to delete
        List<Integer> loListOfNitoficationId = (List<Integer>) DAOUtil.masterDAO(aoMyBatisSession, null ,
                ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, HHSConstants.FETCH_OLD_GROUP_NOTIFICATION, null);

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        archiveGroupNotificationChilderenData( aoMyBatisSession , loListOfNitoficationId );

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION)   ));
            
                Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.ARCHIVE_GROUP_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
                
                Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.SHRINK_GROUP_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
    
                loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0);
                loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Group Notification Data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Group Notification Data \n " );

        return  loTotalRemovedCnt ;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer shrinkNotificationData(SqlSession aoMyBatisSession, List<Integer> loListOfNitoficationId) throws ApplicationException{
        setMoState( "[Start] Archive old Group Notification Data \n " );

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION)   ));
            
                Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.ARCHIVE_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
                
                Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.SHRINK_NOTIFICATION_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
    
                loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0);
                loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Notification Data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Notification Data \n " );

        return  loTotalRemovedCnt ;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer shrinkNotificationParamValueData(SqlSession aoMyBatisSession, List<Integer> loListOfNitoficationId) throws ApplicationException{
        setMoState( "[Start] Archive old Group Notification Data \n " );

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION)   ));
            
                Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.ARCHIVE_NOTIFICATION_PARAM_VAL_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
                
                Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.SHRINK_NOTIFICATION_PARAM_VAL_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
    
                loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0);
                loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Notification Param Value Data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Notification Param Value Data \n " );

        return  loTotalRemovedCnt ;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer shrinkReturnPaymentNotifHistoryData(SqlSession aoMyBatisSession, List<Integer> loListOfNitoficationId) throws ApplicationException{
        setMoState( "[Start] Archive old Group Notification Data \n " );

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION)   ));
            
                Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.ARCHIVE_RETURN_PAYMENT_NOTIF_HISTORY_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
                
                Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                        ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                        HHSConstants.SHRINK_RETURN_PAYMENT_NOTIF_HISTORY_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);
    
                loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0);
                loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Return Payment Notification History Data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Return Payment Notification History Data \n " );

        return  loTotalRemovedCnt ;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer shrinkReturnPaymentNotifMappingData(SqlSession aoMyBatisSession, List<Integer> loListOfNitoficationId) throws ApplicationException{
        setMoState( "[Start] Archive old Group Notification Data \n " );

        //make them a groups that have 100 records for each
        if(loListOfNitoficationId == null || loListOfNitoficationId.isEmpty() ){
            return 0;
        }

        //loListOfNitoficationId.subList(fromIndex, toIndex);
        double loBlkCnt  =  (double)loListOfNitoficationId.size()/100;
        ArrayList<List<Integer>> loBlkList = new ArrayList<List<Integer>>();

        for( int loop = 0 ; loop < loBlkCnt  ; loop ++){
            if( loBlkCnt < (loop+1) ){
                loBlkList.add( loListOfNitoficationId.subList((((loop)*100) ), loListOfNitoficationId.size() ) )  ;
            }else{
                loBlkList.add( loListOfNitoficationId.subList( (((loop)*100)) , (((loop+1)*100)))  )  ;
            }
        }

        Integer loTotalArchivedCnt = 0;
        Integer loTotalRemovedCnt = 0;

        // iterlate to archive and delete all data by group
        for( List<Integer> loNotiId : loBlkList ){
            Map<String,Object> loHashMap =   new  HashMap<String,Object> () ;
            ArrayList<Integer> loArrStr = new ArrayList<Integer>();
            loArrStr.addAll(loNotiId);
            loHashMap.put(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION, loArrStr );

            setMoState(  "\nFinal>>"+ list2Str( (List) loHashMap.get(HHSConstants.IN_BOUND_PARAM_GROUP_NOTIFICATION)   ));

            Integer loArcCnt = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                    ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                    HHSConstants.ARCHIVE_RETURN_PAYMENT_NOTIF_MAPPING_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);

            Integer loRemCnt =  (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHashMap ,
                    ApplicationConstants.MAPPER_CLASS_NOTIFICATION_MAPPER, 
                    HHSConstants.SHRINK_RETURN_PAYMENT_NOTIF_MAPPING_DATA, HHSConstants.JAVA_UTIL_HASH_MAP);

            loTotalArchivedCnt = loTotalArchivedCnt + (  ( loArcCnt != null ) ? loArcCnt : 0) ;
            loTotalRemovedCnt  = loTotalRemovedCnt  + (  ( loRemCnt != null ) ? loRemCnt : 0) ;

            setMoState( " Return Payment Notification Mapping Data ("+  loArcCnt + ":"+ loRemCnt + ") are archived!\n " );
        }

        setMoState( "[End] Archive old Return Payment Notification Mapping Data \n " );

        return  loTotalRemovedCnt ;
    }
    //[End]Added in R8.4.1 for QC9513  -->    
    
}

