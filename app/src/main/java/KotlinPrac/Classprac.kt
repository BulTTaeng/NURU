package com.example.nuru

class Classprac{

}

open class Human (var name : String  = "no name"){

    init{
        println("new human!")
    } // init always run first

    constructor(name : String, age : Int) : this(){
        println("my name is $name , $age old")
    }

    fun cake(){
        println("sweet")
    }

    open fun sing(){
        println("sing")
    }
}

class Korean(name :String , aa : Int) : Human(name){

    override fun sing(){
        super.sing()
        println("Korean song")
        println("my name is $name")
    }
}



fun main(){
//    val human = Human("choi")
//    human.cake()
//    println(human.name)
//
//    val human2 = Human()
//    println(human2.name)
//
//    val human3 = Human("choi2" , 12)

    val k = Korean("Jay" , 23)
    k.sing()

}