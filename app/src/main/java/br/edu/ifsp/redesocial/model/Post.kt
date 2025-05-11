package br.edu.ifsp.redesocial.model

import android.graphics.Bitmap

class Post (private val descricao: String, private val foto: Bitmap, private val localizacao: String, private val owner: String){
    public fun getDescricao() : String{
        return descricao
    }
    public fun getFoto() : Bitmap {
        return foto
    }
    public fun getLocalizacao() : String{
        return localizacao
    }
    public fun getOwner() : String{
        return owner
    }
}