package com.switchpool.detail;

import java.util.Comparator;

import com.switchpool.model.Note;
import com.switchpool.utility.Utility;

public class DetailNoteComparator implements Comparator<Note> {

	@Override
	public int compare(Note note1, Note note2) {
		String note1time = Utility.shareInstance().paserTimeToYM(note1.getTime()); 
		String note2time = Utility.shareInstance().paserTimeToYM(note2.getTime()); 
		return note1time.compareTo(note2time);
	}
}
