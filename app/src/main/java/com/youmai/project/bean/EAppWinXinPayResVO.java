package com.youmai.project.bean;


/**
 * app启动项信息vo对象
 * 
 * @author win7 32
 * 
 */
public class EAppWinXinPayResVO {
    private String appId;
    private String partnerId;
    private String prepayId;
    private String nonceStr;
    private String timeStamp;
    private String packageValue;
    private String sign;
    private Integer status;
    
    public EAppWinXinPayResVO(int status){
        this.status = status;
    }
    
    public String getAppId()
    {
        return appId;
    }
    public void setAppId( String appId )
    {
        this.appId = appId;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public void setPartnerId( String partnerId )
    {
        this.partnerId = partnerId;
    }
    public String getPrepayId()
    {
        return prepayId;
    }
    public void setPrepayId( String prepayId )
    {
        this.prepayId = prepayId;
    }
    public String getNonceStr()
    {
        return nonceStr;
    }
    public void setNonceStr( String nonceStr )
    {
        this.nonceStr = nonceStr;
    }
    public String getTimeStamp()
    {
        return timeStamp;
    }
    public void setTimeStamp( String timeStamp )
    {
        this.timeStamp = timeStamp;
    }
    public String getPackageValue()
    {
        return packageValue;
    }
    public void setPackageValue( String packageValue )
    {
        this.packageValue = packageValue;
    }
    public String getSign()
    {
        return sign;
    }
    public void setSign( String sign )
    {
        this.sign = sign;
    }
    public Integer getStatus()
    {
        return status;
    }
    public void setStatus( Integer status )
    {
        this.status = status;
    }
}
