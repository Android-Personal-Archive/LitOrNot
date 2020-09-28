package com.mcs.litornot.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcs.litornot.R
import com.mcs.litornot.dataaccess.di.RetrofitClientSingleton
import com.mcs.litornot.dataaccess.interfaces.IGetResultService
import com.mcs.litornot.dataaccess.model.PhotoResultPOKO
import com.mcs.litornot.dataaccess.model.ResultPOKO
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.venue_swipe_item_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultAdapter(context: Context, private val pokoDataSet: ResultPOKO): RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    var photoUrl: String = ""
    class ResultViewHolder(venueItemView: View): RecyclerView.ViewHolder(venueItemView){
        val ivVenuePhoto: ImageView = venueItemView.iv_venue_photo
        val tvVenueName: TextView = venueItemView.tv_venue_name
        val tvVenueAddress: TextView = venueItemView.tv_venue_address

        val CLIENT_ID = "JUNUL13CM2UL50POLNVE14ZV0GG4D5B0TX3XBXKPGIF0U1UQ"
        val CLIENT_SECRET = "PCXA2BZ3FWYGOK5Y1BNTI0R42P3ISBHYTY0TBZRJRWADJKWC"
        val V = 20200924
        val CATEGORY_ID = "4d4b7105d754a06376d81259"
        val photoService = RetrofitClientSingleton.retrofitInstance?.create(IGetResultService::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val viView: View = LayoutInflater.from(parent.context).inflate(R.layout.venue_swipe_item_layout, parent, false)
        return ResultViewHolder(viView)
    }

    override fun getItemCount(): Int {
        return pokoDataSet.response.venues.size
    }

    // assume USA
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.tvVenueName.text = pokoDataSet.response.venues[position].name
        holder.tvVenueAddress.text = "${pokoDataSet.response.venues[position].location.address?:""}\n" +
                "${pokoDataSet.response.venues[position].location.city?:""}, " +
                "${pokoDataSet.response.venues[position].location.state?:""} " +
                "${pokoDataSet.response.venues[position].location.postalCode?:""}"

        val photoCall = holder.photoService?.getMePhoto(pokoDataSet.response.venues[position].id, holder.CLIENT_ID, holder.CLIENT_SECRET, holder.V)
        photoCall?.enqueue(object: Callback<PhotoResultPOKO>{
            override fun onFailure(call: Call<PhotoResultPOKO>, t: Throwable) {
                Log.e("ResultAdapter", "Error reading JSON")
            }

            override fun onResponse(call: Call<PhotoResultPOKO>, response: Response<PhotoResultPOKO>) {
                val result = response.body()
                if(result == null){
                    Log.w("ResultAdapter", "Response returned null")
                }else{
                    if(result.response.photos.count > 0){
                        val photoURL = result.response.photos.items[0].prefix + "original" + result.response.photos.items[0].suffix
                        Picasso.get().load(photoURL).into(holder.ivVenuePhoto)
                        setPhoto(photoUrl)
                    }
                    else{
                        Log.e("ResultAdapter", "No photos were returned for this venue")
                    }

                }
            }
        })
    }

    fun setPhoto(url: String){
        photoUrl = url
    }

    fun getPhoto(): String{
        return photoUrl
    }
}