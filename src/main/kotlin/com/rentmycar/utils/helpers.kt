package com.rentmycar.utils

fun isNumeric(str: String): Boolean = str.all { char -> char.isDigit() }

fun sanitizeId(id: String? = null): Int =
    if (id == null || !isNumeric(id)) -1 else id.toInt()