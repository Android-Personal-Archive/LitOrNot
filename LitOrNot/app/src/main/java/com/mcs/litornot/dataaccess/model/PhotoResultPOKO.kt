package com.mcs.litornot.dataaccess.model

data class PhotoResultPOKO(val meta: MetaPOKO, val response: PhotoResponsePOKO)
data class PhotoResponsePOKO(val photos: PhotoPOKO)
data class PhotoPOKO(val count: Int, val items: List<PhotoItemPOKO>)
data class PhotoItemPOKO(val id: String,
                         val createdAt: Long,
                         val source: SourcePOKO,
                         val prefix: String,
                         val suffix: String,
                         val width: Int,
                         val height: Int)
data class SourcePOKO(val name: String, val url: String)