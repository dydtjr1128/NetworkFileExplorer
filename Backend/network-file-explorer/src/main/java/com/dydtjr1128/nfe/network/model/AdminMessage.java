package com.dydtjr1128.nfe.network.model;

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

    private int state;
    private String ip;
}