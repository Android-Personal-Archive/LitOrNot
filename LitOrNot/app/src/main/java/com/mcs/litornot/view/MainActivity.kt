package com.mcs.litornot.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mcs.litornot.R
import com.mcs.litornot.dataaccess.di.RetrofitClientSingleton
import com.mcs.litornot.dataaccess.interfaces.IGetResultService
import com.mcs.litornot.dataaccess.model.ResultPOKO
import com.mcs.litornot.dataaccess.model.ReviewItemModel
import com.mcs.litornot.view.fragment.BaseFragment
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val CLIENT_ID = "JUNUL13CM2UL50POLNVE14ZV0GG4D5B0TX3XBXKPGIF0U1UQ"
private const val CLIENT_SECRET = "PCXA2BZ3FWYGOK5Y1BNTI0R42P3ISBHYTY0TBZRJRWADJKWC"
private const val V = 20200924
private const val CATEGORY_ID = "4d4b7105d754a06376d81259"

class MainActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val dbReference = database.reference.child("reviews")
    var reviewItemList: MutableList<ReviewItemModel> = emptyList<ReviewItemModel>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val LL = intent.getStringExtra("LatLng")?:""
        val resultService = RetrofitClientSingleton.retrofitInstance?.create(IGetResultService::class.java)
        val resultCall = resultService?.getMeResult(clientIdParam = CLIENT_ID, clientSecretParam = CLIENT_SECRET, versionParam = V, llParam = LL, categoryIdParam = CATEGORY_ID)

        var fragment: Fragment = BaseFragment()

        bn_fragment_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.mi_swipe -> {
                    csv_swipe.visibility = View.VISIBLE
                    rv_reviews.visibility = View.GONE
                    callApi(resultCall)
                }
                R.id.mi_review -> {
                    csv_swipe.visibility = View.GONE
                    rv_reviews.visibility = View.VISIBLE

                    dbReference.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelled:", "A cancellation error occurred.")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                for(r in snapshot.children){
                                    val review = r.getValue(ReviewItemModel::class.java)
                                    reviewItemList.add(review!!)
                                }
                            }
                        }
                    })

                    if(reviewItemList == null){
                        Log.w(localClassName, "List returned null")
                    }else{
                        val adapter = ReviewAdapter(this@MainActivity, reviewItemList)
                        rv_reviews.layoutManager = LinearLayoutManager(this@MainActivity)
                        rv_reviews.adapter = adapter
                        rv_reviews.setHasFixedSize(true)
                    }
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.fl_fragment_container, fragment).commit()
            true
        }

        callApi(resultCall)
        supportFragmentManager.beginTransaction().replace(R.id.fl_fragment_container, fragment).commit()
    }

    private fun callApi(call: Call<ResultPOKO>?){
        call?.clone()?.enqueue(object: Callback<ResultPOKO>{
            override fun onFailure(call: Call<ResultPOKO>, t: Throwable) {
                Log.e(localClassName, "Error reading JSON")
            }

            override fun onResponse(call: Call<ResultPOKO>, response: Response<ResultPOKO>) {
                val results = response.body()
                if(results == null){
                    Log.w(localClassName, "Response returned null")
                }
                else
                {
                    val adapter = ResultAdapter(this@MainActivity, results)
                    val manager = CardStackLayoutManager(this@MainActivity, object: CardStackListener{

                        var didSwipeUp: Boolean = false
                        override fun onCardDisappeared(view: View?, position: Int) {
                            Log.d(localClassName, "onCardDisappeared: pos=${position}")

                            dbReference.addValueEventListener(object: ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("onCancelled:", "A cancellation error occurred.")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for(r in snapshot.children){
                                            val review = r.getValue(ReviewItemModel::class.java)
                                            reviewItemList.add(review!!)
                                        }
                                    }
                                }
                            })

                            if(didSwipeUp){
                                var foundReviewItem: Boolean = false
                                for(i in reviewItemList?:emptyList<ReviewItemModel>()){
                                    if(results.response.venues[position].id == i.id){
                                        Log.e("Found:", "Updating old review item on swipe up")
                                        foundReviewItem = true

                                        var litVotes = i.litVotes+1
                                        var votes = i.votes+1

                                        var map = mutableMapOf<String, Any>()
                                        map["litVotes"] = litVotes
                                        map["votes"] = votes
                                        dbReference.child(i.id).updateChildren(map)
                                    }
                                }

                                if(!foundReviewItem){
                                    Log.e("Not Found:", "Creating new review item on swipe up")
                                    var map = mutableMapOf<String, Any>()
                                    map["id"] = results.response.venues[position].id
                                    map["name"] = results.response.venues[position].name
                                    map["address"] = results.response.venues[position].location.address+"\n"+results.response.venues[position].location.city+", "+results.response.venues[position].location.state+" "+results.response.venues[position].location.postalCode
                                    map["photoUrl"] = adapter.getPhoto()
                                    map["litVotes"] = 1
                                    map["votes"] = 1
                                    dbReference.child(results.response.venues[position].id).setValue(map)
                                }
                            }else{
                                var foundReviewItem: Boolean = false
                                for(i in reviewItemList?:emptyList<ReviewItemModel>()){
                                    if(results.response.venues[position].id == i.id){
                                        Log.e("Found:", "Updating old review item")
                                        foundReviewItem = true

                                        var votes = i.votes+1

                                        var map = mutableMapOf<String, Any>()
                                        map["votes"] = votes
                                        dbReference.child(i.id).updateChildren(map)
                                    }
                                }

                                if(!foundReviewItem){
                                    Log.e("Not Found:", "Creating new review item")
                                    var map = mutableMapOf<String, Any>()
                                    map["id"] = results.response.venues[position].id
                                    map["name"] = results.response.venues[position].name
                                    map["address"] = results.response.venues[position].location.address+"\n"+results.response.venues[position].location.city+", "+results.response.venues[position].location.state+" "+results.response.venues[position].location.postalCode
                                    map["photoUrl"] = adapter.getPhoto()
                                    map["litVotes"] = 0
                                    map["votes"] = 1
                                    dbReference.child(results.response.venues[position].id).setValue(map)
                                }
                            }
                        }

                        override fun onCardDragging(direction: Direction?, ratio: Float) {
                            Log.d(localClassName, "onCardDragging: dir="+direction?.name+ " ratio="+ratio)
                            when(direction){
                                Direction.Top -> {
                                    didSwipeUp = true
                                }
                                Direction.Bottom -> {
                                    didSwipeUp = false
                                }
                                else -> {}
                            }
                        }

                        override fun onCardSwiped(direction: Direction?) {
                            Log.d(localClassName, "onCardSwiped: dir="+direction)
                            when(direction){
                                Direction.Top -> {
                                    didSwipeUp = true
                                    Toast.makeText(this@MainActivity, "It's LIT", Toast.LENGTH_SHORT).show()
                                }
                                Direction.Bottom -> {
                                    didSwipeUp = false
                                    Toast.makeText(this@MainActivity, "It's NOT", Toast.LENGTH_SHORT).show()
                                }
                                else -> {Log.wtf(localClassName, "That wasn't supposed to happen")}
                            }
                        }

                        override fun onCardCanceled() {
                            Log.d(localClassName, "onCardCancelled:")
                        }

                        override fun onCardAppeared(view: View?, position: Int) {
                            Log.d(localClassName, "onCardAppeared:")
                        }

                        override fun onCardRewound() {
                            Log.d(localClassName, "onCardRewound:")
                        }
                    })

                    manager.setStackFrom(StackFrom.None)
                    manager.setVisibleCount(3)
                    manager.setTranslationInterval(8.0f)
                    manager.setScaleInterval(0.95f)
                    manager.setSwipeThreshold(0.3f)
                    manager.setMaxDegree(20.0f)
                    manager.setDirections(arrayListOf(Direction.Top, Direction.Bottom))
                    manager.setCanScrollHorizontal(true)
                    manager.setSwipeableMethod(SwipeableMethod.Manual)
                    manager.setOverlayInterpolator(LinearInterpolator())

                    csv_swipe.layoutManager = manager
                    csv_swipe.adapter = adapter
                    csv_swipe.setHasFixedSize(true)
                    csv_swipe.itemAnimator = DefaultItemAnimator()
                }
            }
        })
    }
}