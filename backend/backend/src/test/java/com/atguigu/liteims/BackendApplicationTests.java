package com.atguigu.liteims;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {

        // 2. 写 Excel
        List<DemoData> list = new ArrayList<>();
        list.add(new DemoData("a",new Date(),11.11));
        list.add(new DemoData("b",new Date(),22.22));
        // 工作簿   文件
        // 工作表  sheet
        // Row   column    cell
        EasyExcel.write("D:/test.xlsx",DemoData.class).sheet("测试").doWrite(list);
    }

}


