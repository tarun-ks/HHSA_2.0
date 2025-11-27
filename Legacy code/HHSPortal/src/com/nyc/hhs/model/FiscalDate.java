package com.nyc.hhs.model;

import java.util.Calendar;
import java.util.Date;

public class FiscalDate {
    private static final int    FIRST_FISCAL_MONTH  = Calendar.JULY;

    /*[Start] R9.4.0 QC9627   */
    private static final String    FIRST_FISCAL_SRT_MONTH  = "07";
    private static final String    FIRST_FISCAL_SRT_DAY_SRT_MONTH  = "01";
    private static final String    FIRST_FISCAL_END_MONTH  = "06";
    private static final String    FIRST_FISCAL_END_DAY_END_MONTH  = "30";
    /*[End] R9.4.0 QC9627   */
    
    private Calendar            calendarDate;

    public FiscalDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }
    public FiscalDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        this.calendarDate = calendar;
    }

    public FiscalDate(Date date) {
        this.calendarDate = Calendar.getInstance();
        this.calendarDate.setTime(date);
    }

    public int getFiscalMonth() {
        int month = calendarDate.get(Calendar.MONTH);
        int result = ((month - FIRST_FISCAL_MONTH - 1) % 12) + 1;
        if (result < 0) {
            result += 12;
        }
        return result;
    }

    /*[Start] R8.10.0 QC9399 Auto Archive data calcuration */
    public int getFiscalDay() {
        int result = calendarDate.get(Calendar.DAY_OF_MONTH);

        return result;
    }
    /*[End] R8.10.0 QC9399 Auto Archive data calcuration */


/*[Start] R8.4.1 QC9513 Auto Archive data calcuration */
    public Date getDate() {
    	if( this.calendarDate == null){
    		return null;
    	}
        return this.calendarDate.getTime();
    }
/*[End] R8.4.1 QC9513 Auto Archive data calcuration */

    public int getFiscalYear() {
        int month = calendarDate.get(Calendar.MONTH);
        int year = calendarDate.get(Calendar.YEAR);
        return (month >= FIRST_FISCAL_MONTH) ? year + 1 : year ;
    }
    
    public int getCalendarMonth() {
    	return calendarDate.get(Calendar.MONTH)+1;
    }

    public int getCalendarYear() {
        return calendarDate.get(Calendar.YEAR);
    }
    
    public int getCalendarDay() {
        return calendarDate.get(Calendar.DAY_OF_MONTH);
    }
    
    public String toString(){
    	return getCalendarMonth()+"/"+ getCalendarDay() +"/"+ getCalendarYear() ;
    }

    /*[Start] R9.4.0 QC9627   */
    public String getFySrtMMDDString(){
    	return FIRST_FISCAL_SRT_MONTH+"/"+ FIRST_FISCAL_SRT_DAY_SRT_MONTH  ;
    }

    public String toFyEndMMDDString(){
    	return FIRST_FISCAL_END_MONTH+"/"+ FIRST_FISCAL_END_DAY_END_MONTH  ;
    }
    /*[End] R9.4.0 QC9627   */

}