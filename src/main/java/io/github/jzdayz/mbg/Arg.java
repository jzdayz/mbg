package io.github.jzdayz.mbg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arg {
    private String jdbc;
    private String user;
    private String catalog;
    private String pwd;
    private String dao;
    private String model;
    private String xml;
    private String type;
    private String table;
    private String mbpPackage;
    private DbType dbType;

    public enum DbType{
        MYSQL("com.mysql.cj.jdbc.Driver"),
        SQL_SERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
        ORACLE("oracle.jdbc.driver.OracleDriver");

        private final String driver;

        DbType(String driver) {
            this.driver = driver;
        }


        public String getDriver() {
            return driver;
        }
    }
}