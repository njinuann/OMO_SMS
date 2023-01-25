CREATE OR REPLACE PROCEDURE NAWIRILIVE.RPT_DP_CUSTOMER_STMT_NAKU(
      advice_cursor IN OUT Supernova_Package.ref_cursor,
      i_bu_id IN BUSINESS_UNIT.BU_ID%TYPE,
      i_acct_no IN ACCOUNT.acct_no%TYPE,
      i_start_date IN DEPOSIT_ACCOUNT_HISTORY.TRAN_DT%TYPE,
      i_end_date IN DEPOSIT_ACCOUNT_HISTORY.TRAN_DT%TYPE,
      i_event_cd in event.event_cd%type,
      user_bu_id BUSINESS_UNIT.BU_ID%TYPE,
      request_mode  IN varchar2
)
AS
v_sql_text varchar2(20000);
tv_ctrl_parameter varchar2(30);
tv_deposit_account_summary varchar2(30);
v_sql_subquery_bu_id varchar2(100);
v_sql_subquery_acct_no varchar2(100);
v_sql_subquery_start_dt varchar2(1000);
v_sql_subquery_end_dt varchar2(1000);
v_event_cd varchar2(1000);
v_sqlsubquery_user_bu_addr varchar2(1000);
v_sqlquery_user_bu_name varchar2(1000);
subquery_start_date varchar2(1000);
acct_hist_object varchar(100):='acct_no';
BEGIN
select decode(i_bu_id, null, ' ', ' and t2.main_branch_id =  '|| i_bu_id) into v_sql_subquery_bu_id from dual;
select decode(i_acct_no, null, ' ', ' and t2.acct_no = ''' || i_acct_no ||'''') into v_sql_subquery_acct_no from dual;
select decode(i_event_cd, null, ' ', ' and t11.event_cd = ''' || i_event_cd ||'''') into v_event_cd from dual;
select decode(i_start_date , null,' ', 'and t1.tran_dt >= to_date('''||to_char(i_start_date, 'DD/MM/YYYY')||''', ''DD/MM/YYYY'')') into v_sql_subquery_start_dt from dual;
select decode(i_end_date, null, ' ', 'and t1.tran_dt <= to_date('''||to_char(i_end_date, 'DD/MM/YYYY')||''', ''DD/MM/YYYY'')') into v_sql_subquery_end_dt from dual;
select decode(user_bu_id, null, ' ', ',(select bu_nm from business_unit where bu_id = '||user_bu_id|| ') as user_BU') into v_sqlquery_user_bu_name from  dual;
select decode(i_start_date , null,' ', 'and da.tran_dt >= to_date('''||to_char(i_start_date, 'DD/MM/YYYY')||''', ''DD/MM/YYYY'')') into subquery_start_date from dual;
IF(i_acct_no is not null and i_start_date is not null and i_end_date is not null) THEN
	BEGIN
	SELECT DISTINCT(da.ACCT_NO) INTO acct_hist_object FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = i_acct_no and da.tran_dt >= to_date(to_char(i_start_date, 'DD/MM/YYYY'), 'DD/MM/YYYY') and da.tran_dt <= to_date(to_char(i_end_date, 'DD/MM/YYYY'), 'DD/MM/YYYY');
	EXCEPTION WHEN NO_DATA_FOUND THEN
	acct_hist_object:=NULL;
	END;
ELSIF(i_acct_no is not null and i_start_date is not null) THEN
	BEGIN
	SELECT DISTINCT(da.ACCT_NO) INTO acct_hist_object FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = i_acct_no and da.tran_dt >= to_date(to_char(i_start_date, 'DD/MM/YYYY'), 'DD/MM/YYYY');
	EXCEPTION WHEN NO_DATA_FOUND THEN
	acct_hist_object:=NULL;
	END;
ELSIF(i_acct_no is not null and i_end_date is not null) THEN
	BEGIN
	SELECT DISTINCT(da.ACCT_NO) INTO acct_hist_object FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = i_acct_no and da.tran_dt <= to_date(to_char(i_end_date, 'DD/MM/YYYY'), 'DD/MM/YYYY');
	EXCEPTION WHEN NO_DATA_FOUND THEN
	acct_hist_object:=NULL;
	END;
ELSIF (i_acct_no is not null) THEN
	BEGIN
	SELECT DISTINCT(da.ACCT_NO) INTO acct_hist_object FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = i_acct_no;
	EXCEPTION WHEN NO_DATA_FOUND THEN
	acct_hist_object:=NULL;
	END;
ELSIF(i_acct_no is not null and i_start_date is not null and i_end_date is not null and i_event_cd is not null) THEN
	BEGIN
	SELECT DISTINCT(da.ACCT_NO) INTO acct_hist_object FROM DEPOSIT_ACCOUNT_HISTORY da, event e
	WHERE da.event_id = e.event_id and da.ACCT_NO = i_acct_no and da.tran_dt >= to_date(to_char(i_start_date, 'DD/MM/YYYY'), 'DD/MM/YYYY')
	and da.tran_dt <= to_date(to_char(i_end_date, 'DD/MM/YYYY'), 'DD/MM/YYYY')
	and e.event_cd = i_event_cd;
	EXCEPTION WHEN NO_DATA_FOUND THEN
	acct_hist_object:=NULL;
	END;
END IF;
   IF (request_mode = 'REQUEST') THEN
      tv_ctrl_parameter:= 'ctrl_parameter';
      tv_deposit_account_summary:= 'deposit_account_summary';
   ELSIF (request_mode = 'SCHEDULER') THEN
      tv_ctrl_parameter:= 'mv_ctrl_parameter';
      tv_deposit_account_summary:= 'mv_deposit_account_summary';
   ELSE
      tv_ctrl_parameter:= 'ctrl_parameter';
      tv_deposit_account_summary:= 'deposit_account_summary';
   END IF;
v_sqlsubquery_user_bu_addr :=
' , (SELECT BU_ADDR_LINE_1 || '||
'    DECODE (BU_ADDR_LINE_2, NULL, '''','',''||CHR(10)||BU_ADDR_LINE_2) ||'||
'    DECODE (BU_ADDR_LINE_3, NULL, '''','',''||CHR(10)||BU_ADDR_LINE_3) ||'||
'    DECODE (BU_ADDR_LINE_4, NULL, '''','',''||CHR(10)||BU_ADDR_LINE_4) ||'||
'    '','' || CHR(10) ||CITY_DESC  || '',''||CHR(10)  ||'||
'	' ||' STATE_DESC||CHR(10)||CNTRY_NM from business_unit, CITY, STATE, COUNTRY where business_unit.city_id = city.city_id  '||
'   	and city.state_id = STATE.state_id and state.CNTRY_ID  = COUNTRY.CNTRY_ID and bu_id = '||user_bu_id||') as bu_address '; 
IF (acct_hist_object IS NOT NULL) THEN  
v_sql_text :=       
'  SELECT t1.tran_dt, t1.value_dt, substr(t1.tran_desc,1,28) as tran_desc, t1.tran_ref_txt, t1.chq_no, t11.EVENT_CD, t11.EVENT_DESC,  '||
'	t3.crncy_cd_iso, t1.ACCT_AMT, t1.stmnt_bal, t1.acct_no, t3.crncy_nm,   t2.ACCT_NM,     '||
'	t6.bu_no, t6.bu_nm, t12.CLEARED_BAL, t12.UNCLEARED1_BAL,'||
'	nvl(abs((SELECT SUM(da.ACCT_AMT) FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = t1.ACCT_NO '||subquery_start_date||' '||
'	AND da.tran_dt <= TO_DATE((SELECT DISPLAY_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S02''),''dd/mm/yyyy'')'||
'	AND da.DR_CR_IND=''DR'' and da.EVENT_ID IN (SELECT ty.event_id FROM TXN_TYPE ty,TXN_CLASS_PROCESS tcp WHERE ty.TRAN_CLASS_ID = tcp.TRAN_CLASS_ID'||
'	AND tcp.TRAN_PROC_CD = ''dp.dr.cleared.bal''))),0) AS total_dr_amt,'||
'	nvl((SELECT SUM(da.ACCT_AMT) FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = t1.ACCT_NO '||subquery_start_date||' '||
'	AND da.tran_dt <= TO_DATE((SELECT DISPLAY_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S02''),''dd/mm/yyyy'')'||
'	AND da.DR_CR_IND=''CR'' and da.EVENT_ID IN (SELECT ty.event_id FROM TXN_TYPE ty,TXN_CLASS_PROCESS tcp WHERE ty.TRAN_CLASS_ID = tcp.TRAN_CLASS_ID'||
'	AND tcp.TRAN_PROC_CD = ''dp.cr.cleared.bal'')),0) AS total_cr_amt,'||
'	t3.crncy_sym, t4.cust_nm, t5.prod_desc, t1.dr_cr_ind,t10.CHANNEL_DESC,       '||
'	DECODE (t1.dr_cr_ind, ''CR'', t1.ACCT_AMT, '''') AS credit_amt,        '||
'	DECODE (t1.dr_cr_ind, ''DR'', t1.ACCT_AMT, '''') AS debit_amt,        '||
'	t7.addr_line_1 || '||
'	DECODE (t7.addr_line_2, NULL, '''','',''||CHR(10)||t7.addr_line_2) ||'||
'	DECODE (t7.addr_line_3, NULL, '''','',''||CHR(10)||t7.addr_line_3) ||'||
'	DECODE (t7.ADDR_LINE_4, NULL, '''','',''||CHR(10)||t7.addr_line_4) ||'||
'	'','' || CHR(10) || t7.CITY  || '',''||CHR(10)  ||'||
'	' ||' t7.STATE||CHR(10)||t8.cntry_nm AS cust_address, '||
'	  (SELECT PARAM_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S04'') AS BANK_NAME '||
' '||v_sqlsubquery_user_bu_addr||
' '||v_sqlquery_user_bu_name||
'  FROM DEPOSIT_ACCOUNT_HISTORY t1,'||
'	ACCOUNT t2,            '||
'	CURRENCY t3,             '||
'	CUSTOMER t4,             '||
'	PRODUCT t5,             '||
'	BUSINESS_UNIT t6,             '||
'	ADDRESS t7,             '||
'	COUNTRY t8,             '||
'	CUSTOMER_ADDRESS t9, '||
'	SERVICE_CHANNEL t10,   '||
'	EVENT t11,  '||
'	'||tv_deposit_account_summary||' T12,'||
'	TXN_TYPE t13,	'||
'	TXN_CLASS_PROCESS t14 '||
'  WHERE t1.ACCT_NO = t2.ACCT_NO           '||
'	AND t2.crncy_id = t3.crncy_id           '||
'       AND t2.cust_id = t4.cust_id           '||
'       AND t2.prod_id = t5.prod_id            '||
'       AND t2.main_branch_id = t6.bu_id            '||
'       AND t2.cust_id = t9.cust_id           '||
'       AND t7.addr_id = t9.addr_id            '||
'       AND t4.resident_cntry_id = t8.cntry_id          '||
'       AND t9.primary_fg = ''Y''        '||
'       AND t10.CHANNEL_ID = t1.CHANNEL_ID '||
'	AND t11.event_id = t1.event_id '||
'	AND t12.ACCT_NO = t2.ACCT_NO'||
'	AND t1.event_id = t13.event_id'||
'	AND t13.TRAN_CLASS_ID = t14.TRAN_CLASS_ID'||
'	AND t14.TRAN_PROC_CD in (''dp.dr.cleared.bal'',''dp.cr.cleared.bal'')'||
'	'||v_sql_subquery_acct_no||
'	'||v_sql_subquery_start_dt||
'	'||v_sql_subquery_end_dt||
'	'||v_sql_subquery_bu_id||
'	'||v_event_cd||
'  ORDER BY '||
'	T1.TRAN_DT,T1.ACCT_HIST_ID';
ELSE
v_sql_text :=       
'	SELECT LAST_ACCRUAL_TIME as TRAN_DT, LAST_ACCRUAL_TIME AS VALUE_DT, ''No_Account_History_Found'' AS TRAN_DESC, ''No_Account_History_Found'' AS TRAN_REF_TXT,'||
'	''No_Account_History_Found'' AS CHQ_NO, ''No_Account_History_Found'' AS EVENT_CD, ''No_Account_History_Found'' AS EVENT_DESC,  '||
'	NULL AS CRNCY_CD_ISO, NULL AS ACCT_AMT, NULL AS STMNT_BAL, T2.ACCT_NO, T3.CRNCY_NM,   T2.ACCT_NM,     '||
'	T6.BU_NO, T6.BU_NM, T12.CLEARED_BAL, T12.UNCLEARED1_BAL,   nvl(T16.DEBIT_INTEREST_RATE,0) as DR_CALCULATED_EFF_INT_RATE,'||
'	nvl(T15.CREDIT_INTEREST_RATE,0) as CR_CALCULATED_EFF_INT_RATE, '||
'	nvl(abs((SELECT SUM(da.ACCT_AMT) FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = t2.ACCT_NO '||subquery_start_date||' '||
'	AND da.tran_dt <= TO_DATE((SELECT DISPLAY_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S02''),''dd/mm/yyyy'')'||
'	AND da.DR_CR_IND=''DR'' and da.EVENT_ID IN (SELECT ty.event_id FROM TXN_TYPE ty,TXN_CLASS_PROCESS tcp WHERE ty.TRAN_CLASS_ID = tcp.TRAN_CLASS_ID'||
'	AND tcp.TRAN_PROC_CD = ''dp.dr.cleared.bal''))),0) AS total_dr_amt,'||
'	nvl((SELECT SUM(da.ACCT_AMT) FROM DEPOSIT_ACCOUNT_HISTORY da WHERE da.ACCT_NO = t2.ACCT_NO '||subquery_start_date||' '||
'	AND da.tran_dt <= TO_DATE((SELECT DISPLAY_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S02''),''dd/mm/yyyy'')'||
'	AND da.DR_CR_IND=''CR'' and da.EVENT_ID IN (SELECT ty.event_id FROM TXN_TYPE ty,TXN_CLASS_PROCESS tcp WHERE ty.TRAN_CLASS_ID = tcp.TRAN_CLASS_ID'||
'	AND tcp.TRAN_PROC_CD = ''dp.cr.cleared.bal'')),0) AS total_cr_amt,'||
'	T3.CRNCY_SYM, (SELECT P.FIRST_NM||'' ''||P.LAST_NM FROM PERSON P WHERE P.CUST_ID = T4.CUST_ID) AS CUST_NM, '||
'	(SELECT O.ORGANISATION_NM FROM ORGANISATION O WHERE O.CUST_ID = T4.CUST_ID) AS ORG_NM, '||
'	T5.PROD_DESC, NULL AS DR_CR_IND,     '||
'	0 AS CREDIT_AMT,        '||
'	0 AS DEBIT_AMT,      '||
'	t7.addr_line_1 || '||
'	DECODE (t7.addr_line_2, NULL, '''','',''||CHR(10)||t7.addr_line_2) ||'||
'	DECODE (t7.addr_line_3, NULL, '''','',''||CHR(10)||t7.addr_line_3) ||'||
'	DECODE (t7.ADDR_LINE_4, NULL, '''','',''||CHR(10)||t7.addr_line_4) ||'||
'	'','' || CHR(10) || t7.CITY  || '',''||CHR(10)  ||'||
'	' ||' t7.STATE||CHR(10)||t8.cntry_nm AS cust_address, '||
'	  (SELECT PARAM_VALUE FROM '||tv_ctrl_parameter||' WHERE PARAM_CD = ''S04'') AS BANK_NAME '||
'	'||v_sqlsubquery_user_bu_addr||
'	'||v_sqlquery_user_bu_name||
'	 FROM ACCOUNT T2,            '||
'	 CURRENCY T3,             '||
'	 CUSTOMER T4,             '||
'	 PRODUCT T5,             '||
'	 BUSINESS_UNIT T6,             '||
'	 ADDRESS T7,             '||
'	 COUNTRY T8,             '||
'	 CUSTOMER_ADDRESS T9, '||
'	 DEPOSIT_ACCOUNT_SUMMARY T12,'||
'	(SELECT AC.ACCT_NO, DECODE(DECODE(DAI.OVR_FG,''N'',DPI.ABSOLUTE_RATE,DAI.ABSOLUTE_RATE), NULL,'||
'	DECODE(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_TY_CD,DAI.MARGIN_TY_CD),NULL, IR.INDEX_RATE,''POINTS'','||
'	(IR.INDEX_RATE+(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_RATE, DAI.MARGIN_RATE)/100)), '||
'	(IR.INDEX_RATE*(1+(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_RATE, DAI.MARGIN_RATE)/100)))),'||
'	DECODE(DAI.OVR_FG,''N'',DPI.ABSOLUTE_RATE,DAI.ABSOLUTE_RATE)) AS CREDIT_INTEREST_RATE '||
'	FROM'||
'	ACCOUNT AC,'||
'	DEPOSIT_ACCOUNT_INTEREST DAI,'||
'	DEPOSIT_PRODUCT_INTEREST DPI,'||
'	INDEX_RATE IR'||
'	WHERE AC.ACCT_ID = DAI.DEPOSIT_ACCT_ID'||
'	AND DPI.DEPOSIT_PROD_INT_ID = DAI.DEPOSIT_PROD_INT_ID'||
'	AND DPI.INDEX_RATE_ID = IR.INDEX_RATE_ID(+)'||
'	AND DPI.DRCR_IND = ''CR'' ) T15,'||
'	(SELECT AC.ACCT_NO, DECODE(DECODE(DAI.OVR_FG,''N'',DPI.ABSOLUTE_RATE,DAI.ABSOLUTE_RATE), NULL,'||
'	DECODE(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_TY_CD,DAI.MARGIN_TY_CD),NULL, IR.INDEX_RATE,''POINTS'','||
'	(IR.INDEX_RATE+(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_RATE, DAI.MARGIN_RATE)/100)), '||
'	(IR.INDEX_RATE*(1+(DECODE(DAI.OVR_FG,''N'',DPI.MARGIN_RATE, DAI.MARGIN_RATE)/100)))),'||
'	DECODE(DAI.OVR_FG,''N'',DPI.ABSOLUTE_RATE,DAI.ABSOLUTE_RATE)) AS DEBIT_INTEREST_RATE '||
'	FROM'||
'	ACCOUNT AC,'||
'	DEPOSIT_ACCOUNT_INTEREST DAI,'||
'	DEPOSIT_PRODUCT_INTEREST DPI,'||
'	INDEX_RATE IR'||
'	WHERE AC.ACCT_ID = DAI.DEPOSIT_ACCT_ID'||
'	AND DPI.DEPOSIT_PROD_INT_ID = DAI.DEPOSIT_PROD_INT_ID'||
'	AND DPI.INDEX_RATE_ID = IR.INDEX_RATE_ID(+)'||
'	AND DPI.DRCR_IND = ''DR'' ) T16'||
'	  WHERE T2.CRNCY_ID = T3.CRNCY_ID           '||
'	       AND T2.CUST_ID = T4.CUST_ID           '||
'	       AND T2.PROD_ID = T5.PROD_ID            '||
'	       AND T2.MAIN_BRANCH_ID = T6.BU_ID            '||
'	       AND T2.CUST_ID = T9.CUST_ID           '||
'	       AND T7.ADDR_ID = T9.ADDR_ID            '||
'	       AND T4.RESIDENT_CNTRY_ID = T8.CNTRY_ID          '||
'	       AND T9.PRIMARY_FG = ''Y''        '||
'	AND T12.ACCT_NO = T2.ACCT_NO'||
'	AND T2.ACCT_NO = T15.ACCT_NO(+)'||
'	AND T2.ACCT_NO = T16.ACCT_NO(+)'||
'	'||v_sql_subquery_acct_no;
END IF;
OPEN advice_cursor FOR v_sql_text;
END;
/