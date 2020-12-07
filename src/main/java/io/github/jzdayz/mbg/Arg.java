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

    private String schema;

    private String pwd;

    private String dao;

    private String model;

    private String xml;

    private String type;

    private String table;

    private String tfType;

    private String mbpPackage;

    private DbType dbType;

    private boolean swagger2;

    private boolean lombok;

    private String tableNameFormat;

    public enum DbType {
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