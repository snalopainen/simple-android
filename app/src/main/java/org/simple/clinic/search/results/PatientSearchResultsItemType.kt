package org.simple.clinic.search.results

import com.xwray.groupie.Item
import org.simple.clinic.R
import org.simple.clinic.facility.Facility
import org.simple.clinic.patient.PatientSearchResult

sealed class PatientSearchResultsItemType : Item<PatientSearchResultsViewHolder>() {

  data class Header(
      val stringResId: Int,
      val arguments: List<String>
  ) : PatientSearchResultsItemType() {

    override fun getLayout(): Int = R.layout.list_patient_header

    override fun bind(viewHolder: PatientSearchResultsViewHolder, position: Int) {
      viewHolder as PatientSearchResultsViewHolder.Header
    }
  }

  data class Patient(
      val patientSearchResult: PatientSearchResult,
      val currentFacility: Facility
  ) : PatientSearchResultsItemType() {

    override fun getLayout(): Int = R.layout.list_patient_search

    override fun bind(viewHolder: PatientSearchResultsViewHolder, position: Int) {
      viewHolder as PatientSearchResultsViewHolder.Patient
    }
  }
}
