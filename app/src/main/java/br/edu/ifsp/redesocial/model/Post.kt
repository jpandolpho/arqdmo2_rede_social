package br.edu.ifsp.redesocial.model

import android.graphics.Bitmap

class Post (
    private val descricao: String,
    private val foto: Bitmap,
    private val city: String,
    private val state: String,
    private val owner: String){
    public fun getDescricao() : String{
        return descricao
    }
    public fun getFoto() : Bitmap {
        return foto
    }
    public fun getCity() : String{
        return city
    }
    public fun getState() : String{
        return state
    }
    public fun getOwner() : String{
        return owner
    }
}