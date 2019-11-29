package com.dydtjr1128.nfe.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class AdminMessage implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    public static final int ADD = 0;
    @JsonIgnore
    public static final int REMOVE = 1;
    @JsonIgnore
    public static final int DOWNLOAD_SUCCESS = 2;
    @JsonIgnore
    public static final int DOWNLOAD_FAIL = 3;
    @JsonIgnore
    public static final int UPLOAD_SUCCESS = 4;
    @JsonIgnore
    public static final int UPLOAD_FAIL = 5;

    private int state;
    private String ip;
}