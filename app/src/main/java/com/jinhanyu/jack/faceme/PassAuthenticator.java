package com.jinhanyu.jack.faceme;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by anzhuo on 2016/11/8.
 */

public class PassAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication()
    {
        /**
         * 这个地方需要添加上自己的邮箱的账号和密码
         */
        String username ="kinpowoo@outlook.com";
        String password ="3344???a";
        return new PasswordAuthentication(username,password);
    }

}
