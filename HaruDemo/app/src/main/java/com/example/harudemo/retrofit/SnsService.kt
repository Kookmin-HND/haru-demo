package com.example.harudemo.retrofit

import com.example.harudemo.utils.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SnsService {
    //url:    http://10.0.2.2/api/posts/recent/{postId}
    @GET(API.RECENT_POSTS)
    fun getPosts(
        @Path("postId") id:Int
    ) : Call<JsonElement>



//    예시
//    @GET(API.SEARCH_PHOTOS)
//    fun searchPhotos(@Query("query") searchTerm: String) : Call<JsonElement>
//
//    @GET(API.SEARCH_USERS)
//    fun searchUsers(@Query("query") searchTerm: String) : Call<JsonElement>

}