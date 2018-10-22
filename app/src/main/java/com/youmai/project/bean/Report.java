package com.youmai.project.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Report extends HttpBaseBean {

    private List<ReportData> data=new ArrayList<>();

    public List<ReportData> getData() {
        return data;
    }

    public void setData(List<ReportData> data) {
        this.data = data;
    }

    public static class ReportData implements Serializable{
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
