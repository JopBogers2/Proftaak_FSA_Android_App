package com.example.rentmycar.utils

enum class Category {
    ICE, BEV, FCEV;

    companion object {
        val categories by lazy { Category.entries.map { it.name } }
    }
}