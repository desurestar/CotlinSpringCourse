package com.example.front.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Long,
    @SerializedName("post_name")
    val postName: String
)
