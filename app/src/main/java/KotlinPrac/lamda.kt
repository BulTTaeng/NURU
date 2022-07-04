package com.example.nuru

//lamda는 value 처럼 다룰 수 있음
//파라미터로 넣기 가능
//리턴 람다로 가능

//val LamdaName : Type = {argumentList -> codeBody}

val square  : (Int) -> (Int) = {number -> number * number}

val nameAge ={name : String , age : Int -> "myname is $name and I'm $age"}

fun main(){
    println(square(3))
    println(nameAge("choi" , 26))

    println(Pizza("combination"))

    val cheese = "cheese"
    println(cheese.Pizza())

    println(exstring("choi" , 26))

    val l : (Double) ->Boolean = { number -> number > 5 }

    println(invokelamda(l))

    println(invokelamda( {it > 8}) )
    println( invokelamda { it > 8 } ) // if the last parameter of fun is lamda , we can omit ()

    //자바 인터페이스에서 가능
    //그 인터페이스는 하나의 파라미터만 가져야 한다. ex) setClickListener.


}

//확장 함수
//파이썬이나 자바 처럼 .함수(파라미터)로 쓰는 듯
val Pizza : String.() -> String = {this  + " Pizza" }

fun exstring(name : String , age : Int) : String{
    val introduce : String.(Int) -> String = {this + " age is $it" }
    return name.introduce(age)
    //one parameter == it
}

fun invokelamda(l : (Double) -> Boolean) : Boolean{
    return l(5.2343)
}