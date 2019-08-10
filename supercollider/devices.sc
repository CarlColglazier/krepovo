LinkClock

l = LinkClock(1).latency_(Server.default.latency);

(
var win = Window("LinkClock", Rect(200, 200, 500, 100)).front,
peersBox, tempoBox, barsBox, beatsBox,
font = Font.default.copy.size_(32),
boldFont = font.boldVariant,
controller, task;

win.layout = HLayout(
    StaticText().font_(font).string_("Peers:"),
    peersBox = NumberBox().font_(boldFont).align_(\center).fixedWidth_(80),
    StaticText().font_(font).string_("Tempo:"),
    tempoBox = NumberBox().font_(boldFont).align_(\center).fixedWidth_(120),
    StaticText().font_(font).string_("Now:"),
    barsBox = NumberBox().font_(boldFont).align_(\center).fixedWidth_(80),
    beatsBox = NumberBox().font_(boldFont).align_(\center).fixedWidth_(80)
);

[peersBox, barsBox, beatsBox].do { |view| view.enabled_(false) };

tempoBox.action = { |view| l.tempo = view.value / 60 };
tempoBox.value = l.tempo * 60;
peersBox.value = l.numPeers;

task = Routine {
    var bars, beats;
    loop {
        bars = l.bar;
        beats = l.beatInBar;
        {
            barsBox.value = bars;
            beatsBox.value = beats;
        }.defer(l.latency);
        1.0.wait;
    }
}.play(l, quant: 1);

controller = SimpleController(l)
.put(\tempo, {
    defer { tempoBox.value = l.tempo * 60 }
})
.put(\numPeers, {
    defer { peersBox.value = l.numPeers }
})
.put(\stop, { defer { win.close } });

win.onClose = { task.stop; controller.remove };
)

(
~m = MonoM.new("/monome", 0);
//~status = Array2D.new(8,8);
~status = { { 0 }.dup(8) }.dup(8);
MIDIClient.init(1, 1);
~midi = MIDIOut(0);
s.waitForBoot({
	~m.useDevice(0);
	~m.ledall(1);
	~m.ledall(0);
	OSCdef(\test, {|msg, time, addr, recvPort|
		var x = msg[1];
		var y = msg[2];

		msg.postln;
		if (msg[3] == 1) {
			~midi.noteOn(0, 60 + y, 100);
			~status[x][y] = abs(~status[x][y] - 1);
		} {
			~midi.noteOff(0, 60 + y, 0);
		}
	}, '/monome/grid/key', n); // def style
})
)
(
r = Routine({
	var counter = 0;
	var current;
	loop {
		current = counter % 8;

		for (0, 7, { arg x;
			for (0, 7, { arg y;
				~m.ledset(x, y, ~status[x][y]);
			});
		});

		for (0, 7, { arg note;
			~m.ledset(current, note, 1);
			if (~status[current][note] == 1) {
				~midi.noteOn(0, 60 + note, 100);
				~m.ledset(current, note, 0);
			};

		});
		0.25.wait;
		~midi.allNotesOff(0);
		counter = counter + 1;
	}
})
)
r.play(l, quant: 1);

r.stop;

l.tempo

(
t = Task({
	var	synth;
	loop {
		#[60, 62, 64, 65, 67, 65, 64, 62].do({ |note|
			s.makeBundle(0.2, {
				synth = Synth(\default, [freq: note.midicps])
			});
			s.makeBundle(0.4, {
				synth.release;
			});
			0.25.wait;
		});
	}

}).play(l, quant: 1);
)

l.tempo = 124/60;

(
q = nil;
~c = SimpleController(l)
.put(\linkStart, {
	"start".postln;
    if(q.isNil) {
		q = r.play(l, quant: 1);
    }
})
.put(\linkStop, {
    q.stop;
    q = nil;
})
.put(\stop, { c.remove });  // clean up if clock stops
)
~test = r.play(l, quant: 1);

~test

SynthDescLib.global.read;
(
z = Pbind(\midinote, Pseq([60, 62, 64, 65, 67, 65, 64, 62], inf),
	\delta, 0.25, \dur, 0.25, \instrument, \default)
	.play(l, quant:4);
)

t.stop;

s.freeAll;


x= Synth.new("default");
x.free;