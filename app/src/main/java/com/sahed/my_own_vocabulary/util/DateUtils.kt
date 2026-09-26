package com.sahed.my_own_vocabulary.util

import java.util.Calendar

object DateUtils {
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
}
