/*
 *  Phonk Example: BluetoothLE
 *  Bluetooth Low Energy is a bit complex protocol therefore Phonk only provides
 *  a subset of what is possible
 *
 *  The code should to connect a ESP32 with BLE using the ESP32 Example located at
 *  ESP32 BLE Arduino -> UART
 *
 *  The flow to get data from a BLE device is
 *    Connect to Device -> Connect to a Service -> Read from a characteristic -> Parse the bytes into readable data
 *
 *  The flow to write data to a BLE devie is
 *    Connect to Device -> Connect to a Service -> Write to a characteristic
 */

ui.addTitle(app.name)
network.bluetoothLE.start()

var bleClient = network.bluetoothLE.createClient()

// -> Write here the BLE device MAC Address
var deviceMacAddress = ''
var serviceUUID = '6E400001-B5A3-F393-E0A9-E50E24DCCA9E'
var characteristicUUID_TX = '6e400002-b5a3-f393-e0a9-e50e24dcca9e'
var characteristicUUID_RX = '6e400003-b5a3-f393-e0a9-e50e24dcca9e'

network.bluetoothLE.start()

var txt = ui.addTextList(0.1, 0.1, 0.8, 0.55).autoScroll(true)
txt.props.textSize = 15

// scan bluetooth low energy networks
// this should show devices -> services -> characteristics
ui.addToggle(['Scan bluetooth', 'Stop scan'], 0.1, 0.7, 0.4, 0.1).onChange(function (e) {
  if (e.checked) {
    network.bluetoothLE.scan(function (data) {
      txt.add(data.name + ' ' + data.rssi + ' ' + data.mac)
    })
  } else {
    network.bluetoothLE.stopScan()
  }
})

// connect to a device
ui.addToggle(['Connect', 'Disconnect'], 0.55, 0.7, 0.35, 0.1).onChange(function (e) {
  if (e.checked) {
    bleClient.connectDevice(deviceMacAddress)
  } else {
    bleClient.disconnectDevice(deviceMacAddress)
  }
})

// send bluetooth messages
var input = ui.addInput(0.1, 0.85, 0.55, 0.1).hint('message')
var send = ui.addButton('Send', 0.7, 0.85, 0.2, 0.1).onClick(function () {
  bleClient.write(input.text(), deviceMacAddress, serviceUUID, characteristicUUID_TX)
})

bleClient.onNewDeviceStatus(function (e) {
  // console.log('onNewDeviceStatus', e)
  // double check if is the device we want to connect
  if (e.deviceMac === deviceMacAddress && e.status === 'connected') {
    bleClient.readFromCharacteristic(deviceMacAddress, serviceUUID, characteristicUUID_RX)
    txt.add('connected to ' + e.deviceName)
  }
})

bleClient.onNewData(function (e) {
  var value = util.parseBytes(e.value, 'uint8')
  // console.log('(' + e.deviceName + ') ' + e.deviceMac + '/' + e.serviceUUID + '/' + e.characteristicUUID + ' --> ')
  txt.add(value)
})
