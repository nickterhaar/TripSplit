package com.nickterhaar.tripsplit.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nickterhaar.tripsplit.R
import com.nickterhaar.tripsplit.models.Trip

class TripListAdapter(val context: Activity, val list: List<Trip>) : ArrayAdapter<Trip>
    (context, R.layout.trip_item, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.trip_item, null)

        val tripTitle = view.findViewById<TextView>(R.id.tripTitle)
        val tripDate = view.findViewById<TextView>(R.id.tripDate)

        val trip = list.elementAt(position)

        tripTitle.setText(trip.title)
        tripDate.setText(trip.endDate)

        return view
    }

    override fun getItemId(position: Int): Long {
        return list.elementAt(position).uid!!.toLong()
    }
}