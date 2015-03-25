/*
* Register/discover network services  using mDns
* android > 4.1 
*
*/

network.discoverServices("_qq5._tcp", function(s, data) {
    if (data != null) {
        console.log(data);
        console.log(data.getServiceName());
    }
});

network.registerService("qq1", "_qq5._tcp", 2020, function(n, s) { 
    console.log(n, s)
});
