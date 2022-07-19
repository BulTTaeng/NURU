package com.example.nuru.utility

import java.text.ParseException
import java.text.SimpleDateFormat

object FindDifference {
    fun findDifference (start_date: String?, end_date: String?) : String {
        // SimpleDateFormat converts the
        // string format to date object
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            val d1 = sdf.parse(start_date)
            val d2 = sdf.parse(end_date)

            // Calucalte time difference
            // in milliseconds
            val difference_In_Time = d2.time - d1.time
            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            val difference_In_Seconds = ((difference_In_Time
                    / 1000)
                    % 60)
            val difference_In_Minutes = ((difference_In_Time
                    / (1000 * 60))
                    % 60)
            val difference_In_Hours = ((difference_In_Time
                    / (1000 * 60 * 60))
                    % 24)
            val difference_In_Years = (difference_In_Time
                    / (1000L * 60 * 60 * 24 * 365))
            val difference_In_Days = ((difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365)

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            if(difference_In_Years.toInt() != 0){
                return difference_In_Years.toString() + "년 전"
            }
            else if(difference_In_Days.toInt() > 30){
                return (difference_In_Days.toInt()/30).toString() + "개월 전"
            }
            else if(difference_In_Days.toInt() <= 30){
                return difference_In_Days.toInt().toString() + "일 전"
            }
            else if(difference_In_Hours.toInt() != 0){
                return difference_In_Hours.toInt().toString() + "시간 전"
            }
            else if(difference_In_Minutes.toInt() != 0){
                return difference_In_Minutes.toString() + "분 전"
            }
            else{
                return "방금 전"
            }

        } // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
            return " "
        }
    }
}