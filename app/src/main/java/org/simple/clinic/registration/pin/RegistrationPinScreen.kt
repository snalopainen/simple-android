package org.simple.clinic.registration.pin

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotterknife.bindView
import org.simple.clinic.R
import org.simple.clinic.activity.TheActivity
import org.simple.clinic.registration.confirmpin.RegistrationConfirmPinScreenKey
import org.simple.clinic.router.screen.ScreenRouter
import javax.inject.Inject

class RegistrationPinScreen(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

  @Inject
  lateinit var screenRouter: ScreenRouter

  @Inject
  lateinit var controller: RegistrationPinScreenController

  private val backButton by bindView<ImageButton>(R.id.registrationpin_back)
  private val pinEditText by bindView<EditText>(R.id.registrationpin_pin)
  private val pinHintTextView by bindView<TextView>(R.id.registrationpin_pin_hint)
  private val errorTextView by bindView<TextView>(R.id.registrationpin_error)

  @SuppressLint("CheckResult")
  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) {
      return
    }
    TheActivity.component.inject(this)

    backButton.setOnClickListener {
      screenRouter.pop()
    }

    // Because PIN is auto-submitted when 4 digits are entered, restoring the
    // existing PIN will immediately take the user to the next screen.
    pinEditText.isSaveEnabled = false

    Observable.merge(screenCreates(), pinTextChanges(), doneClicks())
        .observeOn(io())
        .compose(controller)
        .observeOn(mainThread())
        .takeUntil(RxView.detaches(this))
        .subscribe { uiChange -> uiChange(this) }
  }

  private fun screenCreates() = Observable.just(RegistrationPinScreenCreated())

  private fun pinTextChanges() =
      RxTextView.textChanges(pinEditText)
          .map(CharSequence::toString)
          .map(::RegistrationPinTextChanged)

  private fun doneClicks() =
      RxTextView
          .editorActions(pinEditText) { it == EditorInfo.IME_ACTION_DONE }
          .map { RegistrationPinDoneClicked() }

  fun showIncompletePinError() {
    pinHintTextView.visibility = View.GONE
    errorTextView.visibility = View.VISIBLE
    errorTextView.text = resources.getString(R.string.registrationpin_error_incomplete_pin)
  }

  fun hideIncompletePinError() {
    pinHintTextView.visibility = View.VISIBLE
    errorTextView.visibility = View.GONE
  }

  fun openRegistrationConfirmPinScreen() {
    screenRouter.push(RegistrationConfirmPinScreenKey())
  }
}
