package com.mcs.litornot.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcs.litornot.R
import com.mcs.litornot.dataaccess.model.ReviewItemModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.venue_review_item_layout.view.*

class ReviewAdapter(context: Context, private val dataSet: List<ReviewItemModel>): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(reviewItemView: View): RecyclerView.ViewHolder(reviewItemView) {
        val ivPhotoReview = reviewItemView.iv_venue_photo_review
        val tvNameReview = reviewItemView.tv_venue_name_review
        val tvAddressReview = reviewItemView.tv_venue_address_review
        val ivLitMood = reviewItemView.iv_lit_mood
        val tvLitRatio = reviewItemView.tv_lit_ratio
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val riView: View = LayoutInflater.from(parent.context).inflate(R.layout.venue_review_item_layout, parent, false)
        return ReviewAdapter.ReviewViewHolder(riView)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.tvNameReview.text = dataSet[position].name
        holder.tvAddressReview.text = dataSet[position].address
        val ratio: Float = dataSet[position].litVotes.toFloat()/dataSet[position].votes.toFloat()
        holder.tvLitRatio.text = ratio.toString()

        when{
            ratio < 0.3f -> {holder.ivLitMood.setImageResource(R.drawable.ic_baseline_mood_bad_24)}
            ratio > 0.3f && ratio < 0.5f -> {holder.ivLitMood.setImageResource(R.drawable.ic_baseline_sentiment_dissatisfied_24)}
            ratio > 0.5f && ratio < 0.7f -> {holder.ivLitMood.setImageResource(R.drawable.ic_baseline_sentiment_satisfied_24)}
            ratio > 0.7f -> {holder.ivLitMood.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24)}
            else -> {}
        }

        if(!dataSet[position].photoUrl.isEmpty())
            Picasso.get().load(dataSet[position].photoUrl).into(holder.ivPhotoReview)
        else
            holder.ivPhotoReview.setImageResource(R.mipmap.a01f45af87a9c0d09cb8e8888195204e)
    }
}