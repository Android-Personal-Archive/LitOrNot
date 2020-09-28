package com.mcs.litornot.dataaccess.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ReviewItemModel(val id: String, val name: String, val address: String, val photoUrl: String, val litVotes: Int, val votes: Int){
    constructor(): this("","","", "", 0,0)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "address" to address,
            "photoUrl" to photoUrl,
            "litVotes" to litVotes,
            "votes" to votes
        )
    }
}
