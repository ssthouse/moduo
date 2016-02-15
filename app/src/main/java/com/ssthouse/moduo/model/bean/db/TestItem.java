package com.ssthouse.moduo.model.bean.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 测试item
 * Created by ssthouse on 2016/1/23.
 */
@Table(name = "TestSqlite")
public class TestItem extends Model {

    @Column(name = "TestColumn1")
    public String testColumn1;

    public TestItem(){
        super();
    }

    public TestItem(String testColumn1) {
        super();
        this.testColumn1 = testColumn1;
    }
}
