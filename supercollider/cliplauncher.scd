~m = MonoM.new("/monome", 0);

~m.useDevice(1);
~m.ledall(0);
~m.ledall(1);
~m.ledall(0);
n = NetAddr.new("127.0.0.1", 8000);    // create the NetAddr

(
j = OSCdef(\select, {|msg, time, addr, recvPort|
	var x = msg[1];
	var y = msg[2];
	var mess = '/track/'++(x+1)++'/clip/'++(y+1)++'/launch';
	if (msg[3] == 1) {
		mess.postln;
		b.sendMsg(mess);
	}
}, '/monome/grid/key');
)

(
for (0, 7, { arg i;
	for (0, 7, {arg j;
		var mess = '/track/'++(i+1)++'/clip/'++(j+1)++'/hasContent';
		// /track/{1-8}/clip/{1-8}/hasContent
		OSCdef('\content'++i++j, {|msg, time, addr, recvPort|
			msg.postln;
			~m.ledset(i, j, msg[1]);
		}, mess, recvPort: 9000);
	})
})
)
