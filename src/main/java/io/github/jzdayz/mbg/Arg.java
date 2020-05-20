package io.github.jzdayz.mbg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Arg {
    private String jdbc;
    private String user;
    private String pwd;
}