/*
 * PHONK Example: SMS
 *
 * Description Send and receive a SMS.
 * Requires the extended version of Phonk.
 */

ui.addTitle(app.name);

var phoneNumber

const receivedSmsDetails = ui.addText(0, 0, 1, 0.5)

ui.popup()
  .title('Enter the phone number to send an SMS to')
  .ok('ok')
  .input('phone number')
  .onAction(function(phoneNumberInput) {
    phoneNumber = phoneNumberInput.answer
  })
  .show()

// these methods are defined in phonk_apprunner/src/main/java/io/phonk/runner/apprunner/api/PDevice.java
device.onSmsReceived(function(sms) {
  receivedSmsDetails.text('from: ' + sms.from + "\n" + 'message: ' + sms.message)
})

ui.addButton('send sms', 0.1, 0.5, 0.8, 0.4).onClick(function() {
  device.smsSend(phoneNumber, 'hello world!');
})