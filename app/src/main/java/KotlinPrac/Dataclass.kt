package com.example.nuru

data class Ticket(val companyname : String , val name : String, var date : String, var seatnum : Int )
//toString() , hashCode(), equals() , copy() has automatically made.

class _Ticket(val companyname : String , val name : String, var date : String, var seatnum : Int )

fun main(){
    val ticket = Ticket("Asia"  , "Choi" , "2-13" , 31)
    val _ticket = _Ticket("Asia"  , "Choi" , "2-13" , 31)

    println(ticket) // toString
    println(_ticket) // memory address

}