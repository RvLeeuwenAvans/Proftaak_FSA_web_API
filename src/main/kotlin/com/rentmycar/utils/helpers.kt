package com.rentmycar.utils

fun isNumeric(str: String): Boolean = str.all { char -> char.isDigit() }
