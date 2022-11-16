/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ruby.acx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Pecherk
 */
public class AXConstant
{
    public static final String ISO_APPROVED = "00";
    public static final BigDecimal HUNDRED = new BigDecimal(100);
    public static final BigDecimal POINTONE = new BigDecimal(0.1).setScale(1, RoundingMode.DOWN);
    public static final String XAPI_APPROVED = "00";
    public static final String XAPI_FAILED = "01";
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public static final DateTimeFormatter displayDateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    public static final SimpleDateFormat cbsDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat statementDateFormat = new SimpleDateFormat("dd/MM/yy");
    public static final SimpleDateFormat keyDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
}
