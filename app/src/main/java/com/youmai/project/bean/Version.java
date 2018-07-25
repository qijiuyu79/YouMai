package com.youmai.project.bean;

/**
 * 版本类
 */
public class Version extends HttpBaseBean {

    private VersionBean data;

    public VersionBean getData() {
        return data;
    }

    public void setData(VersionBean data) {
        this.data = data;
    }

    public static class VersionBean{
        private String change_log;
        private String download_url;
        private boolean enforce;
        private int version_code;

        public String getChange_log() {
            return change_log;
        }

        public void setChange_log(String change_log) {
            this.change_log = change_log;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public boolean isEnforce() {
            return enforce;
        }

        public void setEnforce(boolean enforce) {
            this.enforce = enforce;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }
    }
}
