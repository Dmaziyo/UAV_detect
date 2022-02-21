package com.example.login.Data;

import org.litepal.crud.LitePalSupport;

public class CokAndMqt extends LitePalSupport {
    private String cookie;
    private String mqt_sub;
    private int id;
    private String m_id;
    private String u_id;

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public CokAndMqt() {
    }

    public CokAndMqt(String cookie, String mqt_sub) {
        this.cookie = cookie;
        this.mqt_sub = mqt_sub;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getMqt_sub() {
        return mqt_sub;
    }

    public void setMqt_sub(String mqt_sub) {
        this.mqt_sub = mqt_sub;
    }
    @Override
    public boolean save() {
        boolean s = super.save();//1先保存生成id
        setId(getBaseObjId());//2设置id
        update(getBaseObjId());//3更新数据，这样每次获取它的实例就能保证正确获取id了。
        return s;
    }
    private int setId(long baseObjId) {
        return id;
    }
    public int getId() {
        return id;
    }

}
