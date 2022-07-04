package com.example.nuru

// = static

class Book private constructor (val id : Int, val name : String) {

    companion object Factory : idprovider{

        override fun getId(): Int {
            return 444
        }

        val  new = "new choi"
        fun create() = Book(getId(), new)
    }

}

interface idprovider{
    fun getId() : Int
}

fun main(){
    val book = Book.create()
    val bookid = Book.Factory.getId()
    println("id is ${book.id} and name is ${book.name}")
}