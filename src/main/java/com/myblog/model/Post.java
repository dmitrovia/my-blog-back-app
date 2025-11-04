package com.myblog.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private byte[] image;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;
}