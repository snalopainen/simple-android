package org.simple.clinic.patient

data class PatientSearchResults(
    val patientsInCurrentFacility: List<PatientSearchResult>,
    val patientsInOtherFacilities: List<PatientSearchResult>
)
