package com.smart.dao;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestCases {//用来导出数据库的所有表 FlatXml格式
    public static void main(String[] args) throws Exception {
        // database connection 数据库链接
        Class.forName("org.postgresql.Driver");// 加载数据库驱动
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/sampledb2", "eugeneyoung", "1234");// 获取数据库链接
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);// 生成DbUnit的数据集的数据库链接
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

    }
}
