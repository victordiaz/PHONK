/*
 * \\\ Example: mDNS
 *
 * Register/discover network services using mDns
 */

ui.addTitle(app.name)

var mDNSdiscover = network.mDNS.discover('_myServiceType._tcp.')
mDNSdiscover.onNewData(function (e) {
  console.log(e);
})

var mDNSservice = network.mDNS.register('myService', '_myServiceType._tcp.', 2020)
mDNSservice.onNewData(function (e) {
  console.log(e)
})

ui.addButton('Be discoverable', 0.1, 0.45, 0.35, 0.1).onClick(function (e) {
  mDNSdiscover.start()
})

ui.addButton('Discover Services', 0.55, 0.45, 0.35, 0.1).onClick(function (e) {
  mDNSservice.start()
})
