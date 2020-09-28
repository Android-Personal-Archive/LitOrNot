package com.mcs.litornot.dataaccess.interfaces

import com.mcs.litornot.dataaccess.model.PhotoResultPOKO
import com.mcs.litornot.dataaccess.model.ResultPOKO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
https://api.foursquare.com/v2/venues/search?client_id=JUNUL13CM2UL50POLNVE14ZV0GG4D5B0TX3XBXKPGIF0U1UQ&client_secret=PCXA2BZ3FWYGOK5Y1BNTI0R42P3ISBHYTY0TBZRJRWADJKWC&v=20200921&ll=33.909265, -84.479227&categoryId=4d4b7105d754a06376d81259
 */

private const val RESULT_ENDPOINT_URL = "v2/venues/search"
private const val PHOTO_ENDPOINT_URL = "v2/venues"

interface IGetResultService {
    @GET(RESULT_ENDPOINT_URL)
    fun getMeResult(
        @Query("client_id") clientIdParam: String,
        @Query("client_secret") clientSecretParam: String,
        @Query("v") versionParam: Int,
        @Query("ll") llParam: String,
        @Query("categoryId") categoryIdParam: String
    ): Call<ResultPOKO>

    @GET("${PHOTO_ENDPOINT_URL}/{venueId}/photos")
    fun getMePhoto(
        @Path("venueId") id: String,
        @Query("client_id") clientIdParam: String,
        @Query("client_secret") clientSecretParam: String,
        @Query("v") versionParam: Int
    ): Call<PhotoResultPOKO>
}