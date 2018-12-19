package com.liuyx.wbspider.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.*;

public class Account {
    private String account;
    private String password;

    public Account() {
    }

    public Account(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Account{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static List<Account> genAccountFromText(File file){
        List<Account> accounts = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = br.readLine()) != null){//使用readLine方法，一次读一行
                String[] cells = line.split(" ");
                if(cells.length == 2){
                    accounts.add(new Account(cells[0], cells[1]));
                }
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return accounts;
    }
}
