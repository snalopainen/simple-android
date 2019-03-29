package org.simple.clinic.addidtopatient

/**
 * We need to render items of [AddIdToPatient] type in the UI, sometimes differently from how
 * they are represented in code.
 *
 * For example, the short of a [AddIdToPatient.BpPassport] is represented as a 7-digit string,
 * ex ("1234567"), but when rendering it in the UI we need to display it as "123 4567". We do not
 * want to store these strings with the spaces in business layer because this is a presentation
 * detail.
 *
 * This class is a helper class that is meant to convert [AddIdToPatient] instances to their display
 * formats.
 **/
object AddIdToPatientStringCreator {

  fun displayString(addIdToPatient: AddIdToPatient): String {
    return when (addIdToPatient) {
      is AddIdToPatient.BpPassport -> displayStringForBpPassportShortCode(addIdToPatient.bpPassportShortCode)
    }
  }

  private fun displayStringForBpPassportShortCode(bpPassportShortCode: String): String {
    // BpPassport shortcodes are guaranteed to be exactly seven characters in length.
    // This method needs to convert a 7-character string to a string with the first 3 and the last
    // 4 characters separated by a space.
    val prefix = bpPassportShortCode.substring(0, 3)
    val suffix = bpPassportShortCode.substring(3)

    return "$prefix $suffix"
  }
}
