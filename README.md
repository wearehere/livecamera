# Brief

Live Camera is the system creating stream file from android device's camera 

then upload the server,  and generate m3u8 playlist for flash/safari/vplayer


# why I do this

I work on one hardware encoder which upload stream data to server for live broadcast years ago

I always think the hardware can be replaced by smart phone with camera

And finally, I work out one demo system on Nexus 5 with Android 5.1

I try to contact previous colleges to make it a real product, but everyone has their own business

And I have no resource to do it with this demo.

So I decice to publish the demo code to github and think may be it will help somebody

#Technical

Refer to Spydroid project, you can get h264 frames encoded by hardware encoder from Nexus 5

Originally, the data is streaming to server by <em>RTP</em> protocol which require <em>RTP</em> receiver in server

The demo compose the data into flv frame instead and create file segments. The it use <em>HTTP</em> to transfer data to server which mean you  can use any cloud service with HTTP post capability

And you can make create m3u8 for player now.

All the process is based on HTTP, you can use and CDN to publish the content

#business

Low price for make large scale live broadcast

* no paticular hardware required, phone is enough
* no specially protocol used, HTTP only
* jus use anything with HTTP optimization, such as CDN/LB/STORAGE


#Wish

* make one complete system, such as Android APP, full server
* make ios app
* anything interesting...