package org.simple.clinic.patient.recent

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.ofType
import org.simple.clinic.ReplayUntilScreenIsDestroyed
import org.simple.clinic.ReportAnalyticsEvents
import org.simple.clinic.facility.FacilityRepository
import org.simple.clinic.patient.PatientConfig
import org.simple.clinic.patient.PatientRepository
import org.simple.clinic.summary.RelativeTimestampGenerator
import org.simple.clinic.user.UserSession
import org.simple.clinic.util.UtcClock
import org.simple.clinic.util.estimateCurrentAge
import org.simple.clinic.widgets.ScreenCreated
import org.simple.clinic.widgets.UiEvent
import javax.inject.Inject

typealias Ui = RecentPatientsView
typealias UiChange = (Ui) -> Unit

class RecentPatientsViewController @Inject constructor(
    private val userSession: UserSession,
    private val patientRepository: PatientRepository,
    private val facilityRepository: FacilityRepository,
    private val relativeTimestampGenerator: RelativeTimestampGenerator,
    private val utcClock: UtcClock,
    private val patientConfig: Observable<PatientConfig>
) : ObservableTransformer<UiEvent, UiChange> {

  override fun apply(events: Observable<UiEvent>): ObservableSource<UiChange> {
    val replayedEvents = ReplayUntilScreenIsDestroyed(events)
        .compose(ReportAnalyticsEvents())
        .replay()

    return Observable.mergeArray(showRecentPatients(replayedEvents))
  }

  private fun showRecentPatients(events: Observable<UiEvent>): Observable<UiChange> {
    val currentFacilityStream = events.ofType<ScreenCreated>()
        .flatMap { userSession.requireLoggedInUser() }
        .switchMap { facilityRepository.currentFacility(it) }
        .replay()
        .refCount()

    val recentPatientsStream = Observables
        .combineLatest(currentFacilityStream, patientConfig)
        .switchMap { (facility, config) ->
          patientRepository.recentPatients(facility.uuid, limit = config.recentPatientLimit)
        }
        .replay()
        .refCount()

    val updateRecentPatients = recentPatientsStream
        .map { it.map(::recentPatientItem) }
        .map { { ui: Ui -> ui.updateRecentPatients(it) } }

    val toggleEmptyState = recentPatientsStream
        .map { it.isNotEmpty() }
        .map { hasRecentPatients ->
          when {
            hasRecentPatients -> { ui: Ui -> ui.hideEmptyState() }
            else -> { ui: Ui -> ui.showEmptyState() }
          }
        }

    return Observable.merge(updateRecentPatients, toggleEmptyState)
  }

  private fun recentPatientItem(recentPatient: RecentPatient) =
      RecentPatientItem(
          uuid = recentPatient.uuid,
          name = recentPatient.fullName,
          age = age(recentPatient),
          lastBp = recentPatient.lastBp?.run {
            RecentPatientItem.LastBp(
                systolic = systolic,
                diastolic = diastolic,
                updatedAtRelativeTimestamp = relativeTimestampGenerator.generate(createdAt)
            )
          },
          gender = recentPatient.gender
      )

  private fun age(recentPatient: RecentPatient): Int =
      when (recentPatient.age) {
        null -> estimateCurrentAge(recentPatient.dateOfBirth!!, utcClock)
        else -> {
          val (recordedAge, ageRecordedAtTimestamp, _) = recentPatient.age
          estimateCurrentAge(recordedAge, ageRecordedAtTimestamp, utcClock)
        }
      }
}
