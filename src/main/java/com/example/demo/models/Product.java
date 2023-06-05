package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String photo;
    private String name;
    private int price;
    private String type;

    @Override
    public String toString() {
        return "{" +
                "    id: " + id + ",\n" +
                "    photo: " + photo + ",\n" +
                "    name: " + name + ",\n" +
                "    price: " + price + ",\n" +
                "    type: " + type + "\n" +
                '}';
    }
}
