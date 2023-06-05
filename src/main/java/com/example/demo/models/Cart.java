package com.example.demo.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private long id;
    private long user_id;
    private List<Product> cart;
}
