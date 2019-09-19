package com.example.upop.sdk;
/**
 * 
 * @ClassName SDKConstants
 * @Description acpsdk constant class
 * @date 2016-7-22 下午4:05:54
 *
 */
public class SDKConstants {

	public final static String COLUMN_DEFAULT = "-";

	public final static String KEY_DELIMITER = "#";

	/** memeber variable: blank. */
	public static final String BLANK = "";

	/** member variabel: space. */
	public static final String SPACE = " ";

	/** memeber variable: unline. */
	public static final String UNLINE = "_";

	/** memeber varibale: star. */
	public static final String STAR = "*";

	/** memeber variable: line. */
	public static final String LINE = "-";

	/** memeber variable: add. */
	public static final String ADD = "+";

	/** memeber variable: colon. */
	public final static String COLON = "|";

	/** memeber variable: point. */
	public final static String POINT = ".";

	/** memeber variable: comma. */
	public final static String COMMA = ",";

	/** memeber variable: slash. */
	public final static String SLASH = "/";

	/** memeber variable: div. */
	public final static String DIV = "/";

	/** memeber variable: left . */
	public final static String LB = "(";

	/** memeber variable: right. */
	public final static String RB = ")";

	/** memeber variable: rmb. */
	public final static String CUR_RMB = "RMB";

	/** memeber variable: .page size */
	public static final int PAGE_SIZE = 10;

	/** memeber variable: String ONE. */
	public static final String ONE = "1";

	/** memeber variable: String ZERO. */
	public static final String ZERO = "0";

	/** memeber variable: number six. */
	public static final int NUM_SIX = 6;

	/** memeber variable: equal mark. */
	public static final String EQUAL = "=";

	/** memeber variable: operation ne. */
	public static final String NE = "!=";

	/** memeber variable: operation le. */
	public static final String LE = "<=";

	/** memeber variable: operation ge. */
	public static final String GE = ">=";

	/** memeber variable: operation lt. */
	public static final String LT = "<";

	/** memeber variable: operation gt. */
	public static final String GT = ">";

	/** memeber variable: list separator. */
	public static final String SEP = "./";

	/** memeber variable: Y. */
	public static final String Y = "Y";

	/** memeber variable: AMPERSAND. */
	public static final String AMPERSAND = "&";

	/** memeber variable: SQL_LIKE_TAG. */
	public static final String SQL_LIKE_TAG = "%";

	/** memeber variable: @. */
	public static final String MAIL = "@";

	/** memeber variable: number zero. */
	public static final int NZERO = 0;

	public static final String LEFT_BRACE = "{";

	public static final String RIGHT_BRACE = "}";

	/** memeber variable: string true. */
	public static final String TRUE_STRING = "true";
	/** memeber variable: string false. */
	public static final String FALSE_STRING = "false";

	/** memeber variable: forward success. */
	public static final String SUCCESS = "success";
	/** memeber variable: forward fail. */
	public static final String FAIL = "fail";
	/** memeber variable: global forward success. */
	public static final String GLOBAL_SUCCESS = "$success";
	/** memeber variable: global forward fail. */
	public static final String GLOBAL_FAIL = "$fail";

	public static final String UTF_8_ENCODING = "UTF-8";
	public static final String GBK_ENCODING = "GBK";
	public static final String CONTENT_TYPE = "Content-type";
	public static final String APP_XML_TYPE = "application/xml;charset=utf-8";
	public static final String APP_FORM_TYPE = "application/x-www-form-urlencoded;charset=";
	
	public static final String VERSION_1_0_0 = "1.0.0";
	public static final String VERSION_5_0_0 = "5.0.0";
	public static final String VERSION_5_0_1 = "5.0.1";
	public static final String VERSION_5_1_0 = "5.1.0";
	public static final String SIGNMETHOD_RSA = "01";
	public static final String SIGNMETHOD_SHA256 = "11";
	public static final String SIGNMETHOD_SM3 = "12";
	public static final String UNIONPAY_CNNAME = "中国银联股份有限公司";
	public static final String CERTTYPE_01 = "01";// Encryption public key for sensitive information
	public static final String CERTTYPE_02 = "02";// Encryption public key for magnetic tracks

	/******************************************** Definitions of interfaces in a 5.0 message ********************************************/
	/** Version number. */
	public static final String param_version = "version";
	/** Certificate ID. */
	public static final String param_certId = "certId";
	/** Signature. */
	public static final String param_signature = "signature";
	/** Signature method. */
	public static final String param_signMethod = "signMethod";
	/** Encoding method. */
	public static final String param_encoding = "encoding";
	/** Transaction type. */
	public static final String param_txnType = "txnType";
	/** Transaction sub-type. */
	public static final String param_txnSubType = "txnSubType";
	/** Business type. */
	public static final String param_bizType = "bizType";
	/** Address of foreground notifications. */
	public static final String param_frontUrl = "frontUrl";
	/** Address of background notifications. */
	public static final String param_backUrl = "backUrl";
	/** Access type. */
	public static final String param_accessType = "accessType";
	/** Acquirer code. */
	public static final String param_acqInsCode = "acqInsCode";
	/** Client category. */
	public static final String param_merCatCode = "merCatCode";
	/** Client type. */
	public static final String param_merType = "merType";
	/** Client code. */
	public static final String param_merId = "merId";
	/** Client name. */
	public static final String param_merName = "merName";
	/** Client abbreviation. */
	public static final String param_merAbbr = "merAbbr";
	/** Level-2 client code. */
	public static final String param_subMerId = "subMerId";
	/** Level-2 client name. */
	public static final String param_subMerName = "subMerName";
	/** Level-2 client abbreviation. */
	public static final String param_subMerAbbr = "subMerAbbr";
	/** Cupsecure client code. */
	public static final String param_csMerId = "csMerId";
	/** Client order number. */
	public static final String param_orderId = "orderId";
	/** Transaction time. */
	public static final String param_txnTime = "txnTime";
	/** Delivery time. */
	public static final String param_txnSendTime = "txnSendTime";
	/** Order timeout interval. */
	public static final String param_orderTimeoutInterval = "orderTimeoutInterval";
	/** Payment timeout. */
	public static final String param_payTimeoutTime = "payTimeoutTime";
	/** Default payment method. */
	public static final String param_defaultPayType = "defaultPayType";
	/** Supported payment method. */
	public static final String param_supPayType = "supPayType";
	/** Payment method. */
	public static final String param_payType = "payType";
	/** Custom payment method. */
	public static final String param_customPayType = "customPayType";
	/** Logistics identifier. */
	public static final String param_shippingFlag = "shippingFlag";
	/** Delivery address - Country */
	public static final String param_shippingCountryCode = "shippingCountryCode";
	/** Delivery address - Province */
	public static final String param_shippingProvinceCode = "shippingProvinceCode";
	/** Delivery address - City */
	public static final String param_shippingCityCode = "shippingCityCode";
	/** Delivery address - District */
	public static final String param_shippingDistrictCode = "shippingDistrictCode";
	/** Delivery address - Detail */
	public static final String param_shippingStreet = "shippingStreet";
	/** Goods general category */
	public static final String param_commodityCategory = "commodityCategory";
	/** Goods name */
	public static final String param_commodityName = "commodityName";
	/** Goods URL */
	public static final String param_commodityUrl = "commodityUrl";
	/** Goods unit price */
	public static final String param_commodityUnitPrice = "commodityUnitPrice";
	/** Goods quantity */
	public static final String param_commodityQty = "commodityQty";
	/** Pre-authorize */
	public static final String param_isPreAuth = "isPreAuth";
	/** Currency */
	public static final String param_currencyCode = "currencyCode";
	/** Account type. */
	public static final String param_accType = "accType";
	/** Account number */
	public static final String param_accNo = "accNo";
	/** Payment card type */
	public static final String param_payCardType = "payCardType";
	/** Card issuer code */
	public static final String param_issInsCode = "issInsCode";
	/** Cardholder information */
	public static final String param_customerInfo = "customerInfo";
	/** Transaction amount */
	public static final String param_txnAmt = "txnAmt";
	/** Balance */
	public static final String param_balance = "balance";
	/** Area code */
	public static final String param_districtCode = "districtCode";
	/** Additional area code */
	public static final String param_additionalDistrictCode = "additionalDistrictCode";
	/** Bill type */
	public static final String param_billType = "billType";
	/** Bill number */
	public static final String param_billNo = "billNo";
	/** Bill month */
	public static final String param_billMonth = "billMonth";
	/** Bill query element */
	public static final String param_billQueryInfo = "billQueryInfo";
	/** Bill details */
	public static final String param_billDetailInfo = "billDetailInfo";
	/** Bill balance */
	public static final String param_billAmt = "billAmt";
	/** Bill amount sign */
	public static final String param_billAmtSign = "billAmtSign";
	/** Bound identification number */
	public static final String param_bindId = "bindId";
	/** Risk level */
	public static final String param_riskLevel = "riskLevel";
	/** Number of bound messages */
	public static final String param_bindInfoQty = "bindInfoQty";
	/** Bound information set */
	public static final String param_bindInfoList = "bindInfoList";
	/** Batch number */
	public static final String param_batchNo = "batchNo";
	/** Total transactions */
	public static final String param_totalQty = "totalQty";
	/** Total amount */
	public static final String param_totalAmt = "totalAmt";
	/** File type */
	public static final String param_fileType = "fileType";
	/** File name */
	public static final String param_fileName = "fileName";
	/** Batch file content. */
	public static final String param_fileContent = "fileContent";
	/** Client abstract */
	public static final String param_merNote = "merNote";
	/** Client custom domain */
	// public static final String param_merReserved = "merReserved";//Interface change/deletion
	/** Domain reserved by the requester */
	public static final String param_reqReserved = "reqReserved";// New interface
	/** Reserved domain */
	public static final String param_reserved = "reserved";
	/** Terminal number */
	public static final String param_termId = "termId";
	/** Terminal type */
	public static final String param_termType = "termType";
	/** Interaction mode */
	public static final String param_interactMode = "interactMode";
	/** Identification mode of the card issuer */
	// public static final String param_recognitionMode = "recognitionMode";
	public static final String param_issuerIdentifyMode = "issuerIdentifyMode";// Interface name change
	/** User number of the client */
	public static final String param_merUserId = "merUserId";
	/** Cardholder IP */
	public static final String param_customerIp = "customerIp";
	/** Query serial number */
	public static final String param_queryId = "queryId";
	/** Original transaction query serial number */
	public static final String param_origQryId = "origQryId";
	/** System tracking number */
	public static final String param_traceNo = "traceNo";
	/** Transaction transmission time */
	public static final String param_traceTime = "traceTime";
	/** Settlement date */
	public static final String param_settleDate = "settleDate";
	/** Settlement currency */
	public static final String param_settleCurrencyCode = "settleCurrencyCode";
	/** Settlement amount */
	public static final String param_settleAmt = "settleAmt";
	/** Settlement exchange rate */
	public static final String param_exchangeRate = "exchangeRate";
	/** Exchange date */
	public static final String param_exchangeDate = "exchangeDate";
	/** Response time */
	public static final String param_respTime = "respTime";
	/** Original transaction response code */
	public static final String param_origRespCode = "origRespCode";
	/** Original transaction response information */
	public static final String param_origRespMsg = "origRespMsg";
	/** Response code */
	public static final String param_respCode = "respCode";
	/** Response code information */
	public static final String param_respMsg = "respMsg";
	// Four message fields are added. They are merUserRegDt, merUserEmail, checkFlag, and activateStatus.
	/** Registration time of client user */
	public static final String param_merUserRegDt = "merUserRegDt";
	/** Registration email of client user */
	public static final String param_merUserEmail = "merUserEmail";
	/** Authentication identifier */
	public static final String param_checkFlag = "checkFlag";
	/** Provisioning state */
	public static final String param_activateStatus = "activateStatus";
	/** Encrypt the certificate ID. */
	public static final String param_encryptCertId = "encryptCertId";
	/** User Mac, IMEI serial number, SSID */
	public static final String param_userMac = "userMac";
	/** Related transaction. */
	// public static final String param_relationTxnType = "relationTxnType";
	/** SMS type */
	public static final String param_smsType = "smsType";

	/** Information domain for risk control */
	public static final String param_riskCtrlInfo = "riskCtrlInfo";

	/** Information domain for IC card transaction */
	public static final String param_ICTransData = "ICTransData";

	/** Information domain for VPC transaction */
	public static final String param_VPCTransData = "VPCTransData";

	/** Security type */
	public static final String param_securityType = "securityType";

	/** UnionPay order number */
	public static final String param_tn = "tn";

	/** Installment payment service fee */
	public static final String param_instalRate = "instalRate";

	/** Installment payment service fee */
	public static final String param_mchntFeeSubsidy = "mchntFeeSubsidy";
	
	/** Signed public key certificates */
	public static final String param_signPubKeyCert = "signPubKeyCert";

	/** Encrypted public key certificates */
	public static final String param_encryptPubKeyCert = "encryptPubKeyCert";
	
	/** Certificate type */
	public static final String param_certType = "certType";

}
