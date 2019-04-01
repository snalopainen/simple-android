package org.simple.clinic.search.results

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.ViewHolder
import kotterknife.bindView
import org.simple.clinic.R

sealed class PatientSearchResultsViewHolder(rootView: View) : ViewHolder(rootView) {
  data class Header(val rootView: View) : PatientSearchResultsViewHolder(rootView)

  data class Patient(val rootView: View) : PatientSearchResultsViewHolder(rootView) {
    val genderImageView by bindView<ImageView>(R.id.patientsearchresult_item_gender)
    val titleTextView by bindView<TextView>(R.id.patientsearchresult_item_title)
    val addressTextView by bindView<TextView>(R.id.patientsearchresult_item_address)
    val dateOfBirthTextView by bindView<TextView>(R.id.patientsearchresult_item_dateofbirth)
    val phoneNumberTextView by bindView<TextView>(R.id.patientsearchresult_item_phone)
    val lastBpDateAndFacilityTextView by bindView<TextView>(R.id.patientsearchresults_item_last_bp)
    val lastBpDateFrame by bindView<ViewGroup>(R.id.patientsearchresults_item_last_bp_container)
  }
}
