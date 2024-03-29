s.boot;

(
// configure the sound server: here you could add hardware specific options
// see http://doc.sccode.org/Classes/ServerOptions.html
s.options.numBuffers = 1024 * 256; // increase this if you need to load more samples
s.options.memSize = 8192 * 32; // increase this if you get "alloc failed" messages
s.options.maxNodes = 1024 * 32; // increase this if you are getting drop outs and the message "too many nodes"
s.options.numOutputBusChannels = 12; // set this to your hardware output channel size, if necessary
s.options.numInputBusChannels = 2; // set this to your hardware output channel size, if necessary
// boot the server and start SuperDirt
s.waitForBoot {
	~dirt = SuperDirt(2, s); // two output channels, increase if you want to pan across more channels
	~dirt.loadSoundFiles;   // load samples (path containing a wildcard can be passed in)
	// for example: ~dirt.loadSoundFiles("/Users/myUserName/Dirt/samples/*");
	// s.sync; // optionally: wait for samples to be read
	~dirt.start(57120, [0, 2, 4, 6, 8, 10]);
	// optional, needed for convenient access from sclang:
	(
		~d1 = ~dirt.orbits[0]; ~d2 = ~dirt.orbits[1]; ~d3 = ~dirt.orbits[2];
		~d4 = ~dirt.orbits[3]; ~d5 = ~dirt.orbits[4]; ~d6 = ~dirt.orbits[5];
		~d7 = ~dirt.orbits[6]; ~d8 = ~dirt.orbits[7]; ~d9 = ~dirt.orbits[8];
		~d10 = ~dirt.orbits[9]; ~d11 = ~dirt.orbits[10]; ~d12 = ~dirt.orbits[11];
	);

	(
		MIDIClient.init;
		//MIDIClient.destinations;
		//  Connect to the virtual MIDI.
		~virmidi = MIDIOut.findPort("Virtual Raw MIDI 3-0", "VirMIDI 3-0");
		MIDIOut.connect(0, ~virmidi);
		// Connect to interface MIDI. 
		~interfacemidi = MIDIOut.findPort("Scarlett 18i8 USB", "Scarlett 18i8 USB MIDI 1");
		MIDIOut.connect(1, ~interfacemidi);
		// Need this in the type SuperDirt expects
		~vmidi = MIDIOut.newByName("Virtual Raw MIDI 3-0", "VirMIDI 3-0");
		~vmidi.latency = 0.000001;
		~imidi = MIDIOut.newByName("Scarlett 18i8 USB", "Scarlett 18i8 USB MIDI 1");
		~imidi.latency = 0.01;
		~dirt.soundLibrary.addMIDI(\r5, ~imidi);
		~dirt.soundLibrary.addMIDI(\vir, ~vmidi);
		~dirt.loadSoundFiles("/home/carl/Bitwig Studio/Projects/gec2u/bounce/")
	);
};

s.latency = 0.05;
);


