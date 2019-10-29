package com.yh.autocontrolwechat.bean;

import java.util.List;

public class WechatGroupInfoBean {

    public List<GroupInfoBean> groupInfo;

    public List<GroupInfoBean> getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(List<GroupInfoBean> groupInfo) {
        this.groupInfo = groupInfo;
    }

    public static class GroupInfoBean {
        /**
         * id : 1
         * group_name : 北京二手车圈①群 二手车
         * areacode : 110000
         * simple_name : 北京①群
         */

        public int id;
        public String group_name;
        public String areacode;
        public String simple_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getSimple_name() {
            return simple_name;
        }

        public void setSimple_name(String simple_name) {
            this.simple_name = simple_name;
        }

        @Override
        public String toString() {
            return "GroupInfoBean{" +
                    "id=" + id +
                    ", group_name='" + group_name + '\'' +
                    ", areacode='" + areacode + '\'' +
                    ", simple_name='" + simple_name + '\'' +
                    '}';
        }
    }
}
