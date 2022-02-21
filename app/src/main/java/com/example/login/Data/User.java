package com.example.login.Data;


import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {
        private boolean remeber_password=false;
        private String name;
        private String job;
        private String sex;

    public User(String name, String sex, String account, String password, String phone_num) {
        this.name = name;
        this.sex = sex;
        this.account = account;
        this.password = password;
        this.phone_num = phone_num;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    private String account;
        private String password;
        private String id_card;
        private String phone_num;
        private String ID;
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


       private int id;
       public static User myUser;

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

    public void setId(int id) {
        this.id = id;
    }
    public User() {
    }

    public boolean isRemeber_password() {
        return remeber_password;
    }

    public void setRemeber_password(boolean remeber_password) {
        this.remeber_password = remeber_password;
    }



    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (phone_num != null ? !phone_num.equals(user.phone_num) : user.phone_num != null) return false;
        return password != null ? password.equals(user.password) : user.password == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (phone_num != null ? phone_num.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
