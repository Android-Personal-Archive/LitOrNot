package com.mcs.litornot.dataaccess.model

data class ResultPOKO(val meta: MetaPOKO, val response: ResponsePOKO)
data class MetaPOKO(val code: Int, val requestId: String)
data class ResponsePOKO(val venues: List<VenuePOKO>)
data class VenuePOKO(val id: String, val name: String, val location: LocationPOKO)
data class LocationPOKO(val address: String,
                        val crossStreet: String,
                        val lat: Double,
                        val lng: Double,
                        val distance: Int,
                        val postalCode: String,
                        val cc: String,
                        val city: String,
                        val state: String,
                        val country: String)