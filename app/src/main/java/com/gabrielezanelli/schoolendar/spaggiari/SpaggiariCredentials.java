package com.gabrielezanelli.schoolendar.spaggiari;

public class SpaggiariCredentials {
    private String schoolCode;
    private String userCode;
    private String password;
    private boolean empty;

    public SpaggiariCredentials(String schoolCode, String userCode, String password) {
        this.schoolCode = schoolCode;
        this.userCode = userCode;
        this.password = password;
        if(schoolCode.equals("") && userCode.equals("") && password.equals(""))
            empty = true;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEmpty() {
        return empty;
    }
}
