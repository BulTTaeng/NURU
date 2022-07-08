package com.example.nuru.utility

import android.content.Context

class GetCurrentContext private constructor(){
    companion object{
        @Volatile private var instance: GetCurrentContext? = null

        @JvmStatic fun getInstance(): GetCurrentContext =
            instance ?: synchronized(this) {
                instance ?: GetCurrentContext().also {
                    instance = it
                }
            }
    }
    var currentContext : Context? = null

    fun setcurrentContext(c : Context){
        currentContext = c
    }

    fun getcurrentContext() : Context? {
        return currentContext
    }

}