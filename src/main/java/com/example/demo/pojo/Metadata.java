package com.example.demo.pojo;

import lombok.Data;

@Data
public class Metadata
{
    private String multiTask;

    private String task;

    private String taskGroupId;

    private String txLabel;

    private String processName;

    private String processId;

    private String requestId;

    private String requestDate;

    private String version;

    private String attempt;

    private String requestAuthToken;

    private String stageId;
}
