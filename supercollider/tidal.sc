SuperDirt.start;

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

//~dirt.soundLibrary.addMIDI(\r5, MIDIOut.new(~interfacemidi));
~dirt.soundLibrary.addMIDI(\vir, ~vmidi);