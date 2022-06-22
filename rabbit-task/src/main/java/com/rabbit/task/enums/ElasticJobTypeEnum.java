package com.rabbit.task.enums;

/**
 * @author ym.y
 * @description
 * @date 14:05 2022/3/29
 */
public enum ElasticJobTypeEnum {
    SIMPLE_JOB("SimpleJob","简单类型job"),
    DATAFLOW_JOB("DataflowJob","流式类型job"),
    SCRIPT("ScriptJob","脚本类型job");
    private String type;
    private String desc;

    ElasticJobTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
